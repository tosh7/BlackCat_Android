package com.blackcat.android.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.blackcat.android.domain.model.DeliveryCarrier

@Entity(tableName = "deliveries")
data class DeliveryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val trackingNumber: String,
    val carrier: String,
    val statusListJson: String = "[]",
    val registeredDate: String,
    val memo: String = ""
) {
    val deliveryCarrier: DeliveryCarrier
        get() = DeliveryCarrier.valueOf(carrier)
}
