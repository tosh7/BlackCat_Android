package com.blackcat.android.data.repository

import com.blackcat.android.data.local.dao.DeliveryDao
import com.blackcat.android.data.local.entity.DeliveryEntity
import com.blackcat.android.data.remote.service.CarrierTrackingService
import com.blackcat.android.domain.model.DeliveryCarrier
import com.blackcat.android.domain.model.DeliveryItem
import com.blackcat.android.domain.model.DeliveryStatus
import com.blackcat.android.domain.model.TrackingResult
import com.blackcat.android.domain.repository.DeliveryRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class DeliveryRepositoryImpl @Inject constructor(
    private val deliveryDao: DeliveryDao,
    private val trackingService: CarrierTrackingService
) : DeliveryRepository {

    private val json = Json { ignoreUnknownKeys = true }
    private val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

    override fun getAllDeliveries(): Flow<List<DeliveryItem>> {
        return deliveryDao.getAllDeliveries().map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override suspend fun getDeliveryById(id: Long): DeliveryItem? {
        return deliveryDao.getDeliveryById(id)?.toDomainModel()
    }

    override suspend fun addDelivery(trackingNumber: String, carrier: DeliveryCarrier, memo: String): Long {
        val entity = DeliveryEntity(
            trackingNumber = trackingNumber,
            carrier = carrier.name,
            registeredDate = LocalDateTime.now().format(dateFormatter),
            memo = memo
        )
        return deliveryDao.insertDelivery(entity)
    }

    override suspend fun deleteDelivery(id: Long) {
        deliveryDao.deleteDeliveryById(id)
    }

    override suspend fun deleteAllDeliveries() {
        deliveryDao.deleteAllDeliveries()
    }

    override suspend fun refreshDelivery(id: Long): TrackingResult {
        val entity = deliveryDao.getDeliveryById(id) ?: return TrackingResult.Error("配送情報が見つかりません")
        val result = trackingService.track(entity.trackingNumber, entity.deliveryCarrier)

        if (result is TrackingResult.Success) {
            val statusJson = json.encodeToString(
                kotlinx.serialization.builtins.ListSerializer(DeliveryStatus.serializer()),
                result.statusList
            )
            deliveryDao.updateDelivery(entity.copy(statusListJson = statusJson))
        }

        return result
    }

    override suspend fun refreshAllDeliveries(): List<Pair<Long, TrackingResult>> = coroutineScope {
        val entities = deliveryDao.getAllDeliveries().let { flow ->
            var result: List<DeliveryEntity> = emptyList()
            flow.collect { result = it; return@collect }
            result
        }

        entities.map { entity ->
            async {
                entity.id to refreshDelivery(entity.id)
            }
        }.awaitAll()
    }

    override suspend fun trackDelivery(trackingNumber: String, carrier: DeliveryCarrier): TrackingResult {
        return trackingService.track(trackingNumber, carrier)
    }

    private fun DeliveryEntity.toDomainModel(): DeliveryItem {
        val statusList = try {
            json.decodeFromString(
                kotlinx.serialization.builtins.ListSerializer(DeliveryStatus.serializer()),
                statusListJson
            )
        } catch (e: Exception) {
            emptyList()
        }

        return DeliveryItem(
            id = id,
            trackingNumber = trackingNumber,
            carrier = deliveryCarrier,
            statusList = statusList,
            registeredDate = try {
                LocalDateTime.parse(registeredDate, dateFormatter)
            } catch (e: Exception) {
                LocalDateTime.now()
            },
            memo = memo
        )
    }
}
