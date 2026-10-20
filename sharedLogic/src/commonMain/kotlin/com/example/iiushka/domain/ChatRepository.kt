package com.example.iiushka.domain
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    fun observeMessages(): Flow<List<ChatMessage>>

    suspend fun sendMessage(text: String): ChatMessage

    suspend fun clearChat()
}
