package com.example.projekmobile_kel11.ui.prediction

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.projekmobile_kel11.Preprocessor
import com.example.projekmobile_kel11.OnnxPredictor
import com.example.projekmobile_kel11.R
import com.example.projekmobile_kel11.data.model.PredictionInput
import com.example.projekmobile_kel11.utils.MLHelper

class PredictionFormActivity : AppCompatActivity() {

    private lateinit var mlHelper: MLHelper


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_prediction_form)

        mlHelper = MLHelper(this)


        findViewById<Button>(R.id.btnSubmit).setOnClickListener {
            runPrediction()
        }
    }

    private fun runPrediction() {

        // ===============================
        // 1. VALIDASI INPUT
        // ===============================
        val ageStr = findViewById<EditText>(R.id.etAge).text.toString()
        if (ageStr.isEmpty()) {
            toast("Usia wajib diisi")
            return
        }

        val gender = when (findViewById<RadioGroup>(R.id.rgGender).checkedRadioButtonId) {
            R.id.rbMale -> "Laki-laki"
            R.id.rbFemale -> "Perempuan"
            else -> null
        }

        if (gender == null) {
            toast("Pilih jenis kelamin")
            return
        }

        // ===============================
        // 2. BUILD INPUT MAP
        // (HARUS SAMA DENGAN model_features.json)
        // ===============================
        val tumorSize = findViewById<EditText>(R.id.etTumorSize).text.toString()
        if (tumorSize.isEmpty()) {
            toast("Ukuran tumor wajib diisi")
            return
        }

        val cancerStage = findRadioText(R.id.rgCancerStage)
        val treatment = findRadioText(R.id.rgTreatment)
        val earlyDiagnosis = findRadioText(R.id.rgEarlyDiagnosis)

        val input = PredictionInput(
            age = ageStr.toInt(),
            gender = gender,
            tobacco = yesNo(R.id.rgTobacco),
            alcohol = yesNo(R.id.rgAlcohol),
            hpv = yesNo(R.id.rgHpv),
            betel = yesNo(R.id.rgBetelQuid),
            sun = yesNo(R.id.rgSunExposure),
            oralHygiene = yesNo(R.id.rgOralHygiene),
            diet = yesNo(R.id.rgDiet),
            familyHistory = yesNo(R.id.rgFamilyHistory),
            immune = yesNo(R.id.rgImmuneSystem),
            lesions = yesNo(R.id.rgOralLesions),
            bleeding = yesNo(R.id.rgBleeding),
            swallowing = yesNo(R.id.rgSwallowing),
            patches = yesNo(R.id.rgPatches),
            tumorSize = tumorSize.toDouble(),
            cancerStage = cancerStage,
            treatmentType = treatment,
            earlyDiagnosis = earlyDiagnosis
        )


        // ===============================
        // 3. PREPROCESS & PREDICT
        // ===============================
        val (predClass, prob) = mlHelper.predict(input)
        showResultPopup(predClass, prob)


        // ===============================
        // 4. TAMPILKAN POPUP
        // ===============================
        showResultPopup(predClass, prob)
    }

    // ===============================
    // HELPER
    // ===============================
    private fun yesNo(rgId: Int): String {
        val id = findViewById<RadioGroup>(rgId).checkedRadioButtonId
        return if (id != -1 &&
            findViewById<RadioButton>(id).text.toString() == "Ya"
        ) "Yes" else "No"
    }

    private fun showResultPopup(predClass: Int, prob: Float) {

        val status = if (predClass == 1)
            "RISIKO TINGGI"
        else
            "RISIKO RENDAH"

        val message = """
            Hasil Prediksi Kanker Mulut
            
            Status:
            $status
            
            Tingkat Kepercayaan:
            ${(prob * 100).toInt()} %
        """.trimIndent()

        AlertDialog.Builder(this)
            .setTitle("Hasil Prediksi")
            .setMessage(message)
            .setPositiveButton("OK", null)
            .show()
    }

    private fun toast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
    private fun findRadioText(rgId: Int): String {
        val rg = findViewById<RadioGroup>(rgId)
        val checkedId = rg.checkedRadioButtonId
        if (checkedId == -1) {
            toast("Semua pertanyaan wajib diisi")
            throw IllegalStateException()
        }
        return findViewById<RadioButton>(checkedId).text.toString()
    }
}
