package com.blackcat.android.ui.screen.deliverylist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blackcat.android.domain.model.DeliveryCarrier
import com.blackcat.android.domain.model.DeliveryItem
import com.blackcat.android.domain.model.DeliveryStatusType
import com.blackcat.android.domain.repository.DeliveryRepository
import com.blackcat.android.domain.repository.SettingsRepository
import com.blackcat.android.domain.repository.SortOrder
import com.blackcat.android.domain.usecase.RefreshDeliveryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class StatusFilter(val displayName: String) {
    ALL("すべて"),
    ACTIVE("配達中"),
    DELIVERED("配達完了")
}

enum class CarrierFilter(val displayName: String) {
    ALL("すべて"),
    YAMATO("ヤマト運輸"),
    SAGAWA("佐川急便"),
    JAPAN_POST("日本郵便")
}

@HiltViewModel
class DeliveryListViewModel @Inject constructor(
    private val repository: DeliveryRepository,
    private val refreshUseCase: RefreshDeliveryUseCase,
    settingsRepository: SettingsRepository
) : ViewModel() {

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _statusFilter = MutableStateFlow(StatusFilter.ALL)
    val statusFilter: StateFlow<StatusFilter> = _statusFilter.asStateFlow()

    private val _carrierFilter = MutableStateFlow(CarrierFilter.ALL)
    val carrierFilter: StateFlow<CarrierFilter> = _carrierFilter.asStateFlow()

    val sortOrder = settingsRepository.sortOrder.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), SortOrder.DATE_DESC
    )

    val deliveryList: StateFlow<List<DeliveryItem>> = combine(
        repository.getAllDeliveries(),
        _searchQuery,
        _statusFilter,
        _carrierFilter,
        sortOrder
    ) { deliveries, query, status, carrier, sort ->
        deliveries
            .filter { item ->
                if (query.isBlank()) true
                else item.trackingNumber.contains(query) || item.memo.contains(query)
            }
            .filter { item ->
                when (status) {
                    StatusFilter.ALL -> true
                    StatusFilter.ACTIVE -> !item.isDelivered
                    StatusFilter.DELIVERED -> item.isDelivered
                }
            }
            .filter { item ->
                when (carrier) {
                    CarrierFilter.ALL -> true
                    CarrierFilter.YAMATO -> item.carrier == DeliveryCarrier.YAMATO
                    CarrierFilter.SAGAWA -> item.carrier == DeliveryCarrier.SAGAWA
                    CarrierFilter.JAPAN_POST -> item.carrier == DeliveryCarrier.JAPAN_POST
                }
            }
            .let { filtered ->
                when (sort) {
                    SortOrder.DATE_DESC -> filtered.sortedByDescending { it.registeredDate }
                    SortOrder.DATE_ASC -> filtered.sortedBy { it.registeredDate }
                    SortOrder.STATUS -> filtered.sortedBy { it.latestStatusType?.ordinal ?: Int.MAX_VALUE }
                    SortOrder.CARRIER -> filtered.sortedBy { it.carrier.ordinal }
                }
            }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun updateStatusFilter(filter: StatusFilter) {
        _statusFilter.value = filter
    }

    fun updateCarrierFilter(filter: CarrierFilter) {
        _carrierFilter.value = filter
    }

    fun refreshAll() {
        viewModelScope.launch {
            _isRefreshing.value = true
            refreshUseCase.refreshAll()
            _isRefreshing.value = false
        }
    }
}
