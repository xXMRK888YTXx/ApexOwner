package com.xxmrk888ytxx.feature.managementmodule.apprestriction

import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
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
    val groupedRestrictions = remember(screenState) {
        mapOf(
            R.string.category_hardware_and_media.uiText() to listOf(
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
//                Restriction(
//                    id = 2,
//                    iconRes = R.drawable.usb,
//                    title = R.string.restriction_usb_data_title.uiText(),
//                    description = R.string.restriction_usb_data_description.uiText(),
//                    isEnabled = screenState.isUSBDataSignalDisabled,
//                    isAvailable = screenState.isCanDisableUSBDataSignal,
//                    unavailableMessage = R.string.restriction_usb_data_unavailable.uiText(),
//                    uiEventForToggle = AppRestrictionModuleUiEvent.ToggleUSBDataSignalDisable
//                ),
//                Restriction(
//                    id = 3,
//                    iconRes = R.drawable.transform,
//                    title = R.string.disable_usb_file_transfer.uiText(),
//                    description = R.string.disables_file_sharing_with_other_devices_via_usb_please_note_that_only_file_transfer_protocols_mtp_ptp_are_restricted_peripherals_and_connected_accessories_will_continue_to_function_normally_this_setting_does_not_provide_protection_against_badusb_attacks.uiText(),
//                    isEnabled = screenState.isUSBFileTransferDisabled,
//                    uiEventForToggle = AppRestrictionModuleUiEvent.ToggleUSBFileTransferDisabled
//                ),
//                Restriction(
//                    id = 19,
//                    iconRes = R.drawable.hard_disk,
//                    title = R.string.disable_external_media_mounting.uiText(),
//                    description = R.string.connecting_and_accessing_physical_external_storage_such_as_usb_drives_or_sd_cards_is_prohibited_the_device_will_not_mount_or_recognize_any_external_media.uiText(),
//                    isEnabled = screenState.isMountPhysicalMediaDisabled,
//                    uiEventForToggle = AppRestrictionModuleUiEvent.ToggleMountPhysicalMediaDisabled
//                ),
//                Restriction(
//                    id = 21,
//                    iconRes = R.drawable.nfc,
//                    title = R.string.disable_nfc.uiText(),
//                    description = R.string.nfc_is_completely_disabled.uiText(),
//                    isEnabled = screenState.isNFCDisabled,
//                    isAvailable = screenState.isCanDisableNFC,
//                    uiEventForToggle = AppRestrictionModuleUiEvent.ToggleNFCDisabled
//                )
            ),
            R.string.category_applications.uiText() to listOf(
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
//                Restriction(
//                    id = 25,
//                    iconRes = R.drawable.`fun`,
//                    title = R.string.disable_fun_features.uiText(),
//                    description = R.string.access_to_entertainment_applications_and_amusement_features_is_prohibited_this_restriction_prevents_the_use_of_the_device_for_non_productive_purposes_or_leisure_activities_no_more_fun_allowed.uiText(),
//                    isEnabled = screenState.isFunDisabled,
//                    uiEventForToggle = AppRestrictionModuleUiEvent.ToggleFunDisabled
//                )
            ),
            R.string.category_security_and_system.uiText() to listOf(
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
                Restriction(
                    id = 11,
                    iconRes = R.drawable.adb,
                    title = R.string.disable_debugging_features.uiText(),
                    description = R.string.enabling_or_accessing_any_debugging_features_including_usb_debugging_is_prohibited_this_restriction_prevents_all_advanced_developer_actions_such_as_app_installations_via_adb_access_to_system_logs_and_direct_service_calls.uiText(),
                    isEnabled = screenState.isDebugFeaturesDisabled,
                    uiEventForToggle = AppRestrictionModuleUiEvent.ToggleDebugFeaturesDisabled
                ),
                Restriction(
                    id = 20,
                    iconRes = R.drawable.location,
                    title = R.string.disable_location_sharing.uiText(),
                    description = R.string.turning_on_location_sharing_features_is_prohibited.uiText(),
                    isEnabled = screenState.isLocationDisabled,
                    uiEventForToggle = AppRestrictionModuleUiEvent.ToggleLocationDisabled
                ),
//                Restriction(
//                    id = 12,
//                    iconRes = R.drawable.lock_reset,
//                    title = R.string.block_factory_reset.uiText(),
//                    description = R.string.the_ability_to_perform_a_factory_reset_is_disabled.uiText(),
//                    isEnabled = screenState.isFactoryResetDisabled,
//                    uiEventForToggle = AppRestrictionModuleUiEvent.ToggleFactoryResetDisabled
//                ),
//                Restriction(
//                    id = 13,
//                    iconRes = R.drawable.shield_locked,
//                    title = R.string.disable_safe_mode.uiText(),
//                    description = R.string.access_to_safe_mode_is_blocked_this_prevents_bypassing_security_restrictions_or_disabling_protection_by_starting_the_device_in_a_limited_state.uiText(),
//                    isEnabled = screenState.isSafeBootDisabled,
//                    uiEventForToggle = AppRestrictionModuleUiEvent.ToggleSafeBootDisabled
//                ),
//                Restriction(
//                    id = 24,
//                    iconRes = R.drawable.wallpaper,
//                    title = R.string.disable_wallpaper_modification.uiText(),
//                    description = R.string.changing_the_home_screen_or_lock_screen_wallpaper_is_prohibited.uiText(),
//                    isEnabled = screenState.isWallpaperChangeDisabled,
//                    uiEventForToggle = AppRestrictionModuleUiEvent.ToggleWallpaperChangeDisabled
//                )
            ),
//            R.string.category_users_and_accounts.uiText() to listOf(
//                Restriction(
//                    id = 14,
//                    iconRes = R.drawable.add,
//                    title = R.string.disable_adding_new_users.uiText(),
//                    description = R.string.creation_of_new_user_profiles_on_the_device_is_prohibited.uiText(),
//                    isEnabled = screenState.isAddUserDisabled,
//                    uiEventForToggle = AppRestrictionModuleUiEvent.ToggleAddUserDisabled
//                ),
//                Restriction(
//                    id = 15,
//                    iconRes = R.drawable.remove,
//                    title = R.string.disable_user_removal.uiText(),
//                    description = R.string.removal_of_secondary_user_profiles_from_the_device_is_prohibited.uiText(),
//                    isEnabled = screenState.isRemoveUserDisabled,
//                    uiEventForToggle = AppRestrictionModuleUiEvent.ToggleRemoveUserDisabled
//                ),
//                Restriction(
//                    id = 16,
//                    iconRes = R.drawable.switch_user,
//                    title = R.string.block_user_switching.uiText(),
//                    description = R.string.the_ability_to_switch_between_different_user_accounts_on_the_device_is_disabled.uiText(),
//                    isEnabled = screenState.isSwitchUserDisabled,
//                    uiEventForToggle = AppRestrictionModuleUiEvent.ToggleSwitchUserDisabled
//                )
//            ),
//            R.string.category_network_and_connectivity.uiText() to listOf(
//                Restriction(
//                    id = 17,
//                    iconRes = R.drawable.bluetooth,
//                    title = R.string.disable_bluetooth.uiText(),
//                    description = R.string.bluetooth_is_completely_disabled_turning_on_using_or_configuring_bluetooth_connections_via_settings_is_prohibited.uiText(),
//                    isEnabled = screenState.isBluetoothDisabled,
//                    uiEventForToggle = AppRestrictionModuleUiEvent.ToggleBluetoothDisabled
//                ),
//                Restriction(
//                    id = 18,
//                    iconRes = R.drawable.settings_bluetooth,
//                    title = R.string.restrict_bluetooth_configuration.uiText(),
//                    description = R.string.bluetooth_settings_and_device_pairing_via_the_settings_menu_are_disabled_this_restriction_does_not_prevent_turning_bluetooth_on_or_off_and_does_not_block_the_use_of_already_paired_devices.uiText(),
//                    isEnabled = screenState.isBluetoothConfigDisabled,
//                    uiEventForToggle = AppRestrictionModuleUiEvent.ToggleBluetoothConfigDisabled
//                ),
//                Restriction(
//                    id = 26,
//                    iconRes = R.drawable.wifi_device,
//                    title = R.string.restrict_wi_fi_networks_configuration.uiText(),
//                    description = R.string.changing_wi_fi_networks_settings_is_prohibited_this_restriction_does_not_affect_wi_fi_tethering_settings.uiText(),
//                    isEnabled = screenState.isWifiConfigDisabled,
//                    uiEventForToggle = AppRestrictionModuleUiEvent.ToggleWifiConfigDisabled
//                ),
//                Restriction(
//                    id = 27,
//                    iconRes = R.drawable.wifi,
//                    title = R.string.disable_wi_fi_control.uiText(),
//                    description = R.string.turning_wi_fi_on_or_off_is_prohibited_this_restriction_applies_globally_and_prevents_changing_the_wi_fi_state_even_through_airplane_mode.uiText(),
//                    isEnabled = screenState.isWifiStateChangeDisabled,
//                    isAvailable = screenState.isCanDisableChangeWifiState,
//                    uiEventForToggle = AppRestrictionModuleUiEvent.ToggleWifiStateChangeDisabled
//                ),
//                Restriction(
//                    id = 28,
//                    iconRes = R.drawable.compass,
//                    title = R.string.disable_wi_fi_tethering.uiText(),
//                    description = R.string.using_the_device_as_a_wi_fi_hotspot_is_prohibited_this_restriction_does_not_affect_other_types_of_tethering_or_the_ability_to_connect_to_regular_wi_fi_networks.uiText(),
//                    isEnabled = screenState.isHotspotDisabled,
//                    isAvailable = screenState.isCanDisableHotspot,
//                    uiEventForToggle = AppRestrictionModuleUiEvent.ToggleHotspotDisabled
//                ),
//                Restriction(
//                    id = 29,
//                    iconRes = R.drawable.wifi_plus,
//                    title = R.string.disable_adding_wi_fi_networks.uiText(),
//                    description = R.string.adding_new_wi_fi_configurations_is_prohibited_this_restriction_prevents_the_setup_of_new_wireless_connections_while_allowing_the_use_of_existing_ones.uiText(),
//                    isEnabled = screenState.isAddNewWifiNetworksDisabled,
//                    isAvailable = screenState.isCanDisableAddNewWifiNetworks,
//                    uiEventForToggle = AppRestrictionModuleUiEvent.ToggleAddNewWifiNetworksDisabled
//                ),
//                Restriction(
//                    id = 30,
//                    iconRes = R.drawable.flight,
//                    title = R.string.disable_airplane_mode.uiText(),
//                    description = R.string.turning_on_airplane_mode_is_prohibited_this_restriction_applies_globally_ensuring_that_all_wireless_radios_remain_active_and_the_device_cannot_be_disconnected_from_networks_via_this_mode.uiText(),
//                    isEnabled = screenState.isAirplaneModeDisabled,
//                    uiEventForToggle = AppRestrictionModuleUiEvent.ToggleAirplaneModeDisabled
//                ),
//                Restriction(
//                    id = 31,
//                    iconRes = R.drawable.vpn,
//                    title = R.string.disable_vpn_configuration.uiText(),
//                    description = R.string.configuration_of_vpn_connections_is_prohibited_this_restriction_prevents_starting_manual_vpns_and_automatically_disconnects_any_existing_user_configured_vpn_services.uiText(),
//                    isEnabled = screenState.isConfigVPNDisabled,
//                    uiEventForToggle = AppRestrictionModuleUiEvent.ToggleConfigVPNDisabled
//                ),
//                Restriction(
//                    id = 32,
//                    iconRes = R.drawable.dns,
//                    title = R.string.disable_private_dns_configuration.uiText(),
//                    description = R.string.modification_of_private_dns_settings_is_prohibited_this_restriction_applies_globally_preventing_any_changes_to_dns_over_tls_configurations_in_the_network_settings.uiText(),
//                    isEnabled = screenState.isConfigPrivateDNSDisabled,
//                    isAvailable = screenState.isCanDisableConfigPrivateDNS,
//                    uiEventForToggle = AppRestrictionModuleUiEvent.ToggleConfigPrivateDNSDisabled
//                ),
//                Restriction(
//                    id = 33,
//                    iconRes = R.drawable.cell,
//                    title = R.string.disable_data_roaming.uiText(),
//                    description = R.string.use_of_cellular_data_while_roaming_is_prohibited_this_restriction_prevents_the_device_from_connecting_to_the_internet_via_mobile_networks_when_outside_the_home_carrier_s_coverage_area.uiText(),
//                    isEnabled = screenState.isRoamingDisabled,
//                    uiEventForToggle = AppRestrictionModuleUiEvent.ToggleRoamingDisabled
//                ),
//                Restriction(
//                    id = 34,
//                    iconRes = R.drawable.mobiledata_arrows,
//                    title = R.string.restrict_mobile_network_configuration.uiText(),
//                    description = R.string.modification_of_mobile_network_settings_is_prohibited_this_restriction_prevents_the_user_from_changing_apn_settings_selecting_network_operators_or_adjusting_other_cellular_data_configurations.uiText(),
//                    isEnabled = screenState.isConfigMobileDataDisabled,
//                    uiEventForToggle = AppRestrictionModuleUiEvent.ToggleMobileDataDisabled
//                ),
//                Restriction(
//                    id = 35,
//                    iconRes = R.drawable.signal_cellular,
//                    title = R.string.disable_2g_networks.uiText(),
//                    description = R.string.the_use_of_2g_cellular_networks_is_prohibited_to_prevent_security_attacks_like_communication_interception_or_modification_the_device_will_only_connect_to_3g_or_newer_networks_except_for_emergency_calls_which_remain_unaffected.uiText(),
//                    isEnabled = screenState.is2GNetworkDisabled,
//                    isAvailable = screenState.isCanDisable2GNetwork,
//                    uiEventForToggle = AppRestrictionModuleUiEvent.Toggle2GNetworkDisabled
//                ),
//            ),
//            R.string.category_telephony.uiText() to listOf(
//                Restriction(
//                    id = 22,
//                    iconRes = R.drawable.call,
//                    title = R.string.disable_outgoing_calls.uiText(),
//                    description = R.string.making_outgoing_phone_calls_is_prohibited_this_restriction_does_not_affect_the_ability_to_make_emergency_calls.uiText(),
//                    isEnabled = screenState.isOutgoingCallsDisabled,
//                    uiEventForToggle = AppRestrictionModuleUiEvent.ToggleOutgoingCallsDisabled
//                ),
//                Restriction(
//                    id = 23,
//                    iconRes = R.drawable.sms,
//                    title = R.string.disable_sms_messaging.uiText(),
//                    description = R.string.sending_and_receiving_sms_messages_is_prohibited_all_text_messaging_functionality_via_the_cellular_network_is_completely_disabled.uiText(),
//                    isEnabled = screenState.isSMSDisabled,
//                    uiEventForToggle = AppRestrictionModuleUiEvent.ToggleSMSDisabled
//                )
//            )
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
            groupedRestrictions.forEach { (categoryTitle, restrictions) ->
                item {
                    CategoryHeader(
                        title = categoryTitle.asString(),
                        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                    )
                }

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
}

@Composable
fun CategoryHeader(
    title: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.primary,
        fontWeight = FontWeight.Bold,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RestrictionCard(
    modifier: Modifier = Modifier,
    restriction: Restriction,
    onEvent: (AppRestrictionModuleUiEvent) -> Unit,
) {
    val contentAlpha = if (restriction.isAvailable) 1f else 0.5f
    var isExpanded by remember { mutableStateOf(false) }
    var showReadMoreButton by remember { mutableStateOf(false) }

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
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = if (isExpanded) Int.MAX_VALUE else 3,
                        overflow = TextOverflow.Ellipsis,
                        onTextLayout = { textLayoutResult ->
                            if (!isExpanded) {
                                showReadMoreButton = textLayoutResult.hasVisualOverflow
                            }
                        }
                    )

                    if (showReadMoreButton) {
                        // Заменили TextButton на Text с clickable
                        Text(
                            text = if (isExpanded) stringResource(R.string.show_less) else stringResource(
                                R.string.show_more
                            ),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .padding(top = 4.dp)
                                .clickable { isExpanded = !isExpanded }
                        )
                    }
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