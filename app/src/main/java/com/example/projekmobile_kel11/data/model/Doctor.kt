package com.example.projekmobile_kel11.models

data class Dokter(
    var userId: String = "",
    var nama: String = "",
    val password: String = "",
    var email: String = "",
    var spesialisasi: String = "",
    var fotoUrl: String = "",
    var role: String = "doctor"
)
