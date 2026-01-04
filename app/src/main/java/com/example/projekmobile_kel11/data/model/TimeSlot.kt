package com.example.projekmobile_kel11.data.model


data class TimeSlot(
    var id: String = "",
    var doctorId: String = "",
    var date: String = "",       // yyyy-MM-dd
    var day: String = "",   // Senin
    var startTime: String = "",
    var endTime: String = "",
    var status: String = "available",
    var patientId: String? = null
)

