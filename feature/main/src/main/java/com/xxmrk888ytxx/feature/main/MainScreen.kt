package com.xxmrk888ytxx.feature.main

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.xxmrk888ytxx.core.base.android.mvi.SideEffect
import com.xxmrk888ytxx.core.base.compose.extension.HandleSideEffects
import com.xxmrk888ytxx.core.base.compose.ui.CoreTopBar
import com.xxmrk888ytxx.feature.main.model.MainOperatingMode
import com.xxmrk888ytxx.feature.main.model.MainScreenEvent
import com.xxmrk888ytxx.feature.main.model.MainScreenSideEffect
import com.xxmrk888ytxx.feature.main.model.ScreenState
import kotlinx.coroutines.flow.Flow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    screenState: ScreenState,
    onEvent: (MainScreenEvent) -> Unit,
    sideEffect: Flow<SideEffect>
) {
    val createWorkProfileLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            onEvent(MainScreenEvent.OnWorkProfileCreatedSuccessfully)
        } else {
            onEvent(MainScreenEvent.OnWorkProfileCreationFailed)
        }
    }

    HandleSideEffects<MainScreenSideEffect>(sideEffect) { effect ->
        when (effect) {
            is MainScreenSideEffect.SendIntentForCreateWorkProfile ->
                createWorkProfileLauncher.launch(effect.createWorkProfileIntent)
        }
    }

    Scaffold(
        topBar = {
            CoreTopBar(
                title = { Text(stringResource(R.string.device_management)) },
                isShouldShowBackArrow = false,
                onNavigateBack = {}
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                androidx.compose.animation.AnimatedVisibility(visible = true, enter = fadeIn()) {
                    OperatingModeStatusCard(mode = screenState.operatingMode)
                }
            }

            item {
                Text(
                    text = stringResource(R.string.management_modules),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                )
            }

            item {
                androidx.compose.animation.AnimatedVisibility(visible = true, enter = fadeIn()) {
                    WorkProfileSectionCard(
                        screenState = screenState,
                        onOpenManagement = { onEvent(MainScreenEvent.OnWorkProfileManagementClicked) },
                        onCreateProfile = { onEvent(MainScreenEvent.OnWorkProfileButtonClicked) },
                        onToggleQuietMode = { onEvent(MainScreenEvent.OnToggleWorkProfileQuietMode(it)) }
                    )
                }
            }

            item {
                androidx.compose.animation.AnimatedVisibility(visible = true, enter = fadeIn()) {
                    DeviceOwnerRestrictionsCard(
                        screenState = screenState,
                        onClick = { onEvent(MainScreenEvent.OnDeviveRestrictionButtonClicked) }
                    )
                }
            }

            item {
                androidx.compose.animation.AnimatedVisibility(visible = true, enter = fadeIn()) {
                    GlobalPermissionsCard(
                        screenState = screenState,
                        onClick = { onEvent(MainScreenEvent.OnGlobalPermissionsButtonClicked) }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun OperatingModeStatusCard(mode: MainOperatingMode) {
    val (title, desc, iconRes, containerColor, contentColor) = when (mode) {
        MainOperatingMode.DEVICE_OWNER -> {
            Tuples5(
                stringResource(R.string.mode_device_owner),
                stringResource(R.string.permissions_granted_successfully_you_now_have_full_access_to_manage_device_policies_using_the_modules_below),
                R.drawable.shield,
                MaterialTheme.colorScheme.primaryContainer,
                MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
        MainOperatingMode.PROFILE_OWNER -> {
            Tuples5(
                stringResource(R.string.mode_profile_owner),
                stringResource(R.string.work_profile_status_created),
                R.drawable.work,
                MaterialTheme.colorScheme.primaryContainer,
                MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
        MainOperatingMode.PERSONAL_HOST -> {
            Tuples5(
                stringResource(R.string.mode_personal_host),
                stringResource(R.string.create_a_separate_profile_to_isolate_work_apps_and_data_from_personal_ones),
                R.drawable.shield_lock,
                MaterialTheme.colorScheme.surfaceVariant,
                MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(iconRes),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(36.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = desc,
                    style = MaterialTheme.typography.bodySmall,
                    color = contentColor.copy(alpha = 0.85f)
                )
            }
        }
    }
}

@Composable
private fun WorkProfileSectionCard(
    screenState: ScreenState,
    onOpenManagement: () -> Unit,
    onCreateProfile: () -> Unit,
    onToggleQuietMode: (Boolean) -> Unit
) {
    val isProfileActive = screenState.hasWorkProfile || screenState.operatingMode == MainOperatingMode.PROFILE_OWNER

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(R.drawable.work),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(32.dp)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.open_work_profile_shelter),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = if (isProfileActive) {
                            stringResource(R.string.shelter_management_desc)
                        } else {
                            stringResource(R.string.work_profile_status_not_created)
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (isProfileActive) {
                Button(
                    onClick = onOpenManagement,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(stringResource(R.string.open_work_profile_shelter))
                }

                if (screenState.operatingMode != MainOperatingMode.PROFILE_OWNER) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = stringResource(R.string.quiet_mode_toggle),
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Switch(
                            checked = screenState.isWorkProfileQuietMode,
                            onCheckedChange = onToggleQuietMode
                        )
                    }
                }
            } else {
                Button(
                    onClick = onCreateProfile,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(stringResource(R.string.create_work_profile_one_click))
                }
            }
        }
    }
}

@Composable
private fun DeviceOwnerRestrictionsCard(
    screenState: ScreenState,
    onClick: () -> Unit
) {
    val isDeviceOwner = screenState.isDeviceOwner
    val isProfileOwner = screenState.operatingMode == MainOperatingMode.PROFILE_OWNER
    val hasWorkProfile = screenState.hasWorkProfile
    val isEnabled = isDeviceOwner || isProfileOwner || hasWorkProfile

    val title = when {
        isDeviceOwner -> stringResource(R.string.device_owner_restrictions_title)
        isProfileOwner || hasWorkProfile -> stringResource(R.string.work_profile_restrictions_title)
        else -> stringResource(R.string.device_owner_restrictions_title)
    }

    val desc = when {
        isDeviceOwner -> stringResource(R.string.device_owner_restrictions_desc)
        isProfileOwner || hasWorkProfile -> stringResource(R.string.work_profile_restrictions_desc)
        else -> stringResource(R.string.device_owner_not_granted_desc)
    }

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = isEnabled, onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(R.drawable.security),
                contentDescription = null,
                tint = if (isEnabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                modifier = Modifier.size(32.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = desc,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Icon(
                painter = painterResource(R.drawable.arrow_forward),
                contentDescription = null,
                tint = if (isEnabled) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.outline.copy(
                    alpha = 0.5f
                )
            )
        }
    }
}

@Composable
private fun GlobalPermissionsCard(
    screenState: ScreenState,
    onClick: () -> Unit
) {
    val isDeviceOwner = screenState.isDeviceOwner
    val isProfileOwner = screenState.operatingMode == MainOperatingMode.PROFILE_OWNER
    val hasWorkProfile = screenState.hasWorkProfile
    val isEnabled = isDeviceOwner || isProfileOwner || hasWorkProfile

    val title = when {
        isDeviceOwner -> stringResource(R.string.global_permissions_title_device)
        else -> stringResource(R.string.global_permissions_title_work_profile)
    }

    val desc = when {
        isDeviceOwner -> stringResource(R.string.global_permissions_desc_device)
        isProfileOwner || hasWorkProfile -> stringResource(R.string.global_permissions_desc_work_profile)
        else -> stringResource(R.string.device_owner_not_granted_desc)
    }

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = isEnabled, onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(R.drawable.shield),
                contentDescription = null,
                tint = if (isEnabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                modifier = Modifier.size(32.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = desc,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Icon(
                painter = painterResource(R.drawable.arrow_forward),
                contentDescription = null,
                tint = if (isEnabled) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
            )
        }
    }
}

private data class Tuples5<A, B, C, D, E>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D,
    val fifth: E
)
