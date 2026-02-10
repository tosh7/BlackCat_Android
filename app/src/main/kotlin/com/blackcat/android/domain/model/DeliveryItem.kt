package com.blackcat.android.domain.model

import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
data class DeliveryItem(
    val id: Long = 0,
    val trackingNumber: String,
    val carrier: DeliveryCarrier,
    val statusList: List<DeliveryStatus> = emptyList(),
    @Serializable(with = LocalDateTimeSerializer::class)
    val registeredDate: LocalDateTime = LocalDateTime.now(),
    val memo: String = ""
) {
    val latestStatus: DeliveryStatus?
        get() = statusList.lastOrNull()

    val latestStatusType: DeliveryStatusType?
        get() = latestStatus?.statusType

    val isDelivered: Boolean
        get() = latestStatusType == DeliveryStatusType.DELIVERED

    val formattedTrackingNumber: String
        get() {
            val cleaned = trackingNumber.replace("-", "").replace(" ", "")
            return if (cleaned.length == 12 && cleaned.all { it.isDigit() }) {
                "${cleaned.substring(0, 4)}-${cleaned.substring(4, 8)}-${cleaned.substring(8, 12)}"
            } else {
                trackingNumber
            }
        }
}
