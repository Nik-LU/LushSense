package com.example.iiushka.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.iiushka.domain.ChatDomainError
import com.example.iiushka.domain.ChatRepository
import com.example.iiushka.domain.ClearChatUseCase
import com.example.iiushka.domain.SendMessageUseCase
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex

class ChatViewModel(
    private val sendMessageUseCase: SendMessageUseCase,
    private val clearChatUseCase: ClearChatUseCase,
    private val chatRepository: ChatRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<ChatUiState>(ChatUiState.Loading)
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    private val _events = Channel<ChatEvent>(Channel.BUFFERED)
    val events: Flow<ChatEvent> = _events.receiveAsFlow()

    private val isSending = MutableStateFlow(false)
    private val operationMutex = Mutex()

    init {
        viewModelScope.launch {
            try {
                combine(
                    chatRepository.observeMessages(),
                    isSending,
                ) { messages, sending ->
                    ChatUiState.Content(messages = messages, isSending = sending)
                }.collect { state ->
                    _uiState.value = state
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.value = ChatUiState.Error(e.message ?: "Ошибка загрузки истории")
            }
        }
    }

    fun sendMessage(text: String) {
        viewModelScope.launch {
            if (!operationMutex.tryLock()) return@launch
            isSending.value = true
            try {
                sendMessageUseCase(text)
            } catch (e: CancellationException) {
                throw e
            } catch (e: ChatDomainError) {
                _events.send(ChatEvent.ShowError(e.message ?: "Ошибка"))
            } catch (e: Exception) {
                _events.send(ChatEvent.ShowError(e.message ?: "Ошибка"))
            } finally {
                isSending.value = false
                operationMutex.unlock()
            }
        }
    }

    fun clearChat() {
        viewModelScope.launch {
            if (!operationMutex.tryLock()) return@launch
            try {
                clearChatUseCase()
            } catch (e: CancellationException) {
                throw e
            } catch (e: ChatDomainError) {
                _events.send(ChatEvent.ShowError(e.message ?: "Ошибка очистки"))
            } finally {
                operationMutex.unlock()
            }
        }
    }

    fun cleanup() {
        viewModelScope.cancel()
    }
}
