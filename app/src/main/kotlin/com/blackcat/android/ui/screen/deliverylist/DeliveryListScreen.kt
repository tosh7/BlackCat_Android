package com.blackcat.android.ui.screen.deliverylist

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.blackcat.android.domain.model.DeliveryCarrier
import com.blackcat.android.domain.model.DeliveryItem
import com.blackcat.android.domain.model.DeliveryStatusType
import com.blackcat.android.ui.theme.AccentPrimary
import com.blackcat.android.ui.theme.BackgroundCard
import com.blackcat.android.ui.theme.BackgroundPrimary
import com.blackcat.android.ui.theme.BackgroundSecondary
import com.blackcat.android.ui.theme.ShadowLevel3
import com.blackcat.android.ui.theme.ShadowLevel5
import com.blackcat.android.ui.theme.StatusDelivered
import com.blackcat.android.ui.theme.StatusInTransit
import com.blackcat.android.ui.theme.StatusOutForDelivery
import com.blackcat.android.ui.theme.StatusReceived
import com.blackcat.android.ui.theme.StatusShipped

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeliveryListScreen(
    onNavigateToDetail: (Long) -> Unit,
    onNavigateToAdd: () -> Unit,
    onNavigateToSettings: () -> Unit,
    viewModel: DeliveryListViewModel = hiltViewModel()
) {
    val deliveryList by viewModel.deliveryList.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val statusFilter by viewModel.statusFilter.collectAsState()
    var showSearch by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = BackgroundPrimary,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "BlackCat",
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp
                    )
                },
                actions = {
                    IconButton(onClick = { showSearch = !showSearch }) {
                        Icon(Icons.Filled.Search, "検索", tint = ShadowLevel3)
                    }
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(Icons.Filled.Settings, "設定", tint = ShadowLevel3)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BackgroundPrimary,
                    titleContentColor = Color.White
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAdd,
                containerColor = AccentPrimary,
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Icon(Icons.Filled.Add, "追加")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Search bar
            AnimatedVisibility(visible = showSearch, enter = fadeIn(), exit = fadeOut()) {
                TextField(
                    value = searchQuery,
                    onValueChange = viewModel::updateSearchQuery,
                    placeholder = { Text("伝票番号で検索", color = ShadowLevel5) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
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
                    leadingIcon = { Icon(Icons.Filled.Search, null, tint = ShadowLevel3) }
                )
            }

            // Filter chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatusFilter.entries.forEach { filter ->
                    FilterChip(
                        selected = statusFilter == filter,
                        onClick = { viewModel.updateStatusFilter(filter) },
                        label = { Text(filter.displayName, fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = AccentPrimary,
                            selectedLabelColor = Color.White,
                            containerColor = BackgroundSecondary,
                            labelColor = ShadowLevel3
                        )
                    )
                }
            }

            // Content
            PullToRefreshBox(
                isRefreshing = isRefreshing,
                onRefresh = viewModel::refreshAll,
                modifier = Modifier.fillMaxSize()
            ) {
                if (deliveryList.isEmpty()) {
                    EmptyState()
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(deliveryList, key = { it.id }) { delivery ->
                            DeliveryCard(
                                delivery = delivery,
                                onClick = { onNavigateToDetail(delivery.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DeliveryCard(
    delivery: DeliveryItem,
    onClick: () -> Unit
) {
    val statusColor = when (delivery.latestStatusType) {
        DeliveryStatusType.RECEIVED -> StatusReceived
        DeliveryStatusType.SENT -> StatusShipped
        DeliveryStatusType.IN_TRANSIT -> StatusInTransit
        DeliveryStatusType.OUT_FOR_DELIVERY -> StatusOutForDelivery
        DeliveryStatusType.DELIVERED -> StatusDelivered
        null -> ShadowLevel3
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(BackgroundCard)
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {
        // Status indicator
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(statusColor)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = delivery.latestStatusType?.displayName ?: "不明",
                fontSize = 12.sp,
                color = statusColor,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Tracking number
        Text(
            text = delivery.formattedTrackingNumber,
            fontSize = 14.sp,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Carrier
        Text(
            text = delivery.carrier.displayName,
            fontSize = 12.sp,
            color = ShadowLevel3
        )

        // Latest status detail
        delivery.latestStatus?.let { status ->
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "${status.date} ${status.time ?: ""}",
                fontSize = 11.sp,
                color = ShadowLevel5
            )
            if (status.location.isNotEmpty()) {
                Text(
                    text = status.location,
                    fontSize = 11.sp,
                    color = ShadowLevel5,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun EmptyState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Filled.Inventory2,
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = ShadowLevel5
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "荷物がありません",
                fontSize = 20.sp,
                color = ShadowLevel3,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "右下の＋ボタンから\n荷物を追加しましょう",
                fontSize = 14.sp,
                color = ShadowLevel5,
                textAlign = TextAlign.Center
            )
        }
    }
}
