package com.blackcat.android.domain.model

sealed class TrackingResult {
    data class Success(val statusList: List<DeliveryStatus>) : TrackingResult()
    data class Error(val message: String) : TrackingResult()
}
