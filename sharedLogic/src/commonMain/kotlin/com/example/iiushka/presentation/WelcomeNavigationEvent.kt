package com.example.iiushka.presentation

/**
 * Одноразовые события навигации с Welcome-экрана.
 * Приходят в UI через Channel (single-consumer).
 */
sealed interface WelcomeNavigationEvent {
    data object NavigateToChat : WelcomeNavigationEvent
}
