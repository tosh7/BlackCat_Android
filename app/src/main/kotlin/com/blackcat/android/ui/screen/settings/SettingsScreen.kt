package com.blackcat.android.ui.screen.settings

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.blackcat.android.domain.repository.SortOrder
import com.blackcat.android.ui.theme.AccentPrimary
import com.blackcat.android.ui.theme.BackgroundCard
import com.blackcat.android.ui.theme.BackgroundPrimary
import com.blackcat.android.ui.theme.NaturalRed
import com.blackcat.android.ui.theme.ShadowLevel3
import com.blackcat.android.ui.theme.ShadowLevel5

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val backgroundRefreshEnabled by viewModel.backgroundRefreshEnabled.collectAsState()
    val notificationsEnabled by viewModel.notificationsEnabled.collectAsState()
    val confettiEnabled by viewModel.confettiEnabled.collectAsState()
    val sortOrder by viewModel.sortOrder.collectAsState()
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("全データ削除") },
            text = { Text("すべての荷物データを削除しますか？\nこの操作は取り消せません。") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteAllData()
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
                title = { Text("設定", fontWeight = FontWeight.Bold) },
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
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Background refresh section
            SettingsSection(title = "バックグラウンド更新", icon = Icons.Filled.Refresh) {
                SettingsToggle(
                    title = "バックグラウンド更新",
                    subtitle = "アプリを閉じていても自動で更新",
                    checked = backgroundRefreshEnabled,
                    onCheckedChange = viewModel::setBackgroundRefreshEnabled
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Notifications section
            SettingsSection(title = "通知", icon = Icons.Filled.Notifications) {
                SettingsToggle(
                    title = "配達状況更新通知",
                    subtitle = "ステータスが変わったら通知",
                    checked = notificationsEnabled,
                    onCheckedChange = viewModel::setNotificationsEnabled
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Display section
            SettingsSection(title = "表示", icon = Icons.Filled.Celebration) {
                SettingsToggle(
                    title = "配達完了アニメーション",
                    subtitle = "配達完了時に紙吹雪を表示",
                    checked = confettiEnabled,
                    onCheckedChange = viewModel::setConfettiEnabled
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Sort order
                Text("リスト表示順", fontSize = 14.sp, color = Color.White)
                Spacer(modifier = Modifier.height(8.dp))
                SortOrder.entries.forEach { order ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (sortOrder == order) AccentPrimary.copy(alpha = 0.2f) else Color.Transparent)
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.Sort,
                            null,
                            tint = if (sortOrder == order) AccentPrimary else ShadowLevel5,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        TextButton(onClick = { viewModel.setSortOrder(order) }) {
                            Text(
                                order.displayName,
                                color = if (sortOrder == order) AccentPrimary else ShadowLevel3,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Data management section
            SettingsSection(title = "データ管理", icon = Icons.Filled.Delete) {
                TextButton(onClick = { showDeleteDialog = true }) {
                    Text("全データを削除", color = NaturalRed, fontSize = 14.sp)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // App info section
            SettingsSection(title = "アプリ情報", icon = Icons.Filled.Info) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("バージョン", fontSize = 14.sp, color = Color.White)
                    Spacer(modifier = Modifier.weight(1f))
                    Text("1.0.0", fontSize = 14.sp, color = ShadowLevel3)
                }
            }
        }
    }
}

@Composable
private fun SettingsSection(
    title: String,
    icon: ImageVector,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(BackgroundCard)
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, tint = AccentPrimary, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(title, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }
        Spacer(modifier = Modifier.height(16.dp))
        content()
    }
}

@Composable
private fun SettingsToggle(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontSize = 14.sp, color = Color.White)
            Text(subtitle, fontSize = 12.sp, color = ShadowLevel5)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = AccentPrimary,
                uncheckedThumbColor = ShadowLevel3,
                uncheckedTrackColor = ShadowLevel5
            )
        )
    }
}
