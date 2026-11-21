package com.xxmrk888ytxx.feature.managementmodule.workprofile

import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.xxmrk888ytxx.core.base.android.mvi.SideEffect
import com.xxmrk888ytxx.core.base.compose.LocalNavigator
import com.xxmrk888ytxx.core.base.compose.extension.HandleSideEffects
import com.xxmrk888ytxx.core.base.compose.ui.CoreTopBar
import com.xxmrk888ytxx.feature.managementmodule.workprofile.model.AppInactivityPolicy
import com.xxmrk888ytxx.feature.managementmodule.workprofile.model.LockInactivityAction
import com.xxmrk888ytxx.feature.managementmodule.workprofile.model.WorkProfileApp
import com.xxmrk888ytxx.feature.managementmodule.workprofile.model.WorkProfileScreenState
import com.xxmrk888ytxx.feature.managementmodule.workprofile.model.WorkProfileSideEffect
import com.xxmrk888ytxx.feature.managementmodule.workprofile.model.WorkProfileUiEvent
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                onEvent(WorkProfileUiEvent.OnRefresh)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    val filteredApps = remember(state.apps, state.searchQuery, state.showSystemApps) {
        state.apps.filter { app ->
            (state.showSystemApps || !app.isSystem) &&
                (app.name.contains(state.searchQuery, ignoreCase = true) ||
                    app.packageName.contains(state.searchQuery, ignoreCase = true))
        }
    }

    var selectedAppForManagement by remember { mutableStateOf<WorkProfileApp?>(null) }

    // App Management Modal Bottom Sheet
    selectedAppForManagement?.let { app ->
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ModalBottomSheet(
            onDismissRequest = { selectedAppForManagement = null },
            sheetState = sheetState
        ) {
            AppManagementSheetContent(
                app = app,
                state = state,
                onLaunch = {
                    onEvent(WorkProfileUiEvent.OnLaunchApp(app.packageName))
                    selectedAppForManagement = null
                },
                onToggleFreeze = {
                    onEvent(WorkProfileUiEvent.OnToggleFreezeApp(app.packageName, !app.isFrozen))
                    selectedAppForManagement = null
                },
                onUninstall = {
                    val target = app
                    selectedAppForManagement = null
                    onEvent(WorkProfileUiEvent.OnUninstallApp(target.packageName))
                },
                onClone = {
                    onEvent(WorkProfileUiEvent.OnCloneApp(app.packageName))
                    selectedAppForManagement = null
                },
                onManagePermissions = {
                    val target = app
                    selectedAppForManagement = null
                    onEvent(WorkProfileUiEvent.OnManagePermissions(target.packageName, target.name))
                },
                onToggleCrossProfile = { isAllowed ->
                    onEvent(WorkProfileUiEvent.OnToggleCrossProfile(app.packageName, isAllowed))
                    selectedAppForManagement = selectedAppForManagement?.copy(isCrossProfileAllowed = isAllowed)
                },
                onToggleOneShot = { isOneShot ->
                    onEvent(WorkProfileUiEvent.OnToggleOneShot(app.packageName, isOneShot))
                    selectedAppForManagement = selectedAppForManagement?.copy(
                        isOneShot = isOneShot,
                        isAutoFreeze = !isOneShot && selectedAppForManagement!!.isAutoFreeze,
                        isFrozen = isOneShot || selectedAppForManagement!!.isFrozen
                    )
                },
                onResetOneShot = {
                    onEvent(WorkProfileUiEvent.OnResetOneShot(app.packageName))
                    selectedAppForManagement = selectedAppForManagement?.copy(
                        isFrozen = true
                    )
                },
                onToggleAutoFreeze = { isAutoFreeze ->
                    onEvent(WorkProfileUiEvent.OnToggleAutoFreeze(app.packageName, isAutoFreeze))
                    selectedAppForManagement = selectedAppForManagement?.copy(
                        isAutoFreeze = isAutoFreeze,
                        isOneShot = !isAutoFreeze && selectedAppForManagement!!.isOneShot,
                        isFrozen = isAutoFreeze || selectedAppForManagement!!.isFrozen
                    )
                },
                onUpdateInactivityPolicy = { policy ->
                    onEvent(WorkProfileUiEvent.OnUpdateInactivityPolicy(policy))
                    selectedAppForManagement = selectedAppForManagement?.copy(
                        inactivityPolicy = policy
                    )
                },
                onDismiss = { selectedAppForManagement = null }
            )
        }
    }

    Scaffold(
        topBar = {
            CoreTopBar(
                title = {
                    Text(
                        if (state.isInsideWorkProfile) {
                            stringResource(R.string.profile_work_shelter)
                        } else {
                            stringResource(R.string.profile_personal_shelter)
                        }
                    )
                },
                isShouldShowBackArrow = true,
                onNavigateBack = { onEvent(WorkProfileUiEvent.OnNavigateBack) }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Profile context switch banner
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

            // Search bar
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
                        text = stringResource(R.string.apps_count, filteredApps.size),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = stringResource(R.string.show_system_apps),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Switch(
                            checked = state.showSystemApps,
                            onCheckedChange = { onEvent(WorkProfileUiEvent.OnToggleSystemApps(it)) }
                        )
                    }
                }
            }

            PullToRefreshBox(
                isRefreshing = state.isLoading,
                onRefresh = { onEvent(WorkProfileUiEvent.OnRefresh) },
                state = rememberPullToRefreshState(),
                modifier = Modifier.fillMaxSize()
            ) {
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
                                modifier = Modifier.animateItem(),
                                app = app,
                                isInsideWorkProfile = state.isInsideWorkProfile,
                                onClick = { selectedAppForManagement = app }
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
}

