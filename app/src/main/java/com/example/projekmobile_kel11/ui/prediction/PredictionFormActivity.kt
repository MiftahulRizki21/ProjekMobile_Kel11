package com.example.projekmobile_kel11.ui.prediction

import android.app.Dialog
import android.os.Bundle
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.projekmobile_kel11.R
import com.example.projekmobile_kel11.data.model.PredictionInput
import com.example.projekmobile_kel11.data.repository.PredictionRepository
import com.example.projekmobile_kel11.databinding.ActivityPredictionFormBinding
import com.example.projekmobile_kel11.databinding.DialogPredictionResultBinding
import com.example.projekmobile_kel11.utils.MLHelper

class PredictionFormActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPredictionFormBinding
    private lateinit var predictionRepository: PredictionRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPredictionFormBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Prediksi Kanker Mulut"

        val mlHelper = MLHelper(this)
        predictionRepository = PredictionRepository(mlHelper)

        binding.btnSubmit.setOnClickListener {
            if (validateForm()) {
                val input = collectInputData()
                val result = predictionRepository.predictCancerRisk(input)
                showResultDialog(result.riskLevel, result.riskScore)
            }
        }
    }

    private fun validateForm(): Boolean {
        // Validasi semua RadioGroup sudah dipilih
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
                Toast.makeText(this, "Mohon jawab semua pertanyaan", Toast.LENGTH_SHORT).show()
                return false
            }
        }

        if (binding.etAge.text.isNullOrEmpty()) {
            Toast.makeText(this, "Mohon isi usia Anda", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    private fun collectInputData(): PredictionInput {
        return PredictionInput(
            age = binding.etAge.text.toString().toInt(),
            gender = getRadioValue(binding.rgGender),
            tobaccoUse = getRadioValue(binding.rgTobacco),
            alcoholConsumption = getRadioValue(binding.rgAlcohol),
            hpvInfection = getRadioValue(binding.rgHpv),
            betelQuidUse = getRadioValue(binding.rgBetelQuid),
            chronicSunExposure = getRadioValue(binding.rgSunExposure),
            poorOralHygiene = getRadioValue(binding.rgOralHygiene),
            dietFruitsVegetables = getRadioValue(binding.rgDiet),
            familyHistoryCancer = getRadioValue(binding.rgFamilyHistory),
            compromisedImmuneSystem = getRadioValue(binding.rgImmuneSystem),
            oralLesions = getRadioValue(binding.rgOralLesions),
            unexplainedBleeding = getRadioValue(binding.rgBleeding),
            difficultySwallowing = getRadioValue(binding.rgSwallowing),
            whiteRedPatches = getRadioValue(binding.rgPatches),
            tumorSize = 0f, // Bisa ditambahkan input field jika diperlukan
            cancerStage = 0,
            treatmentType = 0,
            earlyDiagnosis = 0
        )
    }

    private fun getRadioValue(radioGroup: android.widget.RadioGroup): Int {
        val selectedId = radioGroup.checkedRadioButtonId
        val radioButton = findViewById<RadioButton>(selectedId)
        return if (radioButton.text == "Ya") 1 else 0
    }

    private fun showResultDialog(riskLevel: String, riskScore: Float) {
        val dialog = Dialog(this)
        val dialogBinding = DialogPredictionResultBinding.inflate(layoutInflater)
        dialog.setContentView(dialogBinding.root)

        dialogBinding.tvRiskLevel.text = "Risiko: $riskLevel"
        dialogBinding.tvRiskScore.text = "Skor: ${String.format("%.2f", riskScore * 100)}%"

        val color = when (riskLevel) {
            "Rendah" -> getColor(R.color.risk_low)
            "Sedang" -> getColor(R.color.risk_medium)
            else -> getColor(R.color.risk_high)
        }
        dialogBinding.tvRiskLevel.setTextColor(color)

        dialogBinding.btnOk.setOnClickListener {
            dialog.dismiss()
            finish()
        }

        dialog.show()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
