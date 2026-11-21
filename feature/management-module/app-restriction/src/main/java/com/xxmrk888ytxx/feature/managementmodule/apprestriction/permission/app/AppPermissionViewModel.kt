package com.xxmrk888ytxx.feature.managementmodule.apprestriction.permission.app

import android.Manifest
import android.os.Build
import androidx.lifecycle.viewModelScope
import com.xxmrk888ytxx.core.base.android.uiText.uiText
import com.xxmrk888ytxx.core.base.android.viewModel.ApexOwnerViewModel
import com.xxmrk888ytxx.feature.managementmodule.apprestriction.R
import com.xxmrk888ytxx.feature.managementmodule.apprestriction.permission.app.contract.AppPermissionManageContract
import com.xxmrk888ytxx.feature.managementmodule.apprestriction.permission.app.model.AppPermissionPolicy
import com.xxmrk888ytxx.feature.managementmodule.apprestriction.permission.app.model.AppPermissionScreenState
import com.xxmrk888ytxx.feature.managementmodule.apprestriction.permission.app.model.AppPermissionUiEvent
import com.xxmrk888ytxx.feature.managementmodule.apprestriction.permission.app.model.AppPermissionUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class AppPermissionViewModel @Inject constructor(
    private val contract: AppPermissionManageContract
) : ApexOwnerViewModel<AppPermissionScreenState, AppPermissionUiEvent>(
    AppPermissionScreenState(
        isDeviceOwner = contract.isAppDeviceOwner,
        isProfileOwner = contract.isAppProfileOwner
    )
) {

    private val packageNameFlow = MutableStateFlow("")
    private val appNameFlow = MutableStateFlow("")
    private val searchQuery = MutableStateFlow("")
    private val onlyRequested = MutableStateFlow(false)
    private val requestedPermissionsFlow = MutableStateFlow<Set<String>>(emptySet())

    private val supportedPermissions = listOf(
        // Notifications
        PermissionDefinition(
            id = "POST_NOTIFICATIONS",
            technicalName = "POST_NOTIFICATIONS",
            titleRes = R.string.perm_post_notifications_title,
            descRes = R.string.perm_post_notifications_desc,
            groupRes = R.string.perm_group_notifications,
            iconRes = R.drawable.lightbulb,
            manifestPermissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) listOf(Manifest.permission.POST_NOTIFICATIONS) else emptyList()
        ),

        // Camera & Mic
        PermissionDefinition(
            id = "CAMERA",
            technicalName = "CAMERA",
            titleRes = R.string.perm_camera_title,
            descRes = R.string.perm_camera_desc,
            groupRes = R.string.perm_group_camera_mic,
            iconRes = R.drawable.camera,
            manifestPermissions = listOf(Manifest.permission.CAMERA)
        ),
        PermissionDefinition(
            id = "RECORD_AUDIO",
            technicalName = "RECORD_AUDIO",
            titleRes = R.string.perm_record_audio_title,
            descRes = R.string.perm_record_audio_desc,
            groupRes = R.string.perm_group_camera_mic,
            iconRes = R.drawable.mic,
            manifestPermissions = listOf(Manifest.permission.RECORD_AUDIO)
        ),

        // Location
        PermissionDefinition(
            id = "ACCESS_FINE_LOCATION",
            technicalName = "ACCESS_FINE_LOCATION",
            titleRes = R.string.perm_access_fine_location_title,
            descRes = R.string.perm_access_fine_location_desc,
            groupRes = R.string.perm_group_location,
            iconRes = R.drawable.location,
            manifestPermissions = listOf(Manifest.permission.ACCESS_FINE_LOCATION)
        ),
        PermissionDefinition(
            id = "ACCESS_COARSE_LOCATION",
            technicalName = "ACCESS_COARSE_LOCATION",
            titleRes = R.string.perm_access_coarse_location_title,
            descRes = R.string.perm_access_coarse_location_desc,
            groupRes = R.string.perm_group_location,
            iconRes = R.drawable.location,
            manifestPermissions = listOf(Manifest.permission.ACCESS_COARSE_LOCATION)
        ),
        PermissionDefinition(
            id = "ACCESS_BACKGROUND_LOCATION",
            technicalName = "ACCESS_BACKGROUND_LOCATION",
            titleRes = R.string.perm_access_background_location_title,
            descRes = R.string.perm_access_background_location_desc,
            groupRes = R.string.perm_group_location,
            iconRes = R.drawable.compass,
            manifestPermissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) listOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION) else emptyList()
        ),

        // Storage & Media
        PermissionDefinition(
            id = "READ_EXTERNAL_STORAGE",
            technicalName = "READ_EXTERNAL_STORAGE",
            titleRes = R.string.perm_read_external_storage_title,
            descRes = R.string.perm_read_external_storage_desc,
            groupRes = R.string.perm_group_storage,
            iconRes = R.drawable.hard_disk,
            manifestPermissions = listOf(Manifest.permission.READ_EXTERNAL_STORAGE)
        ),
        PermissionDefinition(
            id = "WRITE_EXTERNAL_STORAGE",
            technicalName = "WRITE_EXTERNAL_STORAGE",
            titleRes = R.string.perm_write_external_storage_title,
            descRes = R.string.perm_write_external_storage_desc,
            groupRes = R.string.perm_group_storage,
            iconRes = R.drawable.hard_disk,
            manifestPermissions = listOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        ),
        PermissionDefinition(
            id = "READ_MEDIA_IMAGES",
            technicalName = "READ_MEDIA_IMAGES",
            titleRes = R.string.perm_read_media_images_title,
            descRes = R.string.perm_read_media_images_desc,
            groupRes = R.string.perm_group_storage,
            iconRes = R.drawable.screenshot,
            manifestPermissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) listOf(Manifest.permission.READ_MEDIA_IMAGES) else emptyList()
        ),
        PermissionDefinition(
            id = "READ_MEDIA_VIDEO",
            technicalName = "READ_MEDIA_VIDEO",
            titleRes = R.string.perm_read_media_video_title,
            descRes = R.string.perm_read_media_video_desc,
            groupRes = R.string.perm_group_storage,
            iconRes = R.drawable.camera,
            manifestPermissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) listOf(Manifest.permission.READ_MEDIA_VIDEO) else emptyList()
        ),
        PermissionDefinition(
            id = "READ_MEDIA_AUDIO",
            technicalName = "READ_MEDIA_AUDIO",
            titleRes = R.string.perm_read_media_audio_title,
            descRes = R.string.perm_read_media_audio_desc,
            groupRes = R.string.perm_group_storage,
            iconRes = R.drawable.mic,
            manifestPermissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) listOf(Manifest.permission.READ_MEDIA_AUDIO) else emptyList()
        ),
        PermissionDefinition(
            id = "READ_MEDIA_VISUAL_USER_SELECTED",
            technicalName = "READ_MEDIA_VISUAL_USER_SELECTED",
            titleRes = R.string.perm_read_media_visual_user_selected_title,
            descRes = R.string.perm_read_media_visual_user_selected_desc,
            groupRes = R.string.perm_group_storage,
            iconRes = R.drawable.screenshot,
            manifestPermissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) listOf(Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED) else emptyList()
        ),
        PermissionDefinition(
            id = "ACCESS_MEDIA_LOCATION",
            technicalName = "ACCESS_MEDIA_LOCATION",
            titleRes = R.string.perm_access_media_location_title,
            descRes = R.string.perm_access_media_location_desc,
            groupRes = R.string.perm_group_storage,
            iconRes = R.drawable.location,
            manifestPermissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) listOf(Manifest.permission.ACCESS_MEDIA_LOCATION) else emptyList()
        ),

        // Contacts & Calendar
        PermissionDefinition(
            id = "READ_CONTACTS",
            technicalName = "READ_CONTACTS",
            titleRes = R.string.perm_read_contacts_title,
            descRes = R.string.perm_read_contacts_desc,
            groupRes = R.string.perm_group_contacts_calendar,
            iconRes = R.drawable.apps,
            manifestPermissions = listOf(Manifest.permission.READ_CONTACTS)
        ),
        PermissionDefinition(
            id = "WRITE_CONTACTS",
            technicalName = "WRITE_CONTACTS",
            titleRes = R.string.perm_write_contacts_title,
            descRes = R.string.perm_write_contacts_desc,
            groupRes = R.string.perm_group_contacts_calendar,
            iconRes = R.drawable.apps,
            manifestPermissions = listOf(Manifest.permission.WRITE_CONTACTS)
        ),
        PermissionDefinition(
            id = "GET_ACCOUNTS",
            technicalName = "GET_ACCOUNTS",
            titleRes = R.string.perm_get_accounts_title,
            descRes = R.string.perm_get_accounts_desc,
            groupRes = R.string.perm_group_contacts_calendar,
            iconRes = R.drawable.switch_user,
            manifestPermissions = listOf(Manifest.permission.GET_ACCOUNTS)
        ),
        PermissionDefinition(
            id = "READ_CALENDAR",
            technicalName = "READ_CALENDAR",
            titleRes = R.string.perm_read_calendar_title,
            descRes = R.string.perm_read_calendar_desc,
            groupRes = R.string.perm_group_contacts_calendar,
            iconRes = R.drawable.compass,
            manifestPermissions = listOf(Manifest.permission.READ_CALENDAR)
        ),
        PermissionDefinition(
            id = "WRITE_CALENDAR",
            technicalName = "WRITE_CALENDAR",
            titleRes = R.string.perm_write_calendar_title,
            descRes = R.string.perm_write_calendar_desc,
            groupRes = R.string.perm_group_contacts_calendar,
            iconRes = R.drawable.compass,
            manifestPermissions = listOf(Manifest.permission.WRITE_CALENDAR)
        ),

        // Telephony & SMS
        PermissionDefinition(
            id = "READ_PHONE_STATE",
            technicalName = "READ_PHONE_STATE",
            titleRes = R.string.perm_read_phone_state_title,
            descRes = R.string.perm_read_phone_state_desc,
            groupRes = R.string.perm_group_telephony,
            iconRes = R.drawable.call,
            manifestPermissions = listOf(Manifest.permission.READ_PHONE_STATE)
        ),
        PermissionDefinition(
            id = "CALL_PHONE",
            technicalName = "CALL_PHONE",
            titleRes = R.string.perm_call_phone_title,
            descRes = R.string.perm_call_phone_desc,
            groupRes = R.string.perm_group_telephony,
            iconRes = R.drawable.call,
            manifestPermissions = listOf(Manifest.permission.CALL_PHONE)
        ),
        PermissionDefinition(
            id = "READ_CALL_LOG",
            technicalName = "READ_CALL_LOG",
            titleRes = R.string.perm_read_call_log_title,
            descRes = R.string.perm_read_call_log_desc,
            groupRes = R.string.perm_group_telephony,
            iconRes = R.drawable.call,
            manifestPermissions = listOf(Manifest.permission.READ_CALL_LOG)
        ),
        PermissionDefinition(
            id = "WRITE_CALL_LOG",
            technicalName = "WRITE_CALL_LOG",
            titleRes = R.string.perm_write_call_log_title,
            descRes = R.string.perm_write_call_log_desc,
            groupRes = R.string.perm_group_telephony,
            iconRes = R.drawable.call,
            manifestPermissions = listOf(Manifest.permission.WRITE_CALL_LOG)
        ),
        PermissionDefinition(
            id = "READ_PHONE_NUMBERS",
            technicalName = "READ_PHONE_NUMBERS",
            titleRes = R.string.perm_read_phone_numbers_title,
            descRes = R.string.perm_read_phone_numbers_desc,
            groupRes = R.string.perm_group_telephony,
            iconRes = R.drawable.call,
            manifestPermissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) listOf(Manifest.permission.READ_PHONE_NUMBERS) else emptyList()
        ),
        PermissionDefinition(
            id = "ANSWER_PHONE_CALLS",
            technicalName = "ANSWER_PHONE_CALLS",
            titleRes = R.string.perm_answer_phone_calls_title,
            descRes = R.string.perm_answer_phone_calls_desc,
            groupRes = R.string.perm_group_telephony,
            iconRes = R.drawable.call,
            manifestPermissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) listOf(Manifest.permission.ANSWER_PHONE_CALLS) else emptyList()
        ),
        PermissionDefinition(
            id = "SEND_SMS",
            technicalName = "SEND_SMS",
            titleRes = R.string.perm_send_sms_title,
            descRes = R.string.perm_send_sms_desc,
            groupRes = R.string.perm_group_telephony,
            iconRes = R.drawable.sms,
            manifestPermissions = listOf(Manifest.permission.SEND_SMS)
        ),
        PermissionDefinition(
            id = "READ_SMS",
            technicalName = "READ_SMS",
            titleRes = R.string.perm_read_sms_title,
            descRes = R.string.perm_read_sms_desc,
            groupRes = R.string.perm_group_telephony,
            iconRes = R.drawable.sms,
            manifestPermissions = listOf(Manifest.permission.READ_SMS)
        ),
        PermissionDefinition(
            id = "RECEIVE_SMS",
            technicalName = "RECEIVE_SMS",
            titleRes = R.string.perm_receive_sms_title,
            descRes = R.string.perm_receive_sms_desc,
            groupRes = R.string.perm_group_telephony,
            iconRes = R.drawable.sms,
            manifestPermissions = listOf(Manifest.permission.RECEIVE_SMS)
        ),
        PermissionDefinition(
            id = "RECEIVE_MMS",
            technicalName = "RECEIVE_MMS",
            titleRes = R.string.perm_receive_mms_title,
            descRes = R.string.perm_receive_mms_desc,
            groupRes = R.string.perm_group_telephony,
            iconRes = R.drawable.sms,
            manifestPermissions = listOf(Manifest.permission.RECEIVE_MMS)
        ),

        // Nearby & Bluetooth
        PermissionDefinition(
            id = "BLUETOOTH_SCAN",
            technicalName = "BLUETOOTH_SCAN",
            titleRes = R.string.perm_bluetooth_scan_title,
            descRes = R.string.perm_bluetooth_scan_desc,
            groupRes = R.string.perm_group_nearby,
            iconRes = R.drawable.bluetooth,
            manifestPermissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) listOf(Manifest.permission.BLUETOOTH_SCAN) else emptyList()
        ),
        PermissionDefinition(
            id = "BLUETOOTH_CONNECT",
            technicalName = "BLUETOOTH_CONNECT",
            titleRes = R.string.perm_bluetooth_connect_title,
            descRes = R.string.perm_bluetooth_connect_desc,
            groupRes = R.string.perm_group_nearby,
            iconRes = R.drawable.bluetooth,
            manifestPermissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) listOf(Manifest.permission.BLUETOOTH_CONNECT) else emptyList()
        ),
        PermissionDefinition(
            id = "BLUETOOTH_ADVERTISE",
            technicalName = "BLUETOOTH_ADVERTISE",
            titleRes = R.string.perm_bluetooth_advertise_title,
            descRes = R.string.perm_bluetooth_advertise_desc,
            groupRes = R.string.perm_group_nearby,
            iconRes = R.drawable.bluetooth,
            manifestPermissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) listOf(Manifest.permission.BLUETOOTH_ADVERTISE) else emptyList()
        ),
        PermissionDefinition(
            id = "NEARBY_WIFI_DEVICES",
            technicalName = "NEARBY_WIFI_DEVICES",
            titleRes = R.string.perm_nearby_wifi_devices_title,
            descRes = R.string.perm_nearby_wifi_devices_desc,
            groupRes = R.string.perm_group_nearby,
            iconRes = R.drawable.wifi,
            manifestPermissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) listOf(Manifest.permission.NEARBY_WIFI_DEVICES) else emptyList()
        ),
        PermissionDefinition(
            id = "UWB_RANGING",
            technicalName = "UWB_RANGING",
            titleRes = R.string.perm_uwb_ranging_title,
            descRes = R.string.perm_uwb_ranging_desc,
            groupRes = R.string.perm_group_nearby,
            iconRes = R.drawable.wifi,
            manifestPermissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) listOf(Manifest.permission.UWB_RANGING) else emptyList()
        ),

        // Sensors & Activity
        PermissionDefinition(
            id = "BODY_SENSORS",
            technicalName = "BODY_SENSORS",
            titleRes = R.string.perm_body_sensors_title,
            descRes = R.string.perm_body_sensors_desc,
            groupRes = R.string.perm_group_sensors,
            iconRes = R.drawable.robot,
            manifestPermissions = listOf(Manifest.permission.BODY_SENSORS)
        ),
        PermissionDefinition(
            id = "BODY_SENSORS_BACKGROUND",
            technicalName = "BODY_SENSORS_BACKGROUND",
            titleRes = R.string.perm_body_sensors_background_title,
            descRes = R.string.perm_body_sensors_background_desc,
            groupRes = R.string.perm_group_sensors,
            iconRes = R.drawable.robot,
            manifestPermissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) listOf(Manifest.permission.BODY_SENSORS_BACKGROUND) else emptyList()
        ),
        PermissionDefinition(
            id = "ACTIVITY_RECOGNITION",
            technicalName = "ACTIVITY_RECOGNITION",
            titleRes = R.string.perm_activity_recognition_title,
            descRes = R.string.perm_activity_recognition_desc,
            groupRes = R.string.perm_group_sensors,
            iconRes = R.drawable.shield_locked,
            manifestPermissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) listOf(Manifest.permission.ACTIVITY_RECOGNITION) else emptyList()
        )
    )

    private data class AppMetadata(
        val pkg: String,
        val name: String,
        val requested: Set<String>
    )

    private data class FilterState(
        val query: String,
        val onlyRequested: Boolean
    )

    private val appMetadataFlow = combine(
        packageNameFlow,
        appNameFlow,
        requestedPermissionsFlow
    ) { pkg, name, req ->
        AppMetadata(pkg, name, req)
    }

    private val filterStateFlow = combine(
        searchQuery,
        onlyRequested
    ) { query, onlyReq ->
        FilterState(query, onlyReq)
    }

    private val appPoliciesFlow = packageNameFlow.flatMapLatest { pkg ->
        if (pkg.isBlank()) flowOf(emptyMap()) else contract.getAppPermissionPolicies(pkg)
    }

    override val state: StateFlow<AppPermissionScreenState> = combine(
        appMetadataFlow,
        filterStateFlow,
        contract.globallyDisabledPermissions,
        appPoliciesFlow
    ) { meta, filter, globallyDisabled, appPolicies ->
        val models = supportedPermissions.map { def ->
            val isRequested = def.manifestPermissions.any { it in meta.requested }
            val policy = appPolicies[def.id] ?: AppPermissionPolicy.DEFAULT
            val isGloballyDisabled = def.id in globallyDisabled
            AppPermissionUiModel(
                id = def.id,
                technicalName = def.technicalName,
                title = def.titleRes.uiText(),
                description = def.descRes.uiText(),
                groupName = def.groupRes.uiText(),
                iconRes = def.iconRes,
                isRequestedByApp = isRequested,
                policy = policy,
                isGloballyDisabled = isGloballyDisabled,
                canForceGrant = contract.isAppDeviceOwner || !isSensorPermission(def.id)
            )
        }

        val filtered = models.filter { model ->
            (!filter.onlyRequested || model.isRequestedByApp) &&
                (filter.query.isBlank() ||
                    model.technicalName.contains(filter.query, ignoreCase = true) ||
                    model.id.contains(filter.query, ignoreCase = true))
        }

        AppPermissionScreenState(
            packageName = meta.pkg,
            appName = meta.name,
            isDeviceOwner = contract.isAppDeviceOwner,
            isProfileOwner = contract.isAppProfileOwner,
            searchQuery = filter.query,
            onlyRequested = filter.onlyRequested,
            permissions = filtered,
            totalRequestedCount = models.count { it.isRequestedByApp },
            userControlledCount = models.count { it.policy == AppPermissionPolicy.USER_CONTROLLED },
            forceGrantedCount = models.count { it.policy == AppPermissionPolicy.FORCE_GRANTED },
            forceDeniedCount = models.count { it.policy == AppPermissionPolicy.FORCE_DENIED }
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        AppPermissionScreenState(
            isDeviceOwner = contract.isAppDeviceOwner,
            isProfileOwner = contract.isAppProfileOwner
        )
    )

    override fun onEvent(event: AppPermissionUiEvent) {
        when (event) {
            is AppPermissionUiEvent.Init -> {
                if (packageNameFlow.value != event.packageName) {
                    packageNameFlow.value = event.packageName
                    appNameFlow.value = event.appName
                    val requested = contract.getAppRequestedPermissions(event.packageName)
                    requestedPermissionsFlow.value = requested
                    onlyRequested.value = requested.isNotEmpty()
                }
            }
            is AppPermissionUiEvent.ChangeSearchQuery -> {
                searchQuery.value = event.query
            }
            is AppPermissionUiEvent.ToggleOnlyRequested -> {
                onlyRequested.value = event.onlyRequested
            }
            is AppPermissionUiEvent.ChangePolicy -> {
                val pkg = packageNameFlow.value
                if (pkg.isNotBlank()) {
                    if (event.policy == AppPermissionPolicy.FORCE_GRANTED &&
                        !contract.isAppDeviceOwner &&
                        isSensorPermission(event.permissionId)
                    ) {
                        sendToastSideEffect(R.string.sensor_grant_not_supported_profile_owner.uiText())
                        return
                    }

                    viewModelScope.launch {
                        try {
                            contract.setAppPermissionPolicy(pkg, event.permissionId, event.policy)
                        } catch (e: Exception) {
                            sendToastSideEffect(R.string.an_error_has_occurred.uiText())
                        }
                    }
                }
            }
        }
    }

    private fun isSensorPermission(permissionId: String): Boolean = when (permissionId) {
        "CAMERA",
        "RECORD_AUDIO",
        "ACCESS_FINE_LOCATION",
        "ACCESS_COARSE_LOCATION",
        "ACCESS_BACKGROUND_LOCATION",
        "BODY_SENSORS",
        "BODY_SENSORS_BACKGROUND" -> true
        else -> false
    }

    private data class PermissionDefinition(
        val id: String,
        val technicalName: String,
        val titleRes: Int,
        val descRes: Int,
        val groupRes: Int,
        val iconRes: Int,
        val manifestPermissions: List<String>
    )
}
