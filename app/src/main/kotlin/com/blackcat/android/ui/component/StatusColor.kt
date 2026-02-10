package com.blackcat.android.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.blackcat.android.domain.model.DeliveryCarrier
import com.blackcat.android.domain.model.DeliveryStatusType
import com.blackcat.android.ui.theme.PrimaryBlue
import com.blackcat.android.ui.theme.PrimaryGreen
import com.blackcat.android.ui.theme.PrimaryOrange
import com.blackcat.android.ui.theme.ShadowLevel3
import com.blackcat.android.ui.theme.StatusDelivered
import com.blackcat.android.ui.theme.StatusInTransit
import com.blackcat.android.ui.theme.StatusOutForDelivery
import com.blackcat.android.ui.theme.StatusReceived
import com.blackcat.android.ui.theme.StatusShipped

fun DeliveryStatusType.toColor(): Color = when (this) {
    DeliveryStatusType.RECEIVED -> StatusReceived
    DeliveryStatusType.SENT -> StatusShipped
    DeliveryStatusType.IN_TRANSIT -> StatusInTransit
    DeliveryStatusType.OUT_FOR_DELIVERY -> StatusOutForDelivery
    DeliveryStatusType.DELIVERED -> StatusDelivered
}

fun DeliveryCarrier.toColor(): Color = when (this) {
    DeliveryCarrier.YAMATO -> PrimaryBlue
    DeliveryCarrier.SAGAWA -> PrimaryOrange
    DeliveryCarrier.JAPAN_POST -> PrimaryGreen
}

fun DeliveryStatusType.toProgress(): Float = when (this) {
    DeliveryStatusType.RECEIVED -> 0.2f
    DeliveryStatusType.SENT -> 0.4f
    DeliveryStatusType.IN_TRANSIT -> 0.6f
    DeliveryStatusType.OUT_FOR_DELIVERY -> 0.8f
    DeliveryStatusType.DELIVERED -> 1.0f
}
