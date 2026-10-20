package com.example.iiushka.presentation

/**
 * Одноразовые события для UI: показать снекбар, диалог и т.д.
 * Отдаются наружу через Flow (Channel.receiveAsFlow()).
 */
sealed interface ChatEvent {
    data class ShowError(val message: String) : ChatEvent
}
