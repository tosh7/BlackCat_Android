package com.blackcat.android.domain.repository

import com.blackcat.android.domain.model.DeliveryCarrier
import com.blackcat.android.domain.model.DeliveryItem
import com.blackcat.android.domain.model.TrackingResult
import kotlinx.coroutines.flow.Flow

interface DeliveryRepository {
    fun getAllDeliveries(): Flow<List<DeliveryItem>>
    suspend fun getDeliveryById(id: Long): DeliveryItem?
    suspend fun addDelivery(trackingNumber: String, carrier: DeliveryCarrier, memo: String = ""): Long
    suspend fun deleteDelivery(id: Long)
    suspend fun deleteAllDeliveries()
    suspend fun refreshDelivery(id: Long): TrackingResult
    suspend fun refreshAllDeliveries(): List<Pair<Long, TrackingResult>>
    suspend fun trackDelivery(trackingNumber: String, carrier: DeliveryCarrier): TrackingResult
}
