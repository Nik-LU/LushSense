package com.example.iiushka.presentation

import com.example.iiushka.domain.ChatMessage

sealed interface ChatUiState {

    /** Кратковременное состояние, пока Room делает первый эмит. */
    data object Loading : ChatUiState

    /** Основное состояние. */
    data class Content(
        val messages: List<ChatMessage>,
        val isSending: Boolean,
    ) : ChatUiState

    /** Фатальная ошибка (например, БД недоступна). Send-ошибки сюда НЕ попадают. */
    data class Error(val message: String) : ChatUiState
}
