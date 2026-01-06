package com.example.projekmobile_kel11.data.model

data class User(
    var userId: String = "",
    var name: String = "",
    var email: String = "",
    var role: String = "",
    var age: Long = 0L,   // 🔥 FIX
    var gender: String = "",
    var phone: String = "",
    var photoUrl: String = "",
    var createdAt: Long = 0L,
    var updatedAt: Long = 0L
)

