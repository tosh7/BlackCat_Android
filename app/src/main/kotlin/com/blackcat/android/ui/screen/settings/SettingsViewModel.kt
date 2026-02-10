package com.blackcat.android.ui.screen.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blackcat.android.domain.repository.DeliveryRepository
import com.blackcat.android.domain.repository.SettingsRepository
import com.blackcat.android.domain.repository.SortOrder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val deliveryRepository: DeliveryRepository
) : ViewModel() {

    val backgroundRefreshEnabled = settingsRepository.backgroundRefreshEnabled
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val backgroundRefreshInterval = settingsRepository.backgroundRefreshIntervalMinutes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 60)

    val notificationsEnabled = settingsRepository.notificationsEnabled
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val confettiEnabled = settingsRepository.confettiEnabled
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val sortOrder = settingsRepository.sortOrder
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SortOrder.DATE_DESC)

    fun setBackgroundRefreshEnabled(enabled: Boolean) {
        viewModelScope.launch { settingsRepository.setBackgroundRefreshEnabled(enabled) }
    }

    fun setBackgroundRefreshInterval(minutes: Int) {
        viewModelScope.launch { settingsRepository.setBackgroundRefreshIntervalMinutes(minutes) }
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        viewModelScope.launch { settingsRepository.setNotificationsEnabled(enabled) }
    }

    fun setConfettiEnabled(enabled: Boolean) {
        viewModelScope.launch { settingsRepository.setConfettiEnabled(enabled) }
    }

    fun setSortOrder(order: SortOrder) {
        viewModelScope.launch { settingsRepository.setSortOrder(order) }
    }

    fun deleteAllData() {
        viewModelScope.launch { deliveryRepository.deleteAllDeliveries() }
    }
}
