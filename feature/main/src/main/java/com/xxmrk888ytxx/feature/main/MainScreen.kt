package com.xxmrk888ytxx.feature.main

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.xxmrk888ytxx.core.base.android.mvi.SideEffect
import com.xxmrk888ytxx.core.base.compose.extension.HandleSideEffects
import com.xxmrk888ytxx.core.base.compose.ui.CoreTopBar
import com.xxmrk888ytxx.feature.main.model.DeviceOwnerModule
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
        when(effect) {
            is MainScreenSideEffect.SendIntentForCreateWorkProfile -> createWorkProfileLauncher.launch(effect.createWorkProfileIntent)
        }
    }
    val modules = remember {
        listOf(
            DeviceOwnerModule(
                id = 0,
                title = R.string.device_restrictions,
                description = R.string.limit_device_features_to_enhance_your_privacy_e_g_camera_microphone_usb_data_transfer,
                iconResId = R.drawable.security,
                onClick = { onEvent(MainScreenEvent.OnDeviveRestrictionButtonClicked) }
            ),
            DeviceOwnerModule(
                id = 1,
                title = R.string.work_profile_setup,
                description = R.string.create_a_separate_profile_to_isolate_work_apps_and_data_from_personal_ones,
                iconResId = R.drawable.work,
                onClick = { onEvent(MainScreenEvent.OnWorkProfileButtonClicked) }
            ),
        )
    }

    Scaffold(
        topBar = {
            CoreTopBar(
                title = {
                    Text(stringResource(R.string.device_management))
                },
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
                ProfileStatusCard(
                   isWorkProfile = screenState.isWorkProfile,
                    onCreateWorkProfileClick = { onEvent(MainScreenEvent.OnWorkProfileButtonClicked) }
                )
            }

            item {
                Text(
                    text = stringResource(R.string.management_modules),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                )
            }

            items(
                items = modules,
                key = { it.id }
            ) { module ->
                ModuleListItem(
                    module = module,
                    onClick = module.onClick
                )
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun ProfileStatusCard(
    isWorkProfile: Boolean,
    onCreateWorkProfileClick: () -> Unit
) {
    val containerColor = if (isWorkProfile) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }

    val contentColor = if (isWorkProfile) {
        MaterialTheme.colorScheme.onPrimaryContainer
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    val statusIconTint = if (isWorkProfile) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.secondary
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = containerColor,
            contentColor = contentColor
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = if (isWorkProfile) R.drawable.work else R.drawable.person),
                    contentDescription = null,
                    tint = statusIconTint,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = if (isWorkProfile) {
                        stringResource(R.string.you_are_in_work_profile)
                    } else {
                        stringResource(R.string.you_are_in_personal_profile)
                    },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = contentColor
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = if (isWorkProfile) {
                    stringResource(R.string.work_profile_status_ok)
                } else {
                    stringResource(R.string.personal_profile_cannot_manage)
                },
                style = MaterialTheme.typography.bodyMedium,
                color = contentColor
            )

            if (!isWorkProfile) {
                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onCreateWorkProfileClick,
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text(stringResource(R.string.create_work_profile))
                }
            }
        }
    }
}

@Composable
fun ModuleListItem(
    module: DeviceOwnerModule,
    onClick: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 75.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = module.iconResId),
                contentDescription = stringResource(module.title),
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = stringResource(module.title),
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = stringResource(module.description),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Icon(
                painter = painterResource(id = R.drawable.arrow_forward),
                contentDescription = "Navigate",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}