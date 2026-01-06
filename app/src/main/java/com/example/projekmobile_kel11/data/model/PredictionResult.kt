package com.example.projekmobile_kel11.data.model

data class PredictionResult(
    var predictionId: String? = null,
    var userId: String = "",
    var timestamp: Long = 0L,
    var riskLevel: String = "",
    var riskScore: Double = 0.0
)

