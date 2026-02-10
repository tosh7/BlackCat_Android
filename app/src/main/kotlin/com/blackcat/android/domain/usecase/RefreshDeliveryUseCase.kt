package com.blackcat.android.domain.usecase

import com.blackcat.android.domain.model.TrackingResult
import com.blackcat.android.domain.repository.DeliveryRepository
import javax.inject.Inject

class RefreshDeliveryUseCase @Inject constructor(
    private val repository: DeliveryRepository
) {
    suspend operator fun invoke(id: Long): TrackingResult {
        return repository.refreshDelivery(id)
    }

    suspend fun refreshAll(): List<Pair<Long, TrackingResult>> {
        return repository.refreshAllDeliveries()
    }
}
