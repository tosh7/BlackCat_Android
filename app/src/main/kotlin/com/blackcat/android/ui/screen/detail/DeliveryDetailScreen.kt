package com.blackcat.android.ui.screen.detail

import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.blackcat.android.domain.model.DeliveryStatus
import com.blackcat.android.domain.model.DeliveryStatusType
import com.blackcat.android.ui.theme.AccentPrimary
import com.blackcat.android.ui.theme.BackgroundCard
import com.blackcat.android.ui.theme.BackgroundPrimary
import com.blackcat.android.ui.theme.NaturalRed
import com.blackcat.android.ui.theme.ShadowLevel3
import com.blackcat.android.ui.theme.ShadowLevel5
import com.blackcat.android.ui.theme.StatusDelivered
import com.blackcat.android.ui.theme.StatusInTransit
import com.blackcat.android.ui.theme.StatusOutForDelivery
import com.blackcat.android.ui.theme.StatusReceived
import com.blackcat.android.ui.theme.StatusShipped

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeliveryDetailScreen(
    deliveryId: Long,
    onNavigateBack: () -> Unit,
    viewModel: DeliveryDetailViewModel = hiltViewModel()
) {
    val delivery by viewModel.delivery.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    val isDeleted by viewModel.isDeleted.collectAsState()
    val context = LocalContext.current
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(deliveryId) {
        viewModel.loadDelivery(deliveryId)
        viewModel.refresh(deliveryId)
    }

    LaunchedEffect(isDeleted) {
        if (isDeleted) onNavigateBack()
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("削除確認") },
            text = { Text("この荷物を削除しますか？") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.delete(deliveryId)
                    showDeleteDialog = false
                }) {
                    Text("削除", color = NaturalRed)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("キャンセル")
                }
            },
            containerColor = BackgroundCard,
            titleContentColor = Color.White,
            textContentColor = ShadowLevel3
        )
    }

    Scaffold(
        containerColor = BackgroundPrimary,
        topBar = {
            TopAppBar(
                title = { Text("配達詳細", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "戻る", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.share(context) }) {
                        Icon(Icons.Filled.Share, "共有", tint = ShadowLevel3)
                    }
                    IconButton(onClick = { viewModel.refresh(deliveryId) }) {
                        Icon(Icons.Filled.Refresh, "更新", tint = ShadowLevel3)
                    }
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(Icons.Filled.Delete, "削除", tint = NaturalRed)
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
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            delivery?.let { item ->
                // Status header
                val statusColor = when (item.latestStatusType) {
                    DeliveryStatusType.RECEIVED -> StatusReceived
                    DeliveryStatusType.SENT -> StatusShipped
                    DeliveryStatusType.IN_TRANSIT -> StatusInTransit
                    DeliveryStatusType.OUT_FOR_DELIVERY -> StatusOutForDelivery
                    DeliveryStatusType.DELIVERED -> StatusDelivered
                    null -> ShadowLevel3
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(BackgroundCard)
                        .padding(24.dp)
                ) {
                    Column {
                        Text(
                            text = item.latestStatusType?.displayName ?: "不明",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = statusColor
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = item.formattedTrackingNumber,
                            fontSize = 18.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = item.carrier.displayName,
                            fontSize = 14.sp,
                            color = ShadowLevel3
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Progress bar
                        val progress = when (item.latestStatusType) {
                            DeliveryStatusType.RECEIVED -> 0.2f
                            DeliveryStatusType.SENT -> 0.4f
                            DeliveryStatusType.IN_TRANSIT -> 0.6f
                            DeliveryStatusType.OUT_FOR_DELIVERY -> 0.8f
                            DeliveryStatusType.DELIVERED -> 1.0f
                            null -> 0f
                        }
                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = statusColor,
                            trackColor = ShadowLevel5.copy(alpha = 0.3f)
                        )
                    }
                }

                if (isRefreshing) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = AccentPrimary,
                            strokeWidth = 2.dp
                        )
                    }
                }

                // Timeline
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "配達履歴",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(16.dp))

                item.statusList.reversed().forEachIndexed { index, status ->
                    TimelineItem(
                        status = status,
                        isFirst = index == 0,
                        isLast = index == item.statusList.size - 1
                    )
                }
            }
        }
    }
}

@Composable
private fun TimelineItem(
    status: DeliveryStatus,
    isFirst: Boolean,
    isLast: Boolean
) {
    val statusColor = when (status.statusType) {
        DeliveryStatusType.RECEIVED -> StatusReceived
        DeliveryStatusType.SENT -> StatusShipped
        DeliveryStatusType.IN_TRANSIT -> StatusInTransit
        DeliveryStatusType.OUT_FOR_DELIVERY -> StatusOutForDelivery
        DeliveryStatusType.DELIVERED -> StatusDelivered
    }

    Row(modifier = Modifier.fillMaxWidth()) {
        // Timeline line and dot
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(32.dp)
        ) {
            if (!isFirst) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(16.dp)
                        .background(ShadowLevel5.copy(alpha = 0.3f))
                )
            } else {
                Spacer(modifier = Modifier.height(16.dp))
            }
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(if (isFirst) statusColor else ShadowLevel5)
            )
            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(40.dp)
                        .background(ShadowLevel5.copy(alpha = 0.3f))
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Content
        Column(modifier = Modifier.padding(top = 8.dp, bottom = 16.dp)) {
            Text(
                text = status.status,
                fontSize = 14.sp,
                color = if (isFirst) Color.White else ShadowLevel3,
                fontWeight = if (isFirst) FontWeight.Bold else FontWeight.Normal
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row {
                Text(
                    text = status.date,
                    fontSize = 12.sp,
                    color = ShadowLevel5
                )
                status.time?.let {
                    Text(
                        text = " $it",
                        fontSize = 12.sp,
                        color = ShadowLevel5
                    )
                }
            }
            if (status.location.isNotEmpty()) {
                Text(
                    text = status.location,
                    fontSize = 12.sp,
                    color = ShadowLevel5
                )
            }
        }
    }
}
