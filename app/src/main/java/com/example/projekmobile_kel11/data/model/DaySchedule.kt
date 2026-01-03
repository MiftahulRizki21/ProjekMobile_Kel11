package com.example.projekmobile_kel11.data.model

data class DaySchedule(
    val dayKey: String = "",
    val dayLabel: String = "",
    var start: String = "",
    var end: String = "",
    var available: Boolean = false
)
