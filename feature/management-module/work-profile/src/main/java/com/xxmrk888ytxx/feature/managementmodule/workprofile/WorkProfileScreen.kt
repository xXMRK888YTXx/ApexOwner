package com.xxmrk888ytxx.feature.managementmodule.workprofile

import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Switch
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.xxmrk888ytxx.core.base.android.mvi.SideEffect
import com.xxmrk888ytxx.core.base.compose.LocalNavigator
import com.xxmrk888ytxx.core.base.compose.extension.HandleSideEffects
import com.xxmrk888ytxx.core.base.compose.ui.CoreTopBar
import com.xxmrk888ytxx.feature.managementmodule.workprofile.model.WorkProfileApp
import com.xxmrk888ytxx.feature.managementmodule.workprofile.model.WorkProfileScreenState
import com.xxmrk888ytxx.feature.managementmodule.workprofile.model.WorkProfileSideEffect
import com.xxmrk888ytxx.feature.managementmodule.workprofile.model.WorkProfileTab
import com.xxmrk888ytxx.feature.managementmodule.workprofile.model.WorkProfileUiEvent
import kotlinx.coroutines.flow.Flow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkProfileScreen(
    state: WorkProfileScreenState,
    onEvent: (WorkProfileUiEvent) -> Unit,
    sideEffect: Flow<SideEffect>
) {
    val navigator = LocalNavigator.current

    HandleSideEffects<WorkProfileSideEffect>(sideEffect) { effect ->
        when (effect) {
            WorkProfileSideEffect.NavigateBack -> navigator.navigateUp()
        }
    }

    val currentList = when (state.selectedTab) {
        WorkProfileTab.PROFILE_APPS -> state.profileApps
        WorkProfileTab.CLONE_APPS -> state.personalApps
    }

    val filteredApps = remember(currentList, state.searchQuery, state.showSystemApps) {
        currentList.filter { app ->
            (state.showSystemApps || !app.isSystem) &&
                (app.name.contains(state.searchQuery, ignoreCase = true) ||
                    app.packageName.contains(state.searchQuery, ignoreCase = true))
        }
    }

    var showDeleteConfirmDialog by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(false) }

    if (showDeleteConfirmDialog) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { showDeleteConfirmDialog = false },
            title = { Text(stringResource(R.string.remove_work_profile_btn)) },
            text = { Text("Вы уверены, что хотите удалить рабочий профиль? Все данные и приложения внутри рабочего профиля будут стерты.") },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteConfirmDialog = false
                        onEvent(WorkProfileUiEvent.OnRemoveWorkProfile)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Удалить")
                }
            },
            dismissButton = {
                androidx.compose.material3.TextButton(onClick = { showDeleteConfirmDialog = false }) {
                    Text("Отмена")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            CoreTopBar(
                title = { Text(stringResource(R.string.work_profile_title)) },
                isShouldShowBackArrow = true,
                onNavigateBack = { onEvent(WorkProfileUiEvent.OnNavigateBack) },
                actions = {
                    if (state.hasWorkProfile) {
                        IconButton(onClick = { showDeleteConfirmDialog = true }) {
                            Text("🗑️", style = MaterialTheme.typography.titleMedium)
                        }
                    }
                    IconButton(onClick = { onEvent(WorkProfileUiEvent.OnRefresh) }) {
                        Icon(
                            painter = painterResource(R.drawable.ic_refresh),
                            contentDescription = "Refresh",
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (!state.isInsideWorkProfile && state.hasWorkProfile) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = stringResource(R.string.work_profile_host_banner),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        FilledTonalButton(
                            onClick = { onEvent(WorkProfileUiEvent.OnOpenWorkProfileApp) },
                            contentPadding = ButtonDefaults.ContentPadding
                        ) {
                            Text("💼")
                        }
                    }
                }
            }

            SecondaryTabRow(
                state.selectedTab.ordinal,
                Modifier.fillMaxWidth(),
                TabRowDefaults.primaryContainerColor,
                TabRowDefaults.primaryContentColor,
                @Composable { tabPositions ->
                    if (state.selectedTab.ordinal < tabPositions.size) {
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[state.selectedTab.ordinal])
                        )
                    }
                },
                @Composable { HorizontalDivider() },
                {
                    Tab(
                        selected = state.selectedTab == WorkProfileTab.PROFILE_APPS,
                        onClick = { onEvent(WorkProfileUiEvent.OnTabSelected(WorkProfileTab.PROFILE_APPS)) },
                        text = {
                            Text(
                                "${stringResource(R.string.profile_apps_tab)} (${state.profileApps.size})"
                            )
                        }
                    )
                    Tab(
                        selected = state.selectedTab == WorkProfileTab.CLONE_APPS,
                        onClick = { onEvent(WorkProfileUiEvent.OnTabSelected(WorkProfileTab.CLONE_APPS)) },
                        text = {
                            Text(
                                "${stringResource(R.string.clone_apps_tab)} (${state.personalApps.size})"
                            )
                        }
                    )
                })

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                OutlinedTextField(
                    value = state.searchQuery,
                    onValueChange = { onEvent(WorkProfileUiEvent.OnSearchQueryChanged(it)) },
                    placeholder = { Text(stringResource(R.string.search_apps_hint)) },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.ic_search),
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = stringResource(R.string.show_system_apps),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Switch(
                        checked = state.showSystemApps,
                        onCheckedChange = { onEvent(WorkProfileUiEvent.OnToggleSystemApps(it)) }
                    )
                }
            }

            if (state.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(modifier = Modifier.size(28.dp))
                }
            }

            if (filteredApps.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.no_apps_found),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(
                        items = filteredApps,
                        key = { it.packageName }
                    ) { app ->
                        AppListItem(
                            app = app,
                            tab = state.selectedTab,
                            onToggleFreeze = {
                                onEvent(WorkProfileUiEvent.OnToggleFreezeApp(app.packageName, !app.isFrozen))
                            },
                            onClone = {
                                onEvent(WorkProfileUiEvent.OnCloneApp(app.packageName))
                            },
                            onLaunch = {
                                onEvent(WorkProfileUiEvent.OnLaunchApp(app.packageName))
                            }
                        )
                    }
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun AppListItem(
    app: WorkProfileApp,
    tab: WorkProfileTab,
    onToggleFreeze: () -> Unit,
    onClone: () -> Unit,
    onLaunch: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (app.isFrozen) {
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            }
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AppIconImage(
                packageName = app.packageName,
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(8.dp))
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = app.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = app.packageName,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Row(
                    modifier = Modifier.padding(top = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    if (app.isSystem) {
                        BadgeTag(
                            text = stringResource(R.string.badge_system),
                            containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    if (tab == WorkProfileTab.PROFILE_APPS) {
                        if (app.isFrozen) {
                            BadgeTag(
                                text = stringResource(R.string.badge_frozen),
                                containerColor = MaterialTheme.colorScheme.errorContainer,
                                contentColor = MaterialTheme.colorScheme.onErrorContainer
                            )
                        } else {
                            BadgeTag(
                                text = stringResource(R.string.badge_active),
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            when (tab) {
                WorkProfileTab.PROFILE_APPS -> {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onLaunch) {
                            Icon(
                                painter = painterResource(R.drawable.ic_play),
                                contentDescription = stringResource(R.string.launch_action),
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        FilledTonalIconButton(
                            onClick = onToggleFreeze,
                            colors = if (app.isFrozen) {
                                androidx.compose.material3.IconButtonDefaults.filledTonalIconButtonColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer
                                )
                            } else {
                                androidx.compose.material3.IconButtonDefaults.filledTonalIconButtonColors(
                                    containerColor = MaterialTheme.colorScheme.errorContainer
                                )
                            }
                        ) {
                            Icon(
                                painter = painterResource(
                                    if (app.isFrozen) R.drawable.ic_wb_sunny else R.drawable.ic_ac_unit
                                ),
                                contentDescription = if (app.isFrozen) {
                                    stringResource(R.string.unfreeze_action)
                                } else {
                                    stringResource(R.string.freeze_action)
                                },
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                WorkProfileTab.CLONE_APPS -> {
                    if (app.isInstalledInWorkProfile) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(end = 4.dp)
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_check),
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    } else {
                        Button(
                            onClick = onClone,
                            contentPadding = ButtonDefaults.ContentPadding
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_add),
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(stringResource(R.string.clone_action))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BadgeTag(
    text: String,
    containerColor: androidx.compose.ui.graphics.Color,
    contentColor: androidx.compose.ui.graphics.Color
) {
    Box(
        modifier = Modifier
            .background(containerColor, RoundedCornerShape(4.dp))
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = contentColor
        )
    }
}

@Composable
private fun AppIconImage(
    packageName: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val iconBitmap = remember(packageName) {
        try {
            val drawable = context.packageManager.getApplicationIcon(packageName)
            val bitmap = Bitmap.createBitmap(
                drawable.intrinsicWidth.coerceAtLeast(64),
                drawable.intrinsicHeight.coerceAtLeast(64),
                Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
            bitmap.asImageBitmap()
        } catch (_: Exception) {
            null
        }
    }

    if (iconBitmap != null) {
        Image(
            bitmap = iconBitmap,
            contentDescription = null,
            modifier = modifier
        )
    } else {
        Box(
            modifier = modifier.background(
                MaterialTheme.colorScheme.surfaceContainerHighest,
                RoundedCornerShape(8.dp)
            )
        )
    }
}
