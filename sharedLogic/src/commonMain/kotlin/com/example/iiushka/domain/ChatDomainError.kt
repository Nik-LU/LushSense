package com.example.iiushka.domain

/**
 * Доменные ошибки чата. Отдельно от инфраструктурных (Ktor, Room),
 * чтобы VM не знала, откуда именно прилетело.
 */
sealed class ChatDomainError(message: String, cause: Throwable? = null) :
    Exception(message, cause) {

    /** Пустой ввод — UI покажет подсказку. */
    class EmptyText : ChatDomainError("Введите текст")

    /** Сетевая ошибка / бэк недоступен. */
    class Network(cause: Throwable) :
        ChatDomainError("Нет связи с сервером: ${cause.message ?: "unknown"}", cause)

    /** Всё прочее — на всякий случай. */
    class Unknown(cause: Throwable) :
        ChatDomainError(cause.message ?: "Неизвестная ошибка", cause)
}
