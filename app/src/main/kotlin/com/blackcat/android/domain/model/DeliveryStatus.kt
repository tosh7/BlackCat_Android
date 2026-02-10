package com.blackcat.android.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class DeliveryStatus(
    val status: String,
    val date: String,
    val time: String? = null,
    val location: String
) {
    val statusType: DeliveryStatusType
        get() = DeliveryStatusType.fromStatus(status)
}
