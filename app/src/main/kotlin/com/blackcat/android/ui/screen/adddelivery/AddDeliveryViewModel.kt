package com.blackcat.android.ui.screen.adddelivery

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blackcat.android.domain.model.DeliveryCarrier
import com.blackcat.android.domain.usecase.AddDeliveryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AddDeliveryUiState(
    val trackingNumber: String = "",
    val selectedCarrier: DeliveryCarrier = DeliveryCarrier.YAMATO,
    val isLoading: Boolean = false,
    val isButtonEnabled: Boolean = false,
    val cautionMessage: String = "",
    val errorMessage: String = "",
    val isSuccessfullyAdded: Boolean = false
)

@HiltViewModel
class AddDeliveryViewModel @Inject constructor(
    private val addDeliveryUseCase: AddDeliveryUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddDeliveryUiState())
    val uiState: StateFlow<AddDeliveryUiState> = _uiState.asStateFlow()

    fun updateTrackingNumber(number: String) {
        val cleaned = number.replace("-", "").replace(" ", "")
        val carrier = _uiState.value.selectedCarrier

        val caution = when {
            cleaned.isEmpty() -> ""
            !cleaned.all { it.isDigit() || it.isLetter() } -> "半角数字で入力してください"
            cleaned.length < carrier.trackingNumberLength.first -> "桁数が足りません（${carrier.trackingNumberLength.first}桁）"
            cleaned.length > carrier.trackingNumberLength.last -> "桁数が多すぎます（${carrier.trackingNumberLength.last}桁）"
            else -> ""
        }

        val isValid = cleaned.length in carrier.trackingNumberLength && caution.isEmpty()

        _uiState.value = _uiState.value.copy(
            trackingNumber = number,
            isButtonEnabled = isValid,
            cautionMessage = caution,
            errorMessage = ""
        )
    }

    fun updateCarrier(carrier: DeliveryCarrier) {
        _uiState.value = _uiState.value.copy(selectedCarrier = carrier)
        updateTrackingNumber(_uiState.value.trackingNumber)
    }

    fun addDelivery() {
        val state = _uiState.value
        if (!state.isButtonEnabled || state.isLoading) return

        viewModelScope.launch {
            _uiState.value = state.copy(isLoading = true, errorMessage = "")

            val result = addDeliveryUseCase(
                trackingNumber = state.trackingNumber.replace("-", "").replace(" ", ""),
                carrier = state.selectedCarrier
            )

            result.fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isSuccessfullyAdded = true
                    )
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = error.message ?: "追加に失敗しました"
                    )
                }
            )
        }
    }
}
