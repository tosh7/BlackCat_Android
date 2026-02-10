package com.blackcat.android.domain.repository

import com.blackcat.android.domain.model.DeliveryStatusType
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    val backgroundRefreshEnabled: Flow<Boolean>
    val backgroundRefreshIntervalMinutes: Flow<Int>
    val notificationsEnabled: Flow<Boolean>
    val confettiEnabled: Flow<Boolean>
    val hasCompletedOnboarding: Flow<Boolean>
    val sortOrder: Flow<SortOrder>

    suspend fun setBackgroundRefreshEnabled(enabled: Boolean)
    suspend fun setBackgroundRefreshIntervalMinutes(minutes: Int)
    suspend fun setNotificationsEnabled(enabled: Boolean)
    suspend fun setConfettiEnabled(enabled: Boolean)
    suspend fun setHasCompletedOnboarding(completed: Boolean)
    suspend fun setSortOrder(order: SortOrder)
}

enum class SortOrder(val displayName: String) {
    DATE_DESC("登録日（新しい順）"),
    DATE_ASC("登録日（古い順）"),
    STATUS("ステータス順"),
    CARRIER("配送会社順")
}
