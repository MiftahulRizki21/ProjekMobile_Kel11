package com.example.projekmobile_kel11.data.model

data class ChatMessage(
    var messageId: String = "",
    var senderId: String = "",
    var message: String = "",
    var timestamp: Long = 0L,
    var status: String = "sent" // sent | delivered | read
)

