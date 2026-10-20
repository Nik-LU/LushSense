package com.example.iiushka.domain
import kotlin.coroutines.cancellation.CancellationException

class SendMessageUseCase(
    private val chatRepository: ChatRepository,
) {

    @Throws(ChatDomainError::class, CancellationException::class)
    suspend operator fun invoke(text: String): ChatMessage {
        val trimmed = text.trim()
        if (trimmed.isEmpty()) throw ChatDomainError.EmptyText()

        return try {
            chatRepository.sendMessage(trimmed)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            throw ChatDomainError.Network(e)
        }
    }
}
