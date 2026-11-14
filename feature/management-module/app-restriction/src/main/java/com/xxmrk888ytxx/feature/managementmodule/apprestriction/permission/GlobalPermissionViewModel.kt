package com.xxmrk888ytxx.feature.managementmodule.apprestriction.permission

import androidx.lifecycle.viewModelScope
import com.xxmrk888ytxx.core.base.android.uiText.uiText
import com.xxmrk888ytxx.core.base.android.viewModel.ApexOwnerViewModel
import com.xxmrk888ytxx.feature.managementmodule.apprestriction.R
import com.xxmrk888ytxx.feature.managementmodule.apprestriction.permission.contract.GlobalPermissionManageContract
import com.xxmrk888ytxx.feature.managementmodule.apprestriction.permission.model.GlobalPermissionScreenState
import com.xxmrk888ytxx.feature.managementmodule.apprestriction.permission.model.GlobalPermissionUiEvent
import com.xxmrk888ytxx.feature.managementmodule.apprestriction.permission.model.GlobalPermissionUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GlobalPermissionViewModel @Inject constructor(
    private val contract: GlobalPermissionManageContract
) : ApexOwnerViewModel<GlobalPermissionScreenState, GlobalPermissionUiEvent>(
    GlobalPermissionScreenState(
        isDeviceOwner = contract.isAppDeviceOwner,
        isProfileOwner = contract.isAppProfileOwner
    )
) {

    private val searchQuery = MutableStateFlow("")

    private val supportedPermissions = listOf(
        // Notifications
        PermissionDefinition(
            id = "POST_NOTIFICATIONS",
            technicalName = "POST_NOTIFICATIONS",
            titleRes = R.string.perm_post_notifications_title,
            descRes = R.string.perm_post_notifications_desc,
            groupRes = R.string.perm_group_notifications,
            iconRes = R.drawable.lightbulb
        ),

        // Camera & Mic
        PermissionDefinition(
            id = "CAMERA",
            technicalName = "CAMERA",
            titleRes = R.string.perm_camera_title,
            descRes = R.string.perm_camera_desc,
            groupRes = R.string.perm_group_camera_mic,
            iconRes = R.drawable.camera
        ),
        PermissionDefinition(
            id = "RECORD_AUDIO",
            technicalName = "RECORD_AUDIO",
            titleRes = R.string.perm_record_audio_title,
            descRes = R.string.perm_record_audio_desc,
            groupRes = R.string.perm_group_camera_mic,
            iconRes = R.drawable.mic
        ),

        // Location
        PermissionDefinition(
            id = "ACCESS_FINE_LOCATION",
            technicalName = "ACCESS_FINE_LOCATION",
            titleRes = R.string.perm_access_fine_location_title,
            descRes = R.string.perm_access_fine_location_desc,
            groupRes = R.string.perm_group_location,
            iconRes = R.drawable.location
        ),
        PermissionDefinition(
            id = "ACCESS_COARSE_LOCATION",
            technicalName = "ACCESS_COARSE_LOCATION",
            titleRes = R.string.perm_access_coarse_location_title,
            descRes = R.string.perm_access_coarse_location_desc,
            groupRes = R.string.perm_group_location,
            iconRes = R.drawable.location
        ),
        PermissionDefinition(
            id = "ACCESS_BACKGROUND_LOCATION",
            technicalName = "ACCESS_BACKGROUND_LOCATION",
            titleRes = R.string.perm_access_background_location_title,
            descRes = R.string.perm_access_background_location_desc,
            groupRes = R.string.perm_group_location,
            iconRes = R.drawable.compass
        ),

        // Storage & Media
        PermissionDefinition(
            id = "READ_EXTERNAL_STORAGE",
            technicalName = "READ_EXTERNAL_STORAGE",
            titleRes = R.string.perm_read_external_storage_title,
            descRes = R.string.perm_read_external_storage_desc,
            groupRes = R.string.perm_group_storage,
            iconRes = R.drawable.hard_disk
        ),
        PermissionDefinition(
            id = "WRITE_EXTERNAL_STORAGE",
            technicalName = "WRITE_EXTERNAL_STORAGE",
            titleRes = R.string.perm_write_external_storage_title,
            descRes = R.string.perm_write_external_storage_desc,
            groupRes = R.string.perm_group_storage,
            iconRes = R.drawable.hard_disk
        ),
        PermissionDefinition(
            id = "READ_MEDIA_IMAGES",
            technicalName = "READ_MEDIA_IMAGES",
            titleRes = R.string.perm_read_media_images_title,
            descRes = R.string.perm_read_media_images_desc,
            groupRes = R.string.perm_group_storage,
            iconRes = R.drawable.screenshot
        ),
        PermissionDefinition(
            id = "READ_MEDIA_VIDEO",
            technicalName = "READ_MEDIA_VIDEO",
            titleRes = R.string.perm_read_media_video_title,
            descRes = R.string.perm_read_media_video_desc,
            groupRes = R.string.perm_group_storage,
            iconRes = R.drawable.camera
        ),
        PermissionDefinition(
            id = "READ_MEDIA_AUDIO",
            technicalName = "READ_MEDIA_AUDIO",
            titleRes = R.string.perm_read_media_audio_title,
            descRes = R.string.perm_read_media_audio_desc,
            groupRes = R.string.perm_group_storage,
            iconRes = R.drawable.mic
        ),
        PermissionDefinition(
            id = "READ_MEDIA_VISUAL_USER_SELECTED",
            technicalName = "READ_MEDIA_VISUAL_USER_SELECTED",
            titleRes = R.string.perm_read_media_visual_user_selected_title,
            descRes = R.string.perm_read_media_visual_user_selected_desc,
            groupRes = R.string.perm_group_storage,
            iconRes = R.drawable.screenshot
        ),
        PermissionDefinition(
            id = "ACCESS_MEDIA_LOCATION",
            technicalName = "ACCESS_MEDIA_LOCATION",
            titleRes = R.string.perm_access_media_location_title,
            descRes = R.string.perm_access_media_location_desc,
            groupRes = R.string.perm_group_storage,
            iconRes = R.drawable.location
        ),

        // Contacts & Calendar
        PermissionDefinition(
            id = "READ_CONTACTS",
            technicalName = "READ_CONTACTS",
            titleRes = R.string.perm_read_contacts_title,
            descRes = R.string.perm_read_contacts_desc,
            groupRes = R.string.perm_group_contacts_calendar,
            iconRes = R.drawable.apps
        ),
        PermissionDefinition(
            id = "WRITE_CONTACTS",
            technicalName = "WRITE_CONTACTS",
            titleRes = R.string.perm_write_contacts_title,
            descRes = R.string.perm_write_contacts_desc,
            groupRes = R.string.perm_group_contacts_calendar,
            iconRes = R.drawable.apps
        ),
        PermissionDefinition(
            id = "GET_ACCOUNTS",
            technicalName = "GET_ACCOUNTS",
            titleRes = R.string.perm_get_accounts_title,
            descRes = R.string.perm_get_accounts_desc,
            groupRes = R.string.perm_group_contacts_calendar,
            iconRes = R.drawable.switch_user
        ),
        PermissionDefinition(
            id = "READ_CALENDAR",
            technicalName = "READ_CALENDAR",
            titleRes = R.string.perm_read_calendar_title,
            descRes = R.string.perm_read_calendar_desc,
            groupRes = R.string.perm_group_contacts_calendar,
            iconRes = R.drawable.compass
        ),
        PermissionDefinition(
            id = "WRITE_CALENDAR",
            technicalName = "WRITE_CALENDAR",
            titleRes = R.string.perm_write_calendar_title,
            descRes = R.string.perm_write_calendar_desc,
            groupRes = R.string.perm_group_contacts_calendar,
            iconRes = R.drawable.compass
        ),

        // Telephony & SMS
        PermissionDefinition(
            id = "READ_PHONE_STATE",
            technicalName = "READ_PHONE_STATE",
            titleRes = R.string.perm_read_phone_state_title,
            descRes = R.string.perm_read_phone_state_desc,
            groupRes = R.string.perm_group_telephony,
            iconRes = R.drawable.call
        ),
        PermissionDefinition(
            id = "CALL_PHONE",
            technicalName = "CALL_PHONE",
            titleRes = R.string.perm_call_phone_title,
            descRes = R.string.perm_call_phone_desc,
            groupRes = R.string.perm_group_telephony,
            iconRes = R.drawable.call
        ),
        PermissionDefinition(
            id = "READ_CALL_LOG",
            technicalName = "READ_CALL_LOG",
            titleRes = R.string.perm_read_call_log_title,
            descRes = R.string.perm_read_call_log_desc,
            groupRes = R.string.perm_group_telephony,
            iconRes = R.drawable.call
        ),
        PermissionDefinition(
            id = "WRITE_CALL_LOG",
            technicalName = "WRITE_CALL_LOG",
            titleRes = R.string.perm_write_call_log_title,
            descRes = R.string.perm_write_call_log_desc,
            groupRes = R.string.perm_group_telephony,
            iconRes = R.drawable.call
        ),
        PermissionDefinition(
            id = "READ_PHONE_NUMBERS",
            technicalName = "READ_PHONE_NUMBERS",
            titleRes = R.string.perm_read_phone_numbers_title,
            descRes = R.string.perm_read_phone_numbers_desc,
            groupRes = R.string.perm_group_telephony,
            iconRes = R.drawable.cell
        ),
        PermissionDefinition(
            id = "ANSWER_PHONE_CALLS",
            technicalName = "ANSWER_PHONE_CALLS",
            titleRes = R.string.perm_answer_phone_calls_title,
            descRes = R.string.perm_answer_phone_calls_desc,
            groupRes = R.string.perm_group_telephony,
            iconRes = R.drawable.call
        ),
        PermissionDefinition(
            id = "SEND_SMS",
            technicalName = "SEND_SMS",
            titleRes = R.string.perm_send_sms_title,
            descRes = R.string.perm_send_sms_desc,
            groupRes = R.string.perm_group_telephony,
            iconRes = R.drawable.sms
        ),
        PermissionDefinition(
            id = "READ_SMS",
            technicalName = "READ_SMS",
            titleRes = R.string.perm_read_sms_title,
            descRes = R.string.perm_read_sms_desc,
            groupRes = R.string.perm_group_telephony,
            iconRes = R.drawable.sms
        ),
        PermissionDefinition(
            id = "RECEIVE_SMS",
            technicalName = "RECEIVE_SMS",
            titleRes = R.string.perm_receive_sms_title,
            descRes = R.string.perm_receive_sms_desc,
            groupRes = R.string.perm_group_telephony,
            iconRes = R.drawable.sms
        ),
        PermissionDefinition(
            id = "RECEIVE_MMS",
            technicalName = "RECEIVE_MMS",
            titleRes = R.string.perm_receive_mms_title,
            descRes = R.string.perm_receive_mms_desc,
            groupRes = R.string.perm_group_telephony,
            iconRes = R.drawable.sms
        ),

        // Nearby & Bluetooth
        PermissionDefinition(
            id = "BLUETOOTH_CONNECT",
            technicalName = "BLUETOOTH_CONNECT",
            titleRes = R.string.perm_bluetooth_connect_title,
            descRes = R.string.perm_bluetooth_connect_desc,
            groupRes = R.string.perm_group_nearby,
            iconRes = R.drawable.bluetooth
        ),
        PermissionDefinition(
            id = "BLUETOOTH_SCAN",
            technicalName = "BLUETOOTH_SCAN",
            titleRes = R.string.perm_bluetooth_scan_title,
            descRes = R.string.perm_bluetooth_scan_desc,
            groupRes = R.string.perm_group_nearby,
            iconRes = R.drawable.settings_bluetooth
        ),
        PermissionDefinition(
            id = "BLUETOOTH_ADVERTISE",
            technicalName = "BLUETOOTH_ADVERTISE",
            titleRes = R.string.perm_bluetooth_advertise_title,
            descRes = R.string.perm_bluetooth_advertise_desc,
            groupRes = R.string.perm_group_nearby,
            iconRes = R.drawable.bluetooth
        ),
        PermissionDefinition(
            id = "NEARBY_WIFI_DEVICES",
            technicalName = "NEARBY_WIFI_DEVICES",
            titleRes = R.string.perm_nearby_wifi_devices_title,
            descRes = R.string.perm_nearby_wifi_devices_desc,
            groupRes = R.string.perm_group_nearby,
            iconRes = R.drawable.wifi_device
        ),
        PermissionDefinition(
            id = "UWB_RANGING",
            technicalName = "UWB_RANGING",
            titleRes = R.string.perm_uwb_ranging_title,
            descRes = R.string.perm_uwb_ranging_desc,
            groupRes = R.string.perm_group_nearby,
            iconRes = R.drawable.wifi
        ),

        // Sensors & Activity
        PermissionDefinition(
            id = "BODY_SENSORS",
            technicalName = "BODY_SENSORS",
            titleRes = R.string.perm_body_sensors_title,
            descRes = R.string.perm_body_sensors_desc,
            groupRes = R.string.perm_group_sensors,
            iconRes = R.drawable.robot
        ),
        PermissionDefinition(
            id = "BODY_SENSORS_BACKGROUND",
            technicalName = "BODY_SENSORS_BACKGROUND",
            titleRes = R.string.perm_body_sensors_background_title,
            descRes = R.string.perm_body_sensors_background_desc,
            groupRes = R.string.perm_group_sensors,
            iconRes = R.drawable.robot
        ),
        PermissionDefinition(
            id = "ACTIVITY_RECOGNITION",
            technicalName = "ACTIVITY_RECOGNITION",
            titleRes = R.string.perm_activity_recognition_title,
            descRes = R.string.perm_activity_recognition_desc,
            groupRes = R.string.perm_group_sensors,
            iconRes = R.drawable.shield_locked
        )
    )

    override val state: StateFlow<GlobalPermissionScreenState> = combine(
        contract.disabledPermissions,
        searchQuery
    ) { disabledSet, query ->
        val filtered = if (query.isBlank()) {
            supportedPermissions
        } else {
            supportedPermissions.filter {
                it.technicalName.contains(query, ignoreCase = true) ||
                it.id.contains(query, ignoreCase = true)
            }
        }
        GlobalPermissionScreenState(
            isDeviceOwner = contract.isAppDeviceOwner,
            isProfileOwner = contract.isAppProfileOwner,
            searchQuery = query,
            permissions = filtered.map { def ->
                GlobalPermissionUiModel(
                    id = def.id,
                    technicalName = def.technicalName,
                    title = def.titleRes.uiText(),
                    description = def.descRes.uiText(),
                    groupName = def.groupRes.uiText(),
                    iconRes = def.iconRes,
                    isDisabled = def.id in disabledSet
                )
            }
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        GlobalPermissionScreenState(
            isDeviceOwner = contract.isAppDeviceOwner,
            isProfileOwner = contract.isAppProfileOwner
        )
    )

    override fun onEvent(event: GlobalPermissionUiEvent) {
        when (event) {
            is GlobalPermissionUiEvent.TogglePermission -> {
                viewModelScope.launch {
                    try {
                        contract.setPermissionGloballyDisabled(event.permissionId, event.isDisable)
                    } catch (e: Exception) {
                        sendToastSideEffect(R.string.an_error_has_occurred.uiText())
                    }
                }
            }
            is GlobalPermissionUiEvent.ChangeSearchQuery -> {
                searchQuery.value = event.query
            }
        }
    }

    private data class PermissionDefinition(
        val id: String,
        val technicalName: String,
        val titleRes: Int,
        val descRes: Int,
        val groupRes: Int,
        val iconRes: Int
    )
}
