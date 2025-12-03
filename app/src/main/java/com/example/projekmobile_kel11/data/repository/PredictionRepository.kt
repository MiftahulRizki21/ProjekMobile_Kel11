package com.example.projekmobile_kel11.data.repository

import com.example.projekmobile_kel11.data.model.PredictionInput
import com.example.projekmobile_kel11.data.model.PredictionResult
import com.example.projekmobile_kel11.utils.MLHelper

class PredictionRepository(private val mlHelper: MLHelper) {

    fun predictCancerRisk(input: PredictionInput): PredictionResult {
        val riskScore = mlHelper.predict(input)
        val riskLevel = when {
            riskScore < 0.3 -> "Rendah"
            riskScore < 0.7 -> "Sedang"
            else -> "Tinggi"
        }

        return PredictionResult(
            predictionId = System.currentTimeMillis().toString(),
            tanggalPrediksi = getCurrentDate(),
            riskLevel = riskLevel,
            riskScore = riskScore,
            input = input
        )
    }

    private fun getCurrentDate(): String {
        val sdf = java.text.SimpleDateFormat("dd MMM yyyy", java.util.Locale("id", "ID"))
        return sdf.format(java.util.Date())
    }
}
