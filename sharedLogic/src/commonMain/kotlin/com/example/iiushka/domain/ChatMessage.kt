package com.example.iiushka.domain

data class ChatMessage(
    val id: Long,
    val text: String,
    val isFromUser: Boolean,
    val timestamp: Long,
    val positive: Boolean? = null,
    val confidence: Double? = null,
)
