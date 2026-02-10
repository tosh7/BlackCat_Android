package com.blackcat.android.ui.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed class Screen {
    @Serializable
    data object Splash : Screen()

    @Serializable
    data object Onboarding : Screen()

    @Serializable
    data object DeliveryList : Screen()

    @Serializable
    data class DeliveryDetail(val deliveryId: Long) : Screen()

    @Serializable
    data object AddDelivery : Screen()

    @Serializable
    data object Settings : Screen()
}
