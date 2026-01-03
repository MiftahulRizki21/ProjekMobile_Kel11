package com.example.projekmobile_kel11.data.model


data class TimeSlot(
    var id: String = "",
    var doctorId: String = "",
    var date: String = "",       // 2026-01-05
    var dayLabel: String = "",   // Senin
    var startTime: String = "",  // 09:00
    var endTime: String = "",    // 10:00
    var status: String = "available",
    var patientId: String? = null
)
