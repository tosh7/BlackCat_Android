package com.blackcat.android.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.blackcat.android.domain.repository.SettingsRepository
import com.blackcat.android.domain.repository.SortOrder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UserPreferencesRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : SettingsRepository {

    private companion object {
        val BACKGROUND_REFRESH_ENABLED = booleanPreferencesKey("background_refresh_enabled")
        val BACKGROUND_REFRESH_INTERVAL = intPreferencesKey("background_refresh_interval")
        val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
        val CONFETTI_ENABLED = booleanPreferencesKey("confetti_enabled")
        val HAS_COMPLETED_ONBOARDING = booleanPreferencesKey("has_completed_onboarding")
        val SORT_ORDER = stringPreferencesKey("sort_order")
    }

    override val backgroundRefreshEnabled: Flow<Boolean> = dataStore.data.map {
        it[BACKGROUND_REFRESH_ENABLED] ?: true
    }

    override val backgroundRefreshIntervalMinutes: Flow<Int> = dataStore.data.map {
        it[BACKGROUND_REFRESH_INTERVAL] ?: 60
    }

    override val notificationsEnabled: Flow<Boolean> = dataStore.data.map {
        it[NOTIFICATIONS_ENABLED] ?: true
    }

    override val confettiEnabled: Flow<Boolean> = dataStore.data.map {
        it[CONFETTI_ENABLED] ?: true
    }

    override val hasCompletedOnboarding: Flow<Boolean> = dataStore.data.map {
        it[HAS_COMPLETED_ONBOARDING] ?: false
    }

    override val sortOrder: Flow<SortOrder> = dataStore.data.map {
        try {
            SortOrder.valueOf(it[SORT_ORDER] ?: SortOrder.DATE_DESC.name)
        } catch (e: Exception) {
            SortOrder.DATE_DESC
        }
    }

    override suspend fun setBackgroundRefreshEnabled(enabled: Boolean) {
        dataStore.edit { it[BACKGROUND_REFRESH_ENABLED] = enabled }
    }

    override suspend fun setBackgroundRefreshIntervalMinutes(minutes: Int) {
        dataStore.edit { it[BACKGROUND_REFRESH_INTERVAL] = minutes }
    }

    override suspend fun setNotificationsEnabled(enabled: Boolean) {
        dataStore.edit { it[NOTIFICATIONS_ENABLED] = enabled }
    }

    override suspend fun setConfettiEnabled(enabled: Boolean) {
        dataStore.edit { it[CONFETTI_ENABLED] = enabled }
    }

    override suspend fun setHasCompletedOnboarding(completed: Boolean) {
        dataStore.edit { it[HAS_COMPLETED_ONBOARDING] = completed }
    }

    override suspend fun setSortOrder(order: SortOrder) {
        dataStore.edit { it[SORT_ORDER] = order.name }
    }
}
