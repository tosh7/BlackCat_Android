package com.blackcat.android.domain.usecase

import com.blackcat.android.domain.model.DeliveryItem
import com.blackcat.android.domain.repository.DeliveryRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetDeliveryListUseCase @Inject constructor(
    private val repository: DeliveryRepository
) {
    operator fun invoke(): Flow<List<DeliveryItem>> {
        return repository.getAllDeliveries()
    }
}
