package com.example.iiushka.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Одно сообщение в локальном чате.
 *
 * Сервер не хранит историю — он только классифицирует текст.
 * Поэтому полей serverId / syncedAt здесь нет.
 */
@Entity(
    tableName = "chat_messages",
    indices = [Index(value = ["timestamp"], name = "idx_chat_messages_timestamp")]
)
data class MessageEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,

    /** Для user — текст реплики. Для бота — "ПОЗИТИВ" / "НЕГАТИВ" / текст ошибки. */
    val text: String,

    /** true — сообщение пользователя, false — ответ LushSense (успех или ошибка). */
    val isFromUser: Boolean,

    /** Время создания в epoch millis. */
    val timestamp: Long,

    /** null для user-сообщений и ошибок бота. true/false — вердикт. */
    val positive: Boolean? = null,

    /** 0.0..1.0 — уверенность бота. null для user и ошибок. */
    val confidence: Double? = null,
)
