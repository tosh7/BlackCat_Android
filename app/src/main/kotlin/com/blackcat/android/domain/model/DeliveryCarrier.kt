package com.blackcat.android.domain.model

import kotlinx.serialization.Serializable

@Serializable
enum class DeliveryCarrier(
    val displayName: String,
    val trackingNumberLength: IntRange
) {
    YAMATO("ヤマト運輸", 12..12),
    SAGAWA("佐川急便", 12..12),
    JAPAN_POST("日本郵便", 11..13);

    companion object {
        fun fromTrackingNumber(number: String): DeliveryCarrier? {
            val cleaned = number.replace("-", "").replace(" ", "")
            return when {
                cleaned.matches(Regex("^[A-Z]{2}\\d{9}[A-Z]{2}$")) -> JAPAN_POST
                cleaned.length == 12 && cleaned.all { it.isDigit() } -> null // ambiguous
                else -> null
            }
        }
    }
}
