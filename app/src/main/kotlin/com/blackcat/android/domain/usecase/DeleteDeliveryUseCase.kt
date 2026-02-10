package com.blackcat.android.domain.usecase

import com.blackcat.android.domain.repository.DeliveryRepository
import javax.inject.Inject

class DeleteDeliveryUseCase @Inject constructor(
    private val repository: DeliveryRepository
) {
    suspend operator fun invoke(id: Long) {
        repository.deleteDelivery(id)
    }
}
