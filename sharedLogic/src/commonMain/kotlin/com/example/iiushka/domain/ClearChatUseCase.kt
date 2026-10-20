package com.example.iiushka.domain
import kotlin.coroutines.cancellation.CancellationException

class ClearChatUseCase(
    private val chatRepository: ChatRepository,
) {

    @Throws(ChatDomainError::class, CancellationException::class)
    suspend operator fun invoke() {
        try {
            chatRepository.clearChat()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            throw ChatDomainError.Unknown(e)
        }
    }
}
