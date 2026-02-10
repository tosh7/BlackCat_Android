package com.blackcat.android.domain.usecase

import com.blackcat.android.domain.model.DeliveryCarrier
import com.blackcat.android.domain.model.TrackingResult
import com.blackcat.android.domain.repository.DeliveryRepository
import javax.inject.Inject

class AddDeliveryUseCase @Inject constructor(
    private val repository: DeliveryRepository
) {
    suspend operator fun invoke(
        trackingNumber: String,
        carrier: DeliveryCarrier,
        memo: String = ""
    ): Result<Long> {
        val cleaned = trackingNumber.replace("-", "").replace(" ", "")

        if (cleaned.length !in carrier.trackingNumberLength) {
            return Result.failure(IllegalArgumentException("伝票番号の桁数が正しくありません"))
        }

        val trackResult = repository.trackDelivery(cleaned, carrier)
        if (trackResult is TrackingResult.Error) {
            return Result.failure(Exception(trackResult.message))
        }

        val id = repository.addDelivery(cleaned, carrier, memo)

        if (trackResult is TrackingResult.Success) {
            repository.refreshDelivery(id)
        }

        return Result.success(id)
    }
}
