package com.blackcat.android.data.remote.service

import com.blackcat.android.domain.model.DeliveryCarrier
import com.blackcat.android.domain.model.TrackingResult
import javax.inject.Inject

class CarrierTrackingService @Inject constructor(
    private val yamatoService: YamatoTrackingService,
    private val sagawaService: SagawaTrackingService,
    private val japanPostService: JapanPostTrackingService
) {
    suspend fun track(trackingNumber: String, carrier: DeliveryCarrier): TrackingResult {
        return when (carrier) {
            DeliveryCarrier.YAMATO -> yamatoService.track(trackingNumber)
            DeliveryCarrier.SAGAWA -> sagawaService.track(trackingNumber)
            DeliveryCarrier.JAPAN_POST -> japanPostService.track(trackingNumber)
        }
    }
}
