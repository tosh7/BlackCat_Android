package com.blackcat.android.ui.screen.adddelivery

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.blackcat.android.domain.model.DeliveryCarrier
import com.blackcat.android.ui.theme.AccentPrimary
import com.blackcat.android.ui.theme.BackgroundCard
import com.blackcat.android.ui.theme.BackgroundPrimary
import com.blackcat.android.ui.theme.BackgroundSecondary
import com.blackcat.android.ui.theme.NaturalRed
import com.blackcat.android.ui.theme.PrimaryOrange
import com.blackcat.android.ui.theme.ShadowLevel3
import com.blackcat.android.ui.theme.ShadowLevel5

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddDeliveryScreen(
    onNavigateBack: () -> Unit,
    onDeliveryAdded: () -> Unit,
    viewModel: AddDeliveryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.isSuccessfullyAdded) {
        if (uiState.isSuccessfullyAdded) {
            onDeliveryAdded()
        }
    }

    Scaffold(
        containerColor = BackgroundPrimary,
        topBar = {
            TopAppBar(
                title = { Text("荷物を追加", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "戻る", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BackgroundPrimary,
                    titleContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp)
        ) {
            // Carrier selection
            Text(
                text = "配送業者",
                fontSize = 16.sp,
                color = ShadowLevel3,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                DeliveryCarrier.entries.forEach { carrier ->
                    CarrierChip(
                        carrier = carrier,
                        isSelected = uiState.selectedCarrier == carrier,
                        onClick = { viewModel.updateCarrier(carrier) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Tracking number input
            Text(
                text = "伝票番号",
                fontSize = 16.sp,
                color = ShadowLevel3,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(12.dp))
            TextField(
                value = uiState.trackingNumber,
                onValueChange = viewModel::updateTrackingNumber,
                placeholder = { Text("伝票番号を入力", color = ShadowLevel5) },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = BackgroundSecondary,
                    unfocusedContainerColor = BackgroundSecondary,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedIndicatorColor = AccentPrimary,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                leadingIcon = {
                    Icon(Icons.Filled.LocalShipping, null, tint = ShadowLevel3)
                }
            )

            // Caution message
            if (uiState.cautionMessage.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = uiState.cautionMessage,
                    fontSize = 12.sp,
                    color = PrimaryOrange
                )
            }

            // Error message
            if (uiState.errorMessage.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = uiState.errorMessage,
                    fontSize = 12.sp,
                    color = NaturalRed
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Add button
            Button(
                onClick = viewModel::addDelivery,
                enabled = uiState.isButtonEnabled && !uiState.isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AccentPrimary,
                    disabledContainerColor = AccentPrimary.copy(alpha = 0.3f)
                )
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        "追加する",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
private fun CarrierChip(
    carrier: DeliveryCarrier,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) AccentPrimary.copy(alpha = 0.2f) else BackgroundCard)
            .border(
                width = if (isSelected) 2.dp else 0.dp,
                color = if (isSelected) AccentPrimary else Color.Transparent,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp, horizontal = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = carrier.displayName,
            fontSize = 12.sp,
            color = if (isSelected) AccentPrimary else ShadowLevel3,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}