@Composable
private fun AppListItem(
    modifier: Modifier = Modifier,
    app: WorkProfileApp,
    isInsideWorkProfile: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
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

                FlowRow(
                    modifier = Modifier.padding(top = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    if (app.isSystem) {
                        BadgeTag(
                            text = stringResource(R.string.badge_system),
                            containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    if (app.isOneShot) {
                        BadgeTag(
                            text = stringResource(R.string.badge_one_shot),
                            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                            contentColor = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                    }

                    if (app.isAutoFreeze) {
                        BadgeTag(
                            text = stringResource(R.string.badge_auto_freeze),
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }

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

                    if (app.isInstalledInOtherProfile) {
                        BadgeTag(
                            text = if (isInsideWorkProfile) {
                                stringResource(R.string.badge_in_personal_profile)
                            } else {
                                stringResource(R.string.badge_in_work_profile)
                            },
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }

                    app.inactivityPolicy?.let { policy ->
                        if (policy.lockAction == LockInactivityAction.FREEZE_ONLY) {
                            val text = if (policy.lockFreezeMinutes > 0) "⏱ ${policy.lockFreezeMinutes} мин" else stringResource(R.string.badge_lock_freeze, policy.lockFreezeHours)
                            BadgeTag(
                                text = text,
                                containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                                contentColor = MaterialTheme.colorScheme.onTertiaryContainer
                            )
                        } else if (policy.lockAction == LockInactivityAction.FREEZE_AND_WIPE) {
                            val freezeText = if (policy.lockFreezeMinutes > 0) "${policy.lockFreezeMinutes}м" else "${policy.lockFreezeHours}ч"
                            val wipeText = if (policy.lockWipeMinutes > 0) "${policy.lockWipeMinutes}м" else "${policy.lockWipeHours}ч"
                            BadgeTag(
                                text = "⏱ $freezeText / 🗑️ $wipeText",
                                containerColor = MaterialTheme.colorScheme.errorContainer,
                                contentColor = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                        if (policy.wipeIfUnusedEnabled) {
                            BadgeTag(
                                text = stringResource(R.string.badge_unused_wipe, policy.wipeIfUnusedDays),
                                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    }
                }
            }

        }
    }
}

@Composable
private fun AppManagementSheetContent(
    app: WorkProfileApp,
    state: WorkProfileScreenState,
    onLaunch: () -> Unit,
    onToggleFreeze: () -> Unit,
    onUninstall: () -> Unit,
    onClone: () -> Unit,
    onManagePermissions: () -> Unit,
    onToggleCrossProfile: (Boolean) -> Unit,
    onToggleOneShot: (Boolean) -> Unit,
    onResetOneShot: () -> Unit,
    onToggleAutoFreeze: (Boolean) -> Unit,
    onUpdateInactivityPolicy: (AppInactivityPolicy) -> Unit,
    onDismiss: () -> Unit
) {
    val canFreeze = state.canFreezeInCurrentProfile

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp)
            .padding(bottom = 36.dp, top = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AppIconImage(
            packageName = app.packageName,
            modifier = Modifier
                .size(64.dp)
                .clip(RoundedCornerShape(14.dp))
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = app.name,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Text(
            text = app.packageName,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(10.dp))

        FlowRow(
            modifier = Modifier.padding(top = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            BadgeTag(
                text = if (app.isSystem) stringResource(R.string.badge_system) else stringResource(R.string.badge_user),
                containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (app.isOneShot) {
                BadgeTag(
                    text = stringResource(R.string.badge_one_shot),
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                    contentColor = MaterialTheme.colorScheme.onTertiaryContainer
                )
            }

            if (app.isAutoFreeze) {
                BadgeTag(
                    text = stringResource(R.string.badge_auto_freeze),
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }

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

            if (app.isInstalledInOtherProfile) {
                BadgeTag(
                    text = if (state.isInsideWorkProfile) {
                        stringResource(R.string.badge_in_personal_profile)
                    } else {
                        stringResource(R.string.badge_in_work_profile)
                    },
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }

            app.inactivityPolicy?.let { policy ->
                if (policy.lockAction == LockInactivityAction.FREEZE_ONLY) {
                    val text = if (policy.lockFreezeMinutes > 0) "⏱ ${policy.lockFreezeMinutes} мин" else stringResource(R.string.badge_lock_freeze, policy.lockFreezeHours)
                    BadgeTag(
                        text = text,
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                        contentColor = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                } else if (policy.lockAction == LockInactivityAction.FREEZE_AND_WIPE) {
                    val freezeText = if (policy.lockFreezeMinutes > 0) "${policy.lockFreezeMinutes}м" else "${policy.lockFreezeHours}ч"
                    val wipeText = if (policy.lockWipeMinutes > 0) "${policy.lockWipeMinutes}м" else "${policy.lockWipeHours}ч"
                    BadgeTag(
                        text = "⏱ $freezeText / 🗑️ $wipeText",
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
                if (policy.wipeIfUnusedEnabled) {
                    BadgeTag(
                        text = stringResource(R.string.badge_unused_wipe, policy.wipeIfUnusedDays),
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))
        HorizontalDivider()
        Spacer(modifier = Modifier.height(18.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Launch Action
            Button(
                onClick = onLaunch,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_play),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(stringResource(R.string.launch_action))
            }

            // Cloning is available only when the other profile exists.
            if (state.hasWorkProfile) {
                Button(
                    onClick = onClone,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_add),
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        if (state.isInsideWorkProfile) {
                            stringResource(R.string.clone_to_personal_profile)
                        } else {
                            stringResource(R.string.clone_to_work_profile)
                        }
                    )
                }
            }

            // Permissions Management Action
            FilledTonalButton(
                onClick = onManagePermissions,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_check),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(stringResource(R.string.permissions_action))
            }

            if (state.hasWorkProfile) Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (app.isCrossProfileAllowed) {
                        MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
                    } else {
                        MaterialTheme.colorScheme.surfaceVariant
                    }
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = stringResource(R.string.cross_profile_title),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = stringResource(R.string.cross_profile_desc),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Switch(
                        checked = app.isCrossProfileAllowed,
                        onCheckedChange = onToggleCrossProfile
                    )
                }
            }

            // Freeze / Unfreeze Action
            if (canFreeze) {
                FilledTonalButton(
                    onClick = onToggleFreeze,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = if (app.isFrozen) {
                        ButtonDefaults.filledTonalButtonColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    } else {
                        ButtonDefaults.filledTonalButtonColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer,
                            contentColor = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                ) {
                    Icon(
                        painter = painterResource(
                            if (app.isFrozen) R.drawable.ic_wb_sunny else R.drawable.ic_ac_unit
                        ),
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        stringResource(if (app.isFrozen) R.string.unfreeze_action else R.string.freeze_action)
                    )
                }

                // One-shot Mode Toggle Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (app.isOneShot) {
                            MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.5f)
                        } else {
                            MaterialTheme.colorScheme.surfaceVariant
                        }
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = stringResource(R.string.one_shot_mode_title),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = stringResource(R.string.one_shot_mode_desc),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Switch(
                            checked = app.isOneShot,
                            onCheckedChange = { onToggleOneShot(it) }
                        )
                    }
                }

                if (app.isOneShot) {
                    FilledTonalButton(
                        onClick = onResetOneShot,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_ac_unit),
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(stringResource(R.string.reset_and_freeze_action))
                    }
                }

                // Auto-freeze Mode Toggle Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (app.isAutoFreeze) {
                            MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
                        } else {
                            MaterialTheme.colorScheme.surfaceVariant
                        }
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = stringResource(R.string.auto_freeze_mode_title),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = stringResource(R.string.auto_freeze_mode_desc),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Switch(
                            checked = app.isAutoFreeze,
                            onCheckedChange = { onToggleAutoFreeze(it) }
                        )
                    }
                }

                // Lock Inactivity Policy Card
                LockInactivityPolicyCard(
                    app = app,
                    onUpdateInactivityPolicy = onUpdateInactivityPolicy
                )

                // App Unused Wipe Policy Card
                AppUnusedPolicyCard(
                    app = app,
                    onUpdateInactivityPolicy = onUpdateInactivityPolicy
                )
            } else {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha = 0.6f)
                    )
                ) {
                    Text(
                        text = stringResource(R.string.freeze_not_supported_personal),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(12.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Uninstall Action
            OutlinedButton(
                onClick = onUninstall,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text(
                    text = "🗑️ ${stringResource(R.string.uninstall_action)}",
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun BadgeTag(
    text: String,
    containerColor: Color,
    contentColor: Color
) {
    Box(
        modifier = Modifier
            .background(containerColor, RoundedCornerShape(4.dp))
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = contentColor,
            maxLines = 1,
            softWrap = false
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
            val appInfo = context.packageManager.getApplicationInfo(
                packageName,
                PackageManager.MATCH_UNINSTALLED_PACKAGES or PackageManager.MATCH_DISABLED_COMPONENTS
            )
            val drawable = context.packageManager.getApplicationIcon(appInfo)
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

@Composable
private fun LockInactivityPolicyCard(
    app: WorkProfileApp,
    onUpdateInactivityPolicy: (AppInactivityPolicy) -> Unit
) {
    val currentPolicy = app.inactivityPolicy ?: remember(app.packageName) {
        AppInactivityPolicy(
            packageName = app.packageName,
            lockAction = LockInactivityAction.NONE,
            lockFreezeHours = 12,
            lockWipeHours = 12,
            wipeIfUnusedEnabled = false,
            wipeIfUnusedDays = 30
        )
    }

    val isEnabled = currentPolicy.lockAction != LockInactivityAction.NONE

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isEnabled) {
                MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.5f)
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            }
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.lock_inactivity_title),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = when (currentPolicy.lockAction) {
                            LockInactivityAction.NONE -> stringResource(R.string.lock_action_none_desc)
                            LockInactivityAction.FREEZE_ONLY -> {
                                if (currentPolicy.lockFreezeMinutes > 0) {
                                    stringResource(R.string.lock_freeze_after_minutes, currentPolicy.lockFreezeMinutes)
                                } else {
                                    stringResource(R.string.lock_freeze_summary, currentPolicy.lockFreezeHours)
                                }
                            }
                            LockInactivityAction.FREEZE_AND_WIPE -> {
                                val freezeStr = if (currentPolicy.lockFreezeMinutes > 0) "${currentPolicy.lockFreezeMinutes} мин" else "${currentPolicy.lockFreezeHours} ч"
                                val wipeStr = if (currentPolicy.lockWipeMinutes > 0) "${currentPolicy.lockWipeMinutes} мин" else "${currentPolicy.lockWipeHours} ч"
                                stringResource(R.string.lock_freeze_and_wipe_summary, freezeStr, wipeStr)
                            }
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Switch(
                    checked = isEnabled,
                    onCheckedChange = { checked ->
                        val newAction = if (checked) LockInactivityAction.FREEZE_ONLY else LockInactivityAction.NONE
                        onUpdateInactivityPolicy(currentPolicy.copy(lockAction = newAction))
                    }
                )
            }

            if (isEnabled) {
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = currentPolicy.lockAction == LockInactivityAction.FREEZE_ONLY,
                        onClick = {
                            onUpdateInactivityPolicy(currentPolicy.copy(lockAction = LockInactivityAction.FREEZE_ONLY))
                        },
                        label = { Text(stringResource(R.string.lock_chip_freeze_only)) }
                    )

                    FilterChip(
                        selected = currentPolicy.lockAction == LockInactivityAction.FREEZE_AND_WIPE,
                        onClick = {
                            val freezeH = if (currentPolicy.lockFreezeHours == 12) 6 else currentPolicy.lockFreezeHours
                            onUpdateInactivityPolicy(
                                currentPolicy.copy(
                                    lockAction = LockInactivityAction.FREEZE_AND_WIPE,
                                    lockFreezeHours = freezeH,
                                    lockWipeHours = 12
                                )
                            )
                        },
                        label = { Text(stringResource(R.string.lock_chip_freeze_wipe)) }
                    )
                }

                // Единицы измерения (Часы / Минуты для тестирования)
                val isMinutesMode = currentPolicy.lockFreezeMinutes > 0 || currentPolicy.lockWipeMinutes > 0
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.unit_label),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    FilterChip(
                        selected = !isMinutesMode,
                        onClick = {
                            onUpdateInactivityPolicy(
                                currentPolicy.copy(
                                    lockFreezeMinutes = 0,
                                    lockWipeMinutes = 0,
                                    lockFreezeHours = if (currentPolicy.lockFreezeHours <= 0) 12 else currentPolicy.lockFreezeHours,
                                    lockWipeHours = if (currentPolicy.lockWipeHours <= 0) 12 else currentPolicy.lockWipeHours
                                )
                            )
                        },
                        label = { Text(stringResource(R.string.unit_hours)) }
                    )
                    FilterChip(
                        selected = isMinutesMode,
                        onClick = {
                            onUpdateInactivityPolicy(
                                currentPolicy.copy(
                                    lockFreezeMinutes = if (currentPolicy.lockFreezeMinutes <= 0) 1 else currentPolicy.lockFreezeMinutes,
                                    lockWipeMinutes = if (currentPolicy.lockWipeMinutes <= 0) 2 else currentPolicy.lockWipeMinutes
                                )
                            )
                        },
                        label = { Text(stringResource(R.string.unit_minutes_test)) }
                    )
                }

                if (!isMinutesMode) {
                    if (currentPolicy.lockAction == LockInactivityAction.FREEZE_ONLY) {
                        SettingStepperRow(
                            label = stringResource(R.string.hours_without_unlock_label),
                            value = currentPolicy.lockFreezeHours,
                            unit = stringResource(R.string.unit_hour_short),
                            minValue = 1,
                            maxValue = 168,
                            onValueChange = { hours ->
                                onUpdateInactivityPolicy(currentPolicy.copy(lockFreezeHours = hours))
                            }
                        )
                    } else if (currentPolicy.lockAction == LockInactivityAction.FREEZE_AND_WIPE) {
                        SettingStepperRow(
                            label = stringResource(R.string.hours_to_freeze_label),
                            value = currentPolicy.lockFreezeHours,
                            unit = stringResource(R.string.unit_hour_short),
                            minValue = 1,
                            maxValue = currentPolicy.lockWipeHours,
                            onValueChange = { hours ->
                                onUpdateInactivityPolicy(currentPolicy.copy(lockFreezeHours = hours))
                            }
                        )
                        SettingStepperRow(
                            label = stringResource(R.string.hours_to_wipe_label),
                            value = currentPolicy.lockWipeHours,
                            unit = stringResource(R.string.unit_hour_short),
                            minValue = currentPolicy.lockFreezeHours,
                            maxValue = 168,
                            onValueChange = { hours ->
                                onUpdateInactivityPolicy(currentPolicy.copy(lockWipeHours = hours))
                            }
                        )
                    }
                } else {
                    if (currentPolicy.lockAction == LockInactivityAction.FREEZE_ONLY) {
                        SettingStepperRow(
                            label = stringResource(R.string.hours_without_unlock_label),
                            value = currentPolicy.lockFreezeMinutes,
                            unit = stringResource(R.string.unit_minute_short),
                            minValue = 1,
                            maxValue = 120,
                            onValueChange = { mins ->
                                onUpdateInactivityPolicy(currentPolicy.copy(lockFreezeMinutes = mins))
                            }
                        )
                    } else if (currentPolicy.lockAction == LockInactivityAction.FREEZE_AND_WIPE) {
                        SettingStepperRow(
                            label = stringResource(R.string.hours_to_freeze_label),
                            value = currentPolicy.lockFreezeMinutes,
                            unit = stringResource(R.string.unit_minute_short),
                            minValue = 1,
                            maxValue = currentPolicy.lockWipeMinutes,
                            onValueChange = { mins ->
                                onUpdateInactivityPolicy(currentPolicy.copy(lockFreezeMinutes = mins))
                            }
                        )
                        SettingStepperRow(
                            label = stringResource(R.string.hours_to_wipe_label),
                            value = currentPolicy.lockWipeMinutes,
                            unit = stringResource(R.string.unit_minute_short),
                            minValue = currentPolicy.lockFreezeMinutes,
                            maxValue = 120,
                            onValueChange = { mins ->
                                onUpdateInactivityPolicy(currentPolicy.copy(lockWipeMinutes = mins))
                            }
                        )
                    }
                }

                // Информационная панель
                Card(
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.55f)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(Color(0xFF4CAF50), CircleShape)
                            )
                            Text(
                                text = stringResource(R.string.lock_screen_unlocked_status),
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Text(
                            text = stringResource(R.string.lock_screen_timer_description),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))

                        val freezeText = if (currentPolicy.lockFreezeMinutes > 0) {
                            stringResource(R.string.sleep_after_minutes, currentPolicy.lockFreezeMinutes)
                        } else {
                            stringResource(R.string.sleep_after_hours, currentPolicy.lockFreezeHours)
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = stringResource(R.string.freeze_schedule_label),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = freezeText,
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        if (currentPolicy.lockAction == LockInactivityAction.FREEZE_AND_WIPE) {
                            val wipeText = if (currentPolicy.lockWipeMinutes > 0) {
                                stringResource(R.string.sleep_after_minutes, currentPolicy.lockWipeMinutes)
                            } else {
                                stringResource(R.string.sleep_after_hours, currentPolicy.lockWipeHours)
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = stringResource(R.string.wipe_schedule_label),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = wipeText,
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        }

                        if (currentPolicy.lastLockFreezeTimestamp > 0L || currentPolicy.lastLockWipeTimestamp > 0L) {
                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                            if (currentPolicy.lastLockFreezeTimestamp > 0L) {
                                Text(
                                    text = "❄️ Последняя заморозка: ${formatTargetDateTime(currentPolicy.lastLockFreezeTimestamp)}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            if (currentPolicy.lastLockWipeTimestamp > 0L) {
                                Text(
                                    text = "🗑️ Последний сброс данных: ${formatTargetDateTime(currentPolicy.lastLockWipeTimestamp)}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AppUnusedPolicyCard(
    app: WorkProfileApp,
    onUpdateInactivityPolicy: (AppInactivityPolicy) -> Unit
) {
    val currentPolicy = app.inactivityPolicy ?: remember(app.packageName) {
        AppInactivityPolicy(
            packageName = app.packageName,
            lockAction = LockInactivityAction.NONE,
            lockFreezeHours = 12,
            lockWipeHours = 12,
            wipeIfUnusedEnabled = false,
            wipeIfUnusedDays = 30
        )
    }

    val isFreezeEnabled = currentPolicy.freezeIfUnusedEnabled
    val isWipeEnabled = currentPolicy.wipeIfUnusedEnabled
    val isEnabled = isFreezeEnabled || isWipeEnabled
    val isFreezeMinutesMode = currentPolicy.freezeIfUnusedMinutes > 0
    val isWipeMinutesMode = currentPolicy.wipeIfUnusedMinutes > 0
    val isWipeHoursMode = !isWipeMinutesMode && currentPolicy.wipeIfUnusedHours > 0

    LaunchedEffect(isFreezeEnabled, isWipeEnabled, currentPolicy.lastOpenedTimestamp) {
        if (isEnabled && currentPolicy.lastOpenedTimestamp == 0L) {
            onUpdateInactivityPolicy(currentPolicy.copy(lastOpenedTimestamp = System.currentTimeMillis()))
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isWipeEnabled) {
                        MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
                    } else {
                        MaterialTheme.colorScheme.surfaceVariant
                    }
                )
            ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.app_unused_title),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = when {
                            isWipeEnabled ->
                                stringResource(
                                    R.string.app_unused_enabled_duration_desc,
                                    when {
                                        isWipeMinutesMode -> "${currentPolicy.wipeIfUnusedMinutes} ${stringResource(R.string.unit_minute_short)}"
                                        isWipeHoursMode -> "${currentPolicy.wipeIfUnusedHours} ${stringResource(R.string.unit_hour_short)}"
                                        else -> "${currentPolicy.wipeIfUnusedDays} ${stringResource(R.string.unit_day_short)}"
                                    }
                                )
                            else -> stringResource(R.string.app_unused_disabled_desc)
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Switch(
                    checked = isWipeEnabled,
                    onCheckedChange = { checked ->
                        val updated = currentPolicy.copy(
                            wipeIfUnusedEnabled = checked,
                            lastOpenedTimestamp = if (checked && currentPolicy.lastOpenedTimestamp == 0L) {
                                System.currentTimeMillis()
                            } else {
                                currentPolicy.lastOpenedTimestamp
                            }
                        )
                        onUpdateInactivityPolicy(updated)
                    }
                )
            }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isFreezeEnabled) {
                        MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.5f)
                    } else {
                        MaterialTheme.colorScheme.surfaceVariant
                    }
                )
            ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.freeze_if_unused_title),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = stringResource(
                            R.string.unused_freeze_desc,
                            when {
                                currentPolicy.freezeIfUnusedMinutes > 0 ->
                                    "${currentPolicy.freezeIfUnusedMinutes} ${stringResource(R.string.unit_minute_short)}"
                                currentPolicy.freezeIfUnusedHours > 0 ->
                                    "${currentPolicy.freezeIfUnusedHours} ${stringResource(R.string.unit_hour_short)}"
                                else ->
                                    "${currentPolicy.freezeIfUnusedDays} ${stringResource(R.string.unit_day_short)}"
                            }
                        ),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(
                    checked = isFreezeEnabled,
                    onCheckedChange = { checked ->
                        onUpdateInactivityPolicy(currentPolicy.copy(
                            freezeIfUnusedEnabled = checked,
                                freezeIfUnusedHours = if (checked && currentPolicy.freezeIfUnusedHours <= 0 && currentPolicy.freezeIfUnusedMinutes <= 0) {
                                    currentPolicy.freezeIfUnusedDays * 24
                                } else currentPolicy.freezeIfUnusedHours,
                            lastOpenedTimestamp = if (checked && currentPolicy.lastOpenedTimestamp == 0L) {
                                System.currentTimeMillis()
                            } else currentPolicy.lastOpenedTimestamp
                        ))
                    }
                )
            }
            }

            if (isFreezeEnabled) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = !isFreezeMinutesMode,
                        onClick = {
                            onUpdateInactivityPolicy(currentPolicy.copy(
                                freezeIfUnusedHours = if (currentPolicy.freezeIfUnusedHours > 0) currentPolicy.freezeIfUnusedHours else currentPolicy.freezeIfUnusedDays * 24,
                                freezeIfUnusedMinutes = 0
                            ))
                        },
                        label = { Text(stringResource(R.string.unit_hours)) }
                    )
                    FilterChip(
                        selected = isFreezeMinutesMode,
                        onClick = {
                            onUpdateInactivityPolicy(currentPolicy.copy(
                                freezeIfUnusedHours = 0,
                                freezeIfUnusedMinutes = if (currentPolicy.freezeIfUnusedMinutes > 0) currentPolicy.freezeIfUnusedMinutes else 1
                            ))
                        },
                        label = { Text(stringResource(R.string.unit_minutes_test)) }
                    )
                }
                SettingStepperRow(
                    label = stringResource(R.string.freeze_deadline_label),
                    value = if (isFreezeMinutesMode) currentPolicy.freezeIfUnusedMinutes
                    else if (currentPolicy.freezeIfUnusedHours > 0) currentPolicy.freezeIfUnusedHours
                    else currentPolicy.freezeIfUnusedDays * 24,
                    unit = if (isFreezeMinutesMode) stringResource(R.string.unit_minute_short) else stringResource(R.string.unit_hour_short),
                    minValue = 1,
                    maxValue = if (isFreezeMinutesMode) 120 else 720,
                    onValueChange = { value ->
                        onUpdateInactivityPolicy(if (isFreezeMinutesMode) {
                            currentPolicy.copy(freezeIfUnusedMinutes = value)
                        } else {
                            currentPolicy.copy(freezeIfUnusedHours = value, freezeIfUnusedDays = (value / 24).coerceAtLeast(1))
                        })
                    }
                )
                TextButton(
                    onClick = {
                        val pastTime = System.currentTimeMillis() - currentPolicy.unusedFreezeDurationMs - 60_000L
                        onUpdateInactivityPolicy(currentPolicy.copy(lastOpenedTimestamp = pastTime))
                    },
                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = stringResource(R.string.freeze_expired_test),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            if (isWipeEnabled) {
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = !isWipeMinutesMode && !isWipeHoursMode,
                        onClick = {
                            onUpdateInactivityPolicy(currentPolicy.copy(
                                wipeIfUnusedDays = currentPolicy.wipeIfUnusedDays.coerceAtLeast(1),
                                wipeIfUnusedHours = 0,
                                wipeIfUnusedMinutes = 0
                            ))
                        },
                        label = { Text(stringResource(R.string.unit_days)) }
                    )
                    FilterChip(
                        selected = isWipeHoursMode,
                        onClick = {
                            onUpdateInactivityPolicy(currentPolicy.copy(
                                wipeIfUnusedHours = if (currentPolicy.wipeIfUnusedHours > 0) currentPolicy.wipeIfUnusedHours else currentPolicy.wipeIfUnusedDays * 24,
                                wipeIfUnusedMinutes = 0
                            ))
                        },
                        label = { Text(stringResource(R.string.unit_hours)) }
                    )
                    FilterChip(
                        selected = isWipeMinutesMode,
                        onClick = {
                            onUpdateInactivityPolicy(currentPolicy.copy(
                                wipeIfUnusedHours = 0,
                                wipeIfUnusedMinutes = if (currentPolicy.wipeIfUnusedMinutes > 0) currentPolicy.wipeIfUnusedMinutes else 1
                            ))
                        },
                        label = { Text(stringResource(R.string.unit_minutes_test)) }
                    )
                }
                SettingStepperRow(
                    label = stringResource(R.string.days_unused_label),
                    value = when {
                        isWipeMinutesMode -> currentPolicy.wipeIfUnusedMinutes
                        isWipeHoursMode -> currentPolicy.wipeIfUnusedHours
                        else -> currentPolicy.wipeIfUnusedDays
                    },
                    unit = when {
                        isWipeMinutesMode -> stringResource(R.string.unit_minute_short)
                        isWipeHoursMode -> stringResource(R.string.unit_hour_short)
                        else -> stringResource(R.string.unit_day_short)
                    },
                    minValue = 1,
                    maxValue = when {
                        isWipeMinutesMode -> 120
                        isWipeHoursMode -> 720
                        else -> 365
                    },
                    onValueChange = { value ->
                        onUpdateInactivityPolicy(when {
                            isWipeMinutesMode -> currentPolicy.copy(wipeIfUnusedMinutes = value)
                            isWipeHoursMode -> currentPolicy.copy(wipeIfUnusedHours = value)
                            else -> currentPolicy.copy(wipeIfUnusedDays = value)
                        })
                    }
                )

                val baseTimestamp = if (currentPolicy.lastOpenedTimestamp > 0L) {
                    currentPolicy.lastOpenedTimestamp
                } else {
                    remember { System.currentTimeMillis() }
                }
                val wipeTimestamp = baseTimestamp + currentPolicy.unusedWipeDurationMs
                val now = System.currentTimeMillis()
                val diffMs = wipeTimestamp - now

                val remainingText = if (diffMs > 0) {
                    val days = diffMs / 86400_000L
                    val hours = (diffMs % 86400_000L) / 3600_000L
                    if (days > 0) "$days ${stringResource(R.string.unit_day_short)}. $hours ${stringResource(R.string.unit_hour_short)}" else "$hours ${stringResource(R.string.unit_hour_short)}"
                } else {
                    stringResource(R.string.unused_wipe_expired)
                }

                Card(
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.55f)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.exact_wipe_schedule),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = stringResource(R.string.wipe_date_time),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = formatTargetDateTime(wipeTimestamp),
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.error
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = stringResource(R.string.wipe_remaining),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = remainingText,
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        if (currentPolicy.lastUnusedWipeTimestamp > 0L) {
                            Text(
                                text = stringResource(R.string.last_wipe_time, formatTargetDateTime(currentPolicy.lastUnusedWipeTimestamp)),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.error
                            )
                        }

                    }
                }
            }
        }
    }

