package com.example.projekmobile_kel11.data.model


data class Doctor(
    var userId: String = "",
    var nama: String = "",
    var email: String = "",
    var password: String = "",
    var spesialisasi: String = "",
    var fotoUrl: String = "",
    var role: String = "doctor",
    var mySchedules: MutableList<TimeSlot> = mutableListOf()
)
