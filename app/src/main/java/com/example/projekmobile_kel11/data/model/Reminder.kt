package com.example.projekmobile_kel11.data.model

import com.google.firebase.Timestamp

data class Reminder(
    var id: String = "",
    var title: String? = "",
    var hour: Int = 0,
    var minute: Int = 0,
    var userName: String? = "",
    var interval: String = "daily",
    var isActive: Boolean = true,
    var nextTrigger: Timestamp? = null
) {
    constructor() : this("", "", 0, 0, "", "daily", true, null)
}







