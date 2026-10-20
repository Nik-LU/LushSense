package com.example.iiushka.data

import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json

/** Каждая платформа даёт свой движок (OkHttp на Android, Darwin на iOS). */
expect fun defaultHttpEngine(): HttpClientEngine

/** Общая фабрика клиента — используется на всех платформах. */
@OptIn(ExperimentalSerializationApi::class)
fun createHttpClient(engine: HttpClientEngine = defaultHttpEngine()): HttpClient {
    val json = Json {
        ignoreUnknownKeys = true   // терпим новые поля от сервера
        isLenient = true
        explicitNulls = false
    }

    return HttpClient(engine) {
        // Автоматический JSON ↔ Kotlin
        install(ContentNegotiation) { json(json) }

        // Таймауты
        install(HttpTimeout) {
            requestTimeoutMillis = 15_000
            connectTimeoutMillis = 5_000
            socketTimeoutMillis  = 15_000
        }

        // Логи в консоль
        install(Logging) {
            logger = object : Logger {
                override fun log(message: String) = println("[HTTP] $message")
            }
            level = LogLevel.INFO
        }

        // Базовый URL и Content-Type по умолчанию
        defaultRequest {
            url(ApiConfig.baseUrl)
            contentType(ContentType.Application.Json)
        }
    }
}
