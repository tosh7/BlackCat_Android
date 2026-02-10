package com.blackcat.android.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.blackcat.android.domain.model.DeliveryStatusType
import com.blackcat.android.domain.repository.DeliveryRepository
import com.blackcat.android.domain.repository.SettingsRepository
import com.blackcat.android.util.NotificationHelper
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first

@HiltWorker
class DeliveryRefreshWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val repository: DeliveryRepository,
    private val settingsRepository: SettingsRepository,
    private val notificationHelper: NotificationHelper
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            val notificationsEnabled = settingsRepository.notificationsEnabled.first()
            val deliveries = repository.getAllDeliveries().first()

            // Store previous statuses
            val previousStatuses = deliveries.associate { it.id to it.latestStatusType }

            // Refresh all
            val results = repository.refreshAllDeliveries()

            // Check for status changes and send notifications
            if (notificationsEnabled) {
                val updatedDeliveries = repository.getAllDeliveries().first()
                for (delivery in updatedDeliveries) {
                    val previousStatus = previousStatuses[delivery.id]
                    val currentStatus = delivery.latestStatusType

                    if (previousStatus != null && currentStatus != null && previousStatus != currentStatus) {
                        if (currentStatus == DeliveryStatusType.DELIVERED) {
                            notificationHelper.sendDeliveryCompletedNotification(
                                deliveryId = delivery.id,
                                trackingNumber = delivery.formattedTrackingNumber,
                                location = delivery.latestStatus?.location ?: ""
                            )
                        } else {
                            notificationHelper.sendStatusUpdateNotification(
                                deliveryId = delivery.id,
                                trackingNumber = delivery.formattedTrackingNumber,
                                status = currentStatus.displayName,
                                location = delivery.latestStatus?.location ?: ""
                            )
                        }
                    }
                }
            }

            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }

    companion object {
        const val WORK_NAME = "delivery_refresh"
    }
}
