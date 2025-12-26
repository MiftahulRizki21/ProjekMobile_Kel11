package com.example.projekmobile_kel11.data.model

data class User(
    var userId: String = "",
    var nama: String = "",
    var email: String = "",
    var role: String = "",
    var usia: Int = 0,
    var gender: String = "",
    var phone: String = "",
    var photoUrl: String = "",
    var createdAt: Long = 0L,
    var updatedAt: Long = 0L
)
