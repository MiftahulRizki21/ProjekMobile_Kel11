package com.example.projekmobile_kel11.data.model

data class PredictionResult(
    val predictionId: String = "",
    val userId: String = "",
    val tanggalPrediksi: String = "",
    val riskLevel: String = "", // "Rendah", "Sedang", "Tinggi"
    val riskScore: Float = 0f,
    val input: PredictionInput? = null
)
