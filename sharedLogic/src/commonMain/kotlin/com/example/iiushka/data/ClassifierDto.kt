package com.example.iiushka.data

import kotlinx.serialization.Serializable

/** Запрос к POST /predict */
@Serializable
data class PredictRequest(
    val text: String
)

/** Успешный ответ от POST /predict */
@Serializable
data class PredictResponse(
    val text: String,
    val label: Int,          // 1 = позитив, 0 = негатив
    val sentiment: String,   // "positive" / "negative"
    val prob: Double         // вероятность позитива 0.0..1.0
) {
    /** Уверенность в вердикте: label=1 -> prob, label=0 -> 1-prob */
    val confidence: Double
        get() = if (label == 1) prob else 1.0 - prob

    val isPositive: Boolean get() = label == 1
}

/** Ответ при ошибке (400 и др.) */
@Serializable
data class ErrorResponse(
    val error: String
)

/** Ответ от GET /health */
@Serializable
data class HealthResponse(
    val status: String
)