private fun formatTargetDateTime(timestampMs: Long): String {
    val sdf = SimpleDateFormat("d MMM yyyy, HH:mm", Locale.getDefault())
    return sdf.format(Date(timestampMs))
}

@Composable
private fun SettingStepperRow(
    label: String,
    value: Int,
    unit: String,
    minValue: Int = 1,
    maxValue: Int = 365,
    onValueChange: (Int) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            modifier = Modifier
                .background(
                    MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha = 0.7f),
                    RoundedCornerShape(8.dp)
                )
                .padding(horizontal = 4.dp, vertical = 2.dp)
        ) {
            IconButton(
                onClick = { if (value > minValue) onValueChange(value - 1) },
                enabled = value > minValue,
                modifier = Modifier.size(32.dp)
            ) {
                Text(
                    text = "−",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (value > minValue) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .clickable { showDialog = true }
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "$value $unit",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            IconButton(
                onClick = { if (value < maxValue) onValueChange(value + 1) },
                enabled = value < maxValue,
                modifier = Modifier.size(32.dp)
            ) {
                Text(
                    text = "+",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (value < maxValue) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                )
            }
        }
    }

    if (showDialog) {
        var textInput by remember { mutableStateOf(value.toString()) }
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(label) },
            text = {
                OutlinedTextField(
                    value = textInput,
                    onValueChange = { input ->
                        textInput = input.filter { it.isDigit() }.take(4)
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    suffix = { Text(unit) }
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val num = textInput.toIntOrNull()
                        if (num != null) {
                            onValueChange(num.coerceIn(minValue, maxValue))
                        }
                        showDialog = false
                    }
                ) {
                    Text(stringResource(R.string.ok_action))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
}
