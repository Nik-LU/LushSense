package com.example.iiushka.data

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpStatusCode
import io.ktor.http.isSuccess

class ClassifierApi(
    private val client: HttpClient
) {

    /**
     * POST /predict — анализ тональности фразы.
     * Бросает исключение при ошибке — это требование Kotlin/Native,
     * чтобы функция экспортировалась в Swift как `async throws`.
     */
    @Throws(Exception::class)
    suspend fun predict(text: String): PredictResponse {
        require(text.isNotBlank()) { "empty text" }

        val response: HttpResponse = client.post("/predict") {
            setBody(PredictRequest(text = text))
        }

        return when {
            response.status.isSuccess() -> response.body()

            response.status == HttpStatusCode.BadRequest -> {
                val err = response.body<ErrorResponse>()
                throw IllegalStateException("Server rejected: ${err.error}")
            }

            else -> throw IllegalStateException("HTTP ${response.status.value}")
        }
    }

    /** GET /health — быстрый пинг сервера. */
    @Throws(Exception::class)
    suspend fun health(): HealthResponse =
        client.get("/health").body()

}
