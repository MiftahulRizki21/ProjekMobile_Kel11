// di dalam file models/Reminder.kt
package com.example.projekmobile_kel11.data.model

data class Reminder(
    val id: String,
    val judul: String,
    val waktu: String,
    val namaUser: String // Untuk tahu reminder ini milik siapa
)
