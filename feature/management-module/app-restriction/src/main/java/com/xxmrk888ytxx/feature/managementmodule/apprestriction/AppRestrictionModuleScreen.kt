package com.xxmrk888ytxx.feature.managementmodule.apprestriction

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.xxmrk888ytxx.core.base.android.mvi.SideEffect
import com.xxmrk888ytxx.core.base.android.mvi.UiEvent
import com.xxmrk888ytxx.core.base.android.uiText.uiText
import com.xxmrk888ytxx.core.base.compose.asString
import com.xxmrk888ytxx.core.base.compose.extension.HandleSideEffects
import com.xxmrk888ytxx.core.base.compose.ui.CoreTopBar
import com.xxmrk888ytxx.feature.managementmodule.apprestriction.model.AppRestrictionModuleUiEvent
import com.xxmrk888ytxx.feature.managementmodule.apprestriction.model.Restriction
import com.xxmrk888ytxx.feature.managementmodule.apprestriction.model.ScreenState
import kotlinx.coroutines.flow.Flow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppRestrictionModuleScreen(
    screenState: ScreenState,
    onEvent: (AppRestrictionModuleUiEvent) -> Unit,
    sideEffect: Flow<SideEffect>
) {
    val restrictions = remember(screenState) {
        listOf(
            Restriction(
                id = 0,
                iconRes = R.drawable.camera,
                title = R.string.disable_camera.uiText(),
                description = R.string.prevent_all_applications_from_accessing_the_camera_hardware.uiText(),
                isEnabled = screenState.isCameraDisabled,
                isAvailable = true,
                uiEventForToggle = AppRestrictionModuleUiEvent.ToggleCameraDisabled
            ),
            Restriction(
                id = 1,
                iconRes = R.drawable.mic,
                title = R.string.disable_microphone.uiText(),
                description = R.string.prevent_all_applications_and_system_from_accessing_the_microphone_and_recording_audio.uiText(),
                isEnabled = screenState.isMicrophoneDisabled,
                isAvailable = true,
                uiEventForToggle = AppRestrictionModuleUiEvent.ToggleMicrophoneDisabled
            ),
            Restriction(
                id = 2,
                iconRes = R.drawable.usb,
                title = R.string.restriction_usb_data_title.uiText(),
                description = R.string.restriction_usb_data_description.uiText(),
                isEnabled = screenState.isUSBDataSignalDisabled,
                isAvailable = screenState.isCanDisableUSBDataSignal,
                unavailableMessage = R.string.restriction_usb_data_unavailable.uiText(),
                uiEventForToggle = AppRestrictionModuleUiEvent.ToggleUSBDataSignalDisable
            ),
        )
    }

    HandleSideEffects(sideEffect)
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets(),
        topBar = {
            CoreTopBar(
                title = {
                    Text(text = stringResource(R.string.app_restriction))
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(
                items = restrictions,
                key = { restriction -> restriction.id }
            ) { restriction ->
                RestrictionCard(
                    restriction = restriction,
                    onEvent = onEvent
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RestrictionCard(
    modifier: Modifier = Modifier,
    restriction: Restriction,
    onEvent: (AppRestrictionModuleUiEvent) -> Unit,
) {
    val contentAlpha = if (restriction.isAvailable) 1f else 0.5f

    Card(
        onClick = { onEvent(restriction.uiEventForToggle) },
        enabled = restriction.isAvailable,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        )
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .alpha(contentAlpha)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = restriction.iconRes),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.width(16.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = restriction.title.asString(),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = restriction.description.asString(),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Switch(
                    checked = restriction.isEnabled,
                    onCheckedChange = null,
                    enabled = restriction.isAvailable
                )
            }

            if (!restriction.isAvailable && restriction.unavailableMessage != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = restriction.unavailableMessage.asString(),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}