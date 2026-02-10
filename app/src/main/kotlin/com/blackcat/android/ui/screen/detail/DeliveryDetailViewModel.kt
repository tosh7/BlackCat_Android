package com.blackcat.android.ui.screen.detail

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blackcat.android.domain.model.DeliveryItem
import com.blackcat.android.domain.repository.DeliveryRepository
import com.blackcat.android.domain.repository.SettingsRepository
import com.blackcat.android.domain.usecase.DeleteDeliveryUseCase
import com.blackcat.android.domain.usecase.RefreshDeliveryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DeliveryDetailViewModel @Inject constructor(
    private val repository: DeliveryRepository,
    private val refreshUseCase: RefreshDeliveryUseCase,
    private val deleteUseCase: DeleteDeliveryUseCase,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _delivery = MutableStateFlow<DeliveryItem?>(null)
    val delivery: StateFlow<DeliveryItem?> = _delivery.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private val _isDeleted = MutableStateFlow(false)
    val isDeleted: StateFlow<Boolean> = _isDeleted.asStateFlow()

    private val _showConfetti = MutableStateFlow(false)
    val showConfetti: StateFlow<Boolean> = _showConfetti.asStateFlow()

    val confettiEnabled = settingsRepository.confettiEnabled.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), true
    )

    fun loadDelivery(id: Long) {
        viewModelScope.launch {
            _delivery.value = repository.getDeliveryById(id)
        }
    }

    fun refresh(id: Long) {
        viewModelScope.launch {
            _isRefreshing.value = true
            val previousStatus = _delivery.value?.latestStatusType
            refreshUseCase(id)
            _delivery.value = repository.getDeliveryById(id)
            _isRefreshing.value = false

            // Show confetti if delivery just completed
            val currentStatus = _delivery.value?.latestStatusType
            if (_delivery.value?.isDelivered == true && previousStatus != currentStatus) {
                if (confettiEnabled.value) {
                    _showConfetti.value = true
                }
            }
        }
    }

    fun delete(id: Long) {
        viewModelScope.launch {
            deleteUseCase(id)
            _isDeleted.value = true
        }
    }

    fun share(context: Context) {
        val item = _delivery.value ?: return
        val text = buildString {
            appendLine("【配達追跡情報】")
            appendLine("伝票番号: ${item.formattedTrackingNumber}")
            appendLine("配送業者: ${item.carrier.displayName}")
            item.latestStatus?.let { status ->
                appendLine("最新状態: ${status.status}")
                appendLine("日時: ${status.date} ${status.time ?: ""}")
                if (status.location.isNotEmpty()) {
                    appendLine("場所: ${status.location}")
                }
            }
        }
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)
        }
        context.startActivity(Intent.createChooser(intent, "配達情報を共有"))
    }

    fun dismissConfetti() {
        _showConfetti.value = false
    }
}
