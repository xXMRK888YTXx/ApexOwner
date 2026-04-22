package com.xxmrk888ytxx.feature.managementmodule.apprestriction

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
import androidx.compose.foundation.shape.RoundedCornerShape
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
                uiEventForToggle = AppRestrictionModuleUiEvent.ToggleCameraDisabled
            ),
            Restriction(
                id = 1,
                iconRes = R.drawable.mic,
                title = R.string.disable_microphone.uiText(),
                description = R.string.prevent_all_applications_and_system_from_accessing_the_microphone_and_recording_audio.uiText(),
                isEnabled = screenState.isMicrophoneDisabled,
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
            Restriction(
                id = 3,
                iconRes = R.drawable.transform,
                title = R.string.disable_usb_file_transfer.uiText(),
                description = R.string.disables_file_sharing_with_other_devices_via_usb_please_note_that_only_file_transfer_protocols_mtp_ptp_are_restricted_peripherals_and_connected_accessories_will_continue_to_function_normally_this_setting_does_not_provide_protection_against_badusb_attacks.uiText(),
                isEnabled = screenState.isUSBFileTransferDisabled,
                uiEventForToggle = AppRestrictionModuleUiEvent.ToggleUSBFileTransferDisabled
            ),
            Restriction(
                id = 4,
                iconRes = R.drawable.apps,
                title = R.string.disable_app_installation.uiText(),
                description = R.string.completely_prohibits_the_installation_of_applications_from_any_source_including_official_app_stores_and_manual_package_installations_sideloading.uiText(),
                isEnabled = screenState.isInstallAppsDisabled,
                uiEventForToggle = AppRestrictionModuleUiEvent.ToggleInstallAppsDisabled
            ),
            Restriction(
                id = 5,
                iconRes = R.drawable.apk_install,
                title = R.string.block_installations_from_unknown_sources.uiText(),
                description = R.string.blocks_the_installation_of_third_party_packages_and_manual_sideloading_app_installations_via_authorized_stores_are_not_affected.uiText(),
                isEnabled = screenState.isInstallAppsFromUnknownSourcesDisabled,
                uiEventForToggle = AppRestrictionModuleUiEvent.ToggleInstallAppsFromUnknownSourcesDisabled
            ),
            Restriction(
                id = 6,
                iconRes = R.drawable.delete,
                title = R.string.block_app_removal.uiText(),
                description = R.string.disables_the_ability_to_uninstall_applications_all_existing_apps_will_be_locked_against_deletion.uiText(),
                isEnabled = screenState.isUninstallAppsDisabled,
                uiEventForToggle = AppRestrictionModuleUiEvent.ToggleUninstallAppsDisabled
            ),
            Restriction(
                id = 7,
                iconRes = R.drawable.app_control,
                title = R.string.restrict_app_controls.uiText(),
                description = R.string.blocks_access_to_app_settings_including_uninstallation_force_stopping_and_storage_clearing_data_cache.uiText(),
                isEnabled = screenState.isAppControlDisabled,
                uiEventForToggle = AppRestrictionModuleUiEvent.ToggleAppControlDisabled
            ),
            Restriction(
                id = 8,
                iconRes = R.drawable.robot,
                title = R.string.block_ai_screen_capture.uiText(),
                description = R.string.prevents_your_screen_data_from_being_used_for_ai_driven_features_and_analysis.uiText(),
                isEnabled = screenState.isScreenContentCaptureForAIDisabled,
                isAvailable = screenState.isCanDisableScreenContentCaptureForAI,
                uiEventForToggle = AppRestrictionModuleUiEvent.ToggleScreenContentCaptureForAIDisabled
            ),
            Restriction(
                id = 9,
                iconRes = R.drawable.lightbulb,
                title = R.string.disable_contextual_content_suggestions.uiText(),
                description = R.string.prevents_the_system_from_suggesting_actions_or_content_based_on_what_you_select_on_your_screen_this_disables_smart_suggestions_that_analyze_your_current_screen_context_to_offer_relevant_links_apps_or_information.uiText(),
                isEnabled = screenState.isContentSuggestionDisabled,
                isAvailable = screenState.isCanDisableContentSuggestion,
                uiEventForToggle = AppRestrictionModuleUiEvent.ToggleContentSuggestionDisabled
            ),
            Restriction(
                id = 10,
                iconRes = R.drawable.screenshot,
                title = R.string.disable_screenshots_and_screen_recording.uiText(),
                description = R.string.prevents_capturing_the_screen_via_screenshots_or_video_recordings_this_also_blocks_screen_sharing_and_projection_to_external_displays.uiText(),
                isEnabled = screenState.isScreenshotsDisabled,
                uiEventForToggle = AppRestrictionModuleUiEvent.ToggleScreenshotsDisabled
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