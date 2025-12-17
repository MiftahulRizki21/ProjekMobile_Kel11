// di dalam file models/Dokter.kt
package com.example.projekmobile_kel11.models

data class Dokter(
    val id: String,
    val nama: String,
    val spesialisasi: String,
    val fotoUrl: String? = null
)
