package com.example.projekmobile_kel11.ui.prediction

import android.app.Dialog
import android.os.Bundle
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import ai.onnxruntime.OnnxTensor
import ai.onnxruntime.OrtEnvironment
import ai.onnxruntime.OrtSession
import com.example.projekmobile_kel11.databinding.ActivityPredictionFormBinding
import com.example.projekmobile_kel11.databinding.DialogPredictionResultBinding
import java.io.File
import java.io.FileOutputStream
import java.nio.FloatBuffer

class PredictionFormActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPredictionFormBinding
    private lateinit var ortEnv: OrtEnvironment
    private lateinit var session: OrtSession

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPredictionFormBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inisialisasi ONNX Runtime
        ortEnv = OrtEnvironment.getEnvironment()
        val modelFile = File(getDir("models", MODE_PRIVATE), "oral_cancer_model_RF.onnx")
        copyAssetToInternalStorage("oral_cancer_model_RF.onnx", modelFile)
        val sessionOptions = OrtSession.SessionOptions()
        session = ortEnv.createSession(modelFile.absolutePath, sessionOptions)

        binding.btnSubmit.setOnClickListener {
            try {
                if (!validateForm()) return@setOnClickListener

                // 1. Ambil semua input
                val age = binding.etAge.text.toString().trim().toFloat()
                val gender = getRadioGender(binding.rgGender)               // 0/1
                val tobacco = getRadio01(binding.rgTobacco)
                val alcohol = getRadio01(binding.rgAlcohol)
                val hpv = getRadio01(binding.rgHpv)
                val betel = getRadio01(binding.rgBetelQuid)
                val sun = getRadio01(binding.rgSunExposure)
                val hygiene = getRadio01(binding.rgOralHygiene)
                val diet = getRadio01(binding.rgDiet)
                val family = getRadio01(binding.rgFamilyHistory)
                val immune = getRadio01(binding.rgImmuneSystem)
                val lesions = getRadio01(binding.rgOralLesions)
                val bleeding = getRadio01(binding.rgBleeding)
                val swallowing = getRadio01(binding.rgSwallowing)
                val patches = getRadio01(binding.rgPatches)

                // 2. SUSUN array fitur sesuai urutan training sklearn (contoh 15 fitur)
                // Pastikan urutan & jumlahnya sama dengan yang dipakai saat melatih model RF
                val featureArray = floatArrayOf(
                    age,
                    gender.toFloat(),
                    tobacco.toFloat(),
                    alcohol.toFloat(),
                    hpv.toFloat(),
                    betel.toFloat(),
                    sun.toFloat(),
                    hygiene.toFloat(),
                    diet.toFloat(),
                    family.toFloat(),
                    immune.toFloat(),
                    lesions.toFloat(),
                    bleeding.toFloat(),
                    swallowing.toFloat(),
                    patches.toFloat()
                )

                val nFeatures = featureArray.size
                val buffer = FloatBuffer.wrap(featureArray)
                val tensor = OnnxTensor.createTensor(
                    ortEnv,
                    buffer,
                    longArrayOf(1L, nFeatures.toLong())
                )




                // 3. Jalankan inference (nama input "input" dari initial_type [('input', ...)])
                val outputs = session.run(mapOf("input" to tensor))

                @Suppress("UNCHECKED_CAST")
                val outputArray2D = outputs[0].value as Array<FloatArray>

                // ambil satu nilai probabilitas (baris 0, kolom 0)
                val prob = outputArray2D[0][0]  // Float 0–1

                val riskLevel = when {
                    prob < 0.3f -> "Rendah"
                    prob < 0.7f -> "Sedang"
                    else -> "Tinggi"
                }

                showResultDialog(riskLevel, prob)

                outputs.close()
                tensor.close()
            } catch (e: Exception) {
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                e.printStackTrace()
            }
        }
    }

    private fun validateForm(): Boolean {
        if (binding.etAge.text.isNullOrEmpty()) {
            Toast.makeText(this, "Usia harus diisi", Toast.LENGTH_SHORT).show()
            return false
        }
        val radioGroups = listOf(
            binding.rgGender,
            binding.rgTobacco,
            binding.rgAlcohol,
            binding.rgHpv,
            binding.rgBetelQuid,
            binding.rgSunExposure,
            binding.rgOralHygiene,
            binding.rgDiet,
            binding.rgFamilyHistory,
            binding.rgImmuneSystem,
            binding.rgOralLesions,
            binding.rgBleeding,
            binding.rgSwallowing,
            binding.rgPatches
        )
        for (rg in radioGroups) {
            if (rg.checkedRadioButtonId == -1) {
                Toast.makeText(this, "Jawab semua pertanyaan Ya/Tidak", Toast.LENGTH_SHORT).show()
                return false
            }
        }
        return true
    }

    // Yes/No → 1/0
    private fun getRadio01(rg: android.widget.RadioGroup): Int {
        val selectedId = rg.checkedRadioButtonId
        val rb = findViewById<RadioButton>(selectedId)
        return if (rb.text.toString().equals("Ya", ignoreCase = true)) 1 else 0
    }

    // Gender khusus: Perempuan = 0, Laki-laki = 1 (sesuaikan dengan training)
    private fun getRadioGender(rg: android.widget.RadioGroup): Int {
        val selectedId = rg.checkedRadioButtonId
        val rb = findViewById<RadioButton>(selectedId)
        val text = rb.text.toString().lowercase()
        return if (text.contains("laki")) 1 else 0
    }

    private fun copyAssetToInternalStorage(assetName: String, destFile: File) {
        if (!destFile.exists()) {
            assets.open(assetName).use { input ->
                FileOutputStream(destFile).use { output ->
                    input.copyTo(output)
                }
            }
        }
    }

    private fun showResultDialog(riskLevel: String, prob: Float) {
        val dialog = Dialog(this)
        val dialogBinding = DialogPredictionResultBinding.inflate(layoutInflater)
        dialog.setContentView(dialogBinding.root)

        dialogBinding.tvRiskLevel.text = "Risiko: $riskLevel"
        dialogBinding.tvRiskScore.text = String.format("Skor: %.2f%%", prob * 100f)

        dialogBinding.btnOk.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        session.close()
        ortEnv.close()
    }

}