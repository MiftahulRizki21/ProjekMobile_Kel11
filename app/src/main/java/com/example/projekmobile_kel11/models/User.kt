// di dalam file models/User.kt
package com.example.projekmobile_kel11.models

data class User(
    val id: String,
    val nama: String,    val email: String,
    val fotoUrl: String? = null
)
