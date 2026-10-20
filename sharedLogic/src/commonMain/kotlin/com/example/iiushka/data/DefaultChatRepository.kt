package com.example.iiushka.data

import com.example.iiushka.domain.ChatMessage
import com.example.iiushka.domain.ChatRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DefaultChatRepository(
    private val classifierApi: ClassifierApi,
    private val chatDao: ChatDao,
) : ChatRepository {

    override fun observeMessages(): Flow<List<ChatMessage>> =
        chatDao.observeMessages().map { messages -> messages.map(MessageEntity::toDomain) }

    /**
     * Сохраняет реплику пользователя → шлёт в бэк → сохраняет вердикт бота.
     * Бросает исключение при ошибке сети: user-сообщение уже сохранено,
     * bot-сообщение не появится. Разбор ошибки — на стороне VM (шаг 5).
     */
    override suspend fun sendMessage(text: String): ChatMessage {
        val trimmed = text.trim()
        if (trimmed.isEmpty()) throw IllegalArgumentException("empty text")

        val userMessage = MessageEntity(
            text = trimmed,
            isFromUser = true,
            timestamp = currentTimeMillis(),
        )
        chatDao.insertMessage(userMessage)

        val response = classifierApi.predict(trimmed)

        val botMessage = MessageEntity(
            text = if (response.isPositive) "ПОЗИТИВ" else "НЕГАТИВ",
            isFromUser = false,
            timestamp = currentTimeMillis(),
            positive = response.isPositive,
            confidence = response.confidence,
        )
        chatDao.insertMessage(botMessage)
        return botMessage.toDomain()
    }

    override suspend fun clearChat() {
        chatDao.clearChat()
    }
}

private fun MessageEntity.toDomain(): ChatMessage = ChatMessage(
    id = id,
    text = text,
    isFromUser = isFromUser,
    timestamp = timestamp,
    positive = positive,
    confidence = confidence,
)
