package com.xxmrk888ytxx.core.devicepolicy.ownerComponent

import android.app.admin.DevicePolicyManager
import android.content.Context
import android.os.Build
import android.os.UserManager
import androidx.annotation.ChecksSdkIntAtLeast
import com.xxmrk888ytxx.core.devicepolicy.DeviceRestrictionOwnerComponentManager
import com.xxmrk888ytxx.core.devicepolicy.exception.AppNotDeviceOwnerException
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onSubscription
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class DeviceRestrictionOwnerComponentManagerImpl @Inject constructor(
    devicePolicyManager: DevicePolicyManager,
    @ApplicationContext context: Context
) : BaseOwnerComponent(devicePolicyManager, context), DeviceRestrictionOwnerComponentManager {
    private val _isCameraDisabled = MutableStateFlow(checkIsCameraDisabled())
    private val _isMicrophoneDisabled = MutableStateFlow(false)
    private val _isUSBDataSignalDisabled = MutableStateFlow(checkIsUSBDataSignalDisabled())
    private val _isUSBFileTransferDisabled = MutableStateFlow(false)
    private val _isInstallAppsDisabled = MutableStateFlow(false)
    private val _isInstallAppsFromUnknownSourcesDisabled = MutableStateFlow(false)
    private val _isUninstallAppsDisabled = MutableStateFlow(false)
    private val _isAppControlDisabled = MutableStateFlow(false)
    private val _isScreenContentCaptureForAIDisabled = MutableStateFlow(false)
    private val _isContentSuggestionDisabled = MutableStateFlow(false)
    private val _isScreenshotsDisabled = MutableStateFlow(checkIsScreenshotsDisabled())
    private val _isDebugFeaturesDisabled = MutableStateFlow(false)
    private val _isFactoryResetDisabled = MutableStateFlow(false)
    private val _isSafeBootDisabled = MutableStateFlow(false)
    private val _isAddUserDisabled = MutableStateFlow(false)
    private val _isRemoveUserDisabled = MutableStateFlow(false)
    private val _isSwitchUserDisabled = MutableStateFlow(false)
    private val _isBluetoothDisabled = MutableStateFlow(false)
    private val _isBluetoothConfigDisabled = MutableStateFlow(false)
    private val _isMountPhysicalMediaDisabled = MutableStateFlow(false)
    private val _isLocationDisabled = MutableStateFlow(false)
    private val _isNFCDisabled = MutableStateFlow(false)
    private val _isOutgoingCallsDisabled = MutableStateFlow(false)
    private val _isSMSDisabled = MutableStateFlow(false)
    private val _isWallpaperChangeDisabled = MutableStateFlow(false)
    private val _isFunDisabled = MutableStateFlow(false)
    private val _isWifiConfigDisabled = MutableStateFlow(false)
    private val _isWifiStateChangeDisabled = MutableStateFlow(false)
    private val _isHotspotDisabled = MutableStateFlow(false)
    private val _isAddNewWifiNetworksDisabled = MutableStateFlow(false)
    private val _isAirplaneModeDisabled = MutableStateFlow(false)
    private val _isConfigVPNDisabled = MutableStateFlow(false)
    private val _isConfigPrivateDNSDisabled = MutableStateFlow(false)
    private val _isRoamingDisabled = MutableStateFlow(false)
    private val _isConfigMobileDataDisabled = MutableStateFlow(false)
    private val _is2GNetworkDisabled = MutableStateFlow(false)



    override val isCameraDisabled: Flow<Boolean> = _isCameraDisabled.asAndroidDeviceOwnerFlow()
    override val isMicrophoneDisabled: Flow<Boolean> =
        _isMicrophoneDisabled.asAndroidDeviceOwnerFlow()
    override val isUSBDataSignalDisabled: Flow<Boolean> =
        _isUSBDataSignalDisabled.asAndroidDeviceOwnerFlow()
    override val isUSBFileTransferDisabled: Flow<Boolean> =
        _isUSBFileTransferDisabled.asAndroidDeviceOwnerFlow()
    override val isInstallAppsDisabled: Flow<Boolean> =
        _isInstallAppsDisabled.asAndroidDeviceOwnerFlow()
    override val isInstallAppsFromUnknownSourcesDisabled: Flow<Boolean> =
        _isInstallAppsFromUnknownSourcesDisabled.asAndroidDeviceOwnerFlow()
    override val isUninstallAppsDisabled: Flow<Boolean> =
        _isUninstallAppsDisabled.asAndroidDeviceOwnerFlow()
    override val isAppControlDisabled: Flow<Boolean> =
        _isAppControlDisabled.asAndroidDeviceOwnerFlow()
    override val isScreenContentCaptureForAIDisabled: Flow<Boolean> =
        _isScreenContentCaptureForAIDisabled.asAndroidDeviceOwnerFlow()
    override val isContentSuggestionDisabled: Flow<Boolean> =
        _isContentSuggestionDisabled.asAndroidDeviceOwnerFlow()
    override val isScreenshotsDisabled: Flow<Boolean> =
        _isScreenshotsDisabled.asAndroidDeviceOwnerFlow()
    override val isDebugFeaturesDisabled: Flow<Boolean> =
        _isDebugFeaturesDisabled.asAndroidDeviceOwnerFlow()
    override val isFactoryResetDisabled: Flow<Boolean> =
        _isFactoryResetDisabled.asAndroidDeviceOwnerFlow()
    override val isSafeBootDisabled: Flow<Boolean> = _isSafeBootDisabled.asAndroidDeviceOwnerFlow()
    override val isAddUserDisabled: Flow<Boolean> = _isAddUserDisabled.asAndroidDeviceOwnerFlow()
    override val isRemoveUserDisabled: Flow<Boolean> =
        _isRemoveUserDisabled.asAndroidDeviceOwnerFlow()
    override val isSwitchUserDisabled: Flow<Boolean> =
        _isSwitchUserDisabled.asAndroidDeviceOwnerFlow()
    override val isBluetoothDisabled: Flow<Boolean> =
        _isBluetoothDisabled.asAndroidDeviceOwnerFlow()
    override val isBluetoothConfigDisabled: Flow<Boolean> =
        _isBluetoothConfigDisabled.asAndroidDeviceOwnerFlow()
    override val isMountPhysicalMediaDisabled: Flow<Boolean> =
        _isMountPhysicalMediaDisabled.asAndroidDeviceOwnerFlow()
    override val isLocationDisabled: Flow<Boolean> = _isLocationDisabled.asAndroidDeviceOwnerFlow()
    override val isNFCDisabled: Flow<Boolean> = _isNFCDisabled.asAndroidDeviceOwnerFlow()
    override val isOutgoingCallsDisabled: Flow<Boolean> =
        _isOutgoingCallsDisabled.asAndroidDeviceOwnerFlow()
    override val isSMSDisabled: Flow<Boolean> = _isSMSDisabled.asAndroidDeviceOwnerFlow()
    override val isWallpaperChangeDisabled: Flow<Boolean> =
        _isWallpaperChangeDisabled.asAndroidDeviceOwnerFlow()
    override val isFunDisabled: Flow<Boolean> = _isFunDisabled.asAndroidDeviceOwnerFlow()
    override val isWifiConfigDisabled: Flow<Boolean> = _isWifiConfigDisabled.asAndroidDeviceOwnerFlow()
    override val isWifiStateChangeDisabled: Flow<Boolean> = _isWifiStateChangeDisabled.asAndroidDeviceOwnerFlow()
    override val isHotspotDisabled: Flow<Boolean> = _isHotspotDisabled.asAndroidDeviceOwnerFlow()
    override val isAddNewWifiNetworksDisabled: Flow<Boolean> = _isAddNewWifiNetworksDisabled.asAndroidDeviceOwnerFlow()
    override val isAirplaneModeDisabled: Flow<Boolean> = _isAirplaneModeDisabled.asAndroidDeviceOwnerFlow()
    override val isConfigVPNDisabled: Flow<Boolean> = _isConfigVPNDisabled.asAndroidDeviceOwnerFlow()
    override val isConfigPrivateDNSDisabled: Flow<Boolean> = _isConfigPrivateDNSDisabled.asAndroidDeviceOwnerFlow()
    override val isRoamingDisabled: Flow<Boolean> = _isRoamingDisabled.asAndroidDeviceOwnerFlow()
    override val isConfigMobileDataDisabled: Flow<Boolean> = _isConfigMobileDataDisabled.asAndroidDeviceOwnerFlow()
    override val is2GNetworkDisabled: Flow<Boolean> = _is2GNetworkDisabled.asAndroidDeviceOwnerFlow()

    override val isCanDisableUSBDataSignal: Boolean
        get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            devicePolicyManager.canUsbDataSignalingBeDisabled()
        } else {
            false
        }
    override val isCanDisableScreenContentCaptureForAI: Boolean
        @ChecksSdkIntAtLeast(api = Build.VERSION_CODES.Q)
        get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
    override val isCanDisableContentSuggestion: Boolean
        @ChecksSdkIntAtLeast(api = Build.VERSION_CODES.Q)
        get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
    override val isCanDisableNFC: Boolean
        @ChecksSdkIntAtLeast(api = Build.VERSION_CODES.VANILLA_ICE_CREAM)
        get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM
    override val isCanDisableChangeWifiState: Boolean
        @ChecksSdkIntAtLeast(api = Build.VERSION_CODES.TIRAMISU)
        get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
    override val isCanDisableHotspot: Boolean
        @ChecksSdkIntAtLeast(api = Build.VERSION_CODES.TIRAMISU)
        get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
    override val isCanDisableAddNewWifiNetworks: Boolean
        @ChecksSdkIntAtLeast(api = Build.VERSION_CODES.TIRAMISU)
        get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
    override val isCanDisableConfigPrivateDNS: Boolean
        @ChecksSdkIntAtLeast(api = Build.VERSION_CODES.Q)
        get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
    override val isCanDisable2GNetwork: Boolean
        @ChecksSdkIntAtLeast(api = Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
        get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE

    override suspend fun updateRestrictionState() {
        if (!checkIsAppDeviceOwner()) return
        _isCameraDisabled.value = checkIsCameraDisabled()
        _isUSBDataSignalDisabled.value = checkIsUSBDataSignalDisabled()
        val userRestriction = devicePolicyManager.getUserRestrictions(deviceOwnerReceiver)
        _isMicrophoneDisabled.value =
            userRestriction.getBoolean(UserManager.DISALLOW_UNMUTE_MICROPHONE, false)
        _isUSBFileTransferDisabled.value =
            userRestriction.getBoolean(UserManager.DISALLOW_USB_FILE_TRANSFER, false)
        _isInstallAppsDisabled.value =
            userRestriction.getBoolean(UserManager.DISALLOW_INSTALL_APPS, false)
        _isInstallAppsFromUnknownSourcesDisabled.value =
            userRestriction.getBoolean(UserManager.DISALLOW_INSTALL_UNKNOWN_SOURCES, false)
        _isUninstallAppsDisabled.value =
            userRestriction.getBoolean(UserManager.DISALLOW_UNINSTALL_APPS, false)
        _isAppControlDisabled.value =
            userRestriction.getBoolean(UserManager.DISALLOW_APPS_CONTROL, false)
        if (isCanDisableScreenContentCaptureForAI) {
            _isScreenContentCaptureForAIDisabled.value =
                userRestriction.getBoolean(UserManager.DISALLOW_CONTENT_CAPTURE, false)
        }
        if (isCanDisableContentSuggestion) {
            _isContentSuggestionDisabled.value =
                userRestriction.getBoolean(UserManager.DISALLOW_CONTENT_SUGGESTIONS, false)
        }
        _isScreenshotsDisabled.value = checkIsScreenshotsDisabled()
        _isDebugFeaturesDisabled.value =
            userRestriction.getBoolean(UserManager.DISALLOW_DEBUGGING_FEATURES, false)
        _isFactoryResetDisabled.value =
            userRestriction.getBoolean(UserManager.DISALLOW_FACTORY_RESET, false)
        _isSafeBootDisabled.value =
            userRestriction.getBoolean(UserManager.DISALLOW_SAFE_BOOT, false)
        _isAddUserDisabled.value =
            userRestriction.getBoolean(UserManager.DISALLOW_ADD_USER, false)
        _isRemoveUserDisabled.value =
            userRestriction.getBoolean(UserManager.DISALLOW_REMOVE_USER, false)
        _isSwitchUserDisabled.value =
            userRestriction.getBoolean(UserManager.DISALLOW_USER_SWITCH, false)
        _isBluetoothDisabled.value =
            userRestriction.getBoolean(UserManager.DISALLOW_BLUETOOTH, false)
        _isBluetoothConfigDisabled.value =
            userRestriction.getBoolean(UserManager.DISALLOW_CONFIG_BLUETOOTH, false)
        _isMountPhysicalMediaDisabled.value =
            userRestriction.getBoolean(UserManager.DISALLOW_MOUNT_PHYSICAL_MEDIA, false)
        _isLocationDisabled.value =
            userRestriction.getBoolean(UserManager.DISALLOW_SHARE_LOCATION, false)
        if (isCanDisableNFC) {
            _isNFCDisabled.value =
                userRestriction.getBoolean(
                    UserManager.DISALLOW_NEAR_FIELD_COMMUNICATION_RADIO,
                    false
                )
        }
        _isOutgoingCallsDisabled.value =
            userRestriction.getBoolean(UserManager.DISALLOW_OUTGOING_CALLS, false)
        _isSMSDisabled.value =
            userRestriction.getBoolean(UserManager.DISALLOW_SMS, false)
        _isWallpaperChangeDisabled.value =
            userRestriction.getBoolean(UserManager.DISALLOW_SET_WALLPAPER, false)
        _isFunDisabled.value =
            userRestriction.getBoolean(UserManager.DISALLOW_FUN, false)
        _isWifiConfigDisabled.value =
            userRestriction.getBoolean(UserManager.DISALLOW_CONFIG_WIFI, false)
        if (isCanDisableChangeWifiState) {
            _isWifiStateChangeDisabled.value =
                userRestriction.getBoolean(UserManager.DISALLOW_CHANGE_WIFI_STATE, false)
        }
        if (isCanDisableHotspot) {
            _isHotspotDisabled.value =
                userRestriction.getBoolean(UserManager.DISALLOW_WIFI_TETHERING, false)
        }
        if (isCanDisableAddNewWifiNetworks) {
            _isAddNewWifiNetworksDisabled.value =
                userRestriction.getBoolean(UserManager.DISALLOW_ADD_WIFI_CONFIG, false)
        }
        _isAirplaneModeDisabled.value =
            userRestriction.getBoolean(UserManager.DISALLOW_AIRPLANE_MODE, false)
        _isConfigVPNDisabled.value =
            userRestriction.getBoolean(UserManager.DISALLOW_CONFIG_VPN, false)
        if (isCanDisableConfigPrivateDNS) {
            _isConfigPrivateDNSDisabled.value =
                userRestriction.getBoolean(UserManager.DISALLOW_CONFIG_PRIVATE_DNS, false)
        }
        _isRoamingDisabled.value =
            userRestriction.getBoolean(UserManager.DISALLOW_DATA_ROAMING, false)
        _isConfigMobileDataDisabled.value =
            userRestriction.getBoolean(UserManager.DISALLOW_CONFIG_MOBILE_NETWORKS, false)
        if (isCanDisable2GNetwork) {
            _is2GNetworkDisabled.value =
                userRestriction.getBoolean(UserManager.DISALLOW_CELLULAR_2G, false)
        }
    }

    override suspend fun setCameraDisabled(isDisabled: Boolean) = changeDeviceOwnerPolicy {
        setCameraDisabled(deviceOwnerReceiver, isDisabled)
    }

    override suspend fun setMicrophoneDisabled(isDisabled: Boolean) = changeDeviceOwnerPolicy {
        toggleUserRestriction(UserManager.DISALLOW_UNMUTE_MICROPHONE, isDisabled)
    }

    override suspend fun setUSBDataSignalDisabled(isDisabled: Boolean) = changeDeviceOwnerPolicy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            devicePolicyManager.isUsbDataSignalingEnabled = !isDisabled
        }
    }

    override suspend fun setUSBFileTransferDisabled(isDisabled: Boolean) = changeDeviceOwnerPolicy {
        toggleUserRestriction(UserManager.DISALLOW_USB_FILE_TRANSFER, isDisabled)
    }

    override suspend fun setInstallAppsDisabled(isDisabled: Boolean) = changeDeviceOwnerPolicy {
        toggleUserRestriction(UserManager.DISALLOW_INSTALL_APPS, isDisabled)
    }

    override suspend fun setInstallAppsFromUnknownSourcesDisabled(isDisabled: Boolean) =
        changeDeviceOwnerPolicy {
            toggleUserRestriction(UserManager.DISALLOW_INSTALL_UNKNOWN_SOURCES, isDisabled)
        }

    override suspend fun setUninstallAppsDisabled(isDisabled: Boolean) = changeDeviceOwnerPolicy {
        toggleUserRestriction(UserManager.DISALLOW_UNINSTALL_APPS, isDisabled)
    }

    override suspend fun setAppControlDisabled(isDisabled: Boolean) = changeDeviceOwnerPolicy {
        toggleUserRestriction(UserManager.DISALLOW_APPS_CONTROL, isDisabled)
    }

    override suspend fun setScreenContentCaptureForAIDisabled(isDisabled: Boolean) =
        changeDeviceOwnerPolicy {
            if (isCanDisableScreenContentCaptureForAI) {
                toggleUserRestriction(UserManager.DISALLOW_CONTENT_CAPTURE, isDisabled)
            }
        }

    override suspend fun setContentSuggestionDisabled(isDisabled: Boolean) =
        changeDeviceOwnerPolicy {
            if (isCanDisableContentSuggestion) {
                toggleUserRestriction(UserManager.DISALLOW_CONTENT_SUGGESTIONS, isDisabled)
            }
        }

    override suspend fun setScreenshotsDisabled(isDisabled: Boolean) = changeDeviceOwnerPolicy {
        devicePolicyManager.setScreenCaptureDisabled(deviceOwnerReceiver, isDisabled)
    }

    override suspend fun setDebugFeaturesDisabled(isDisabled: Boolean) = changeDeviceOwnerPolicy {
        toggleUserRestriction(UserManager.DISALLOW_DEBUGGING_FEATURES, isDisabled)
    }

    override suspend fun setFactoryResetDisabled(isDisabled: Boolean) = changeDeviceOwnerPolicy {
        toggleUserRestriction(UserManager.DISALLOW_FACTORY_RESET, isDisabled)
    }

    override suspend fun setSafeBootDisabled(isDisabled: Boolean) = changeDeviceOwnerPolicy {
        toggleUserRestriction(UserManager.DISALLOW_SAFE_BOOT, isDisabled)
    }

    override suspend fun setAddUserDisabled(isDisabled: Boolean) = changeDeviceOwnerPolicy {
        toggleUserRestriction(UserManager.DISALLOW_ADD_USER, isDisabled)
    }

    override suspend fun setRemoveUserDisabled(isDisabled: Boolean) = changeDeviceOwnerPolicy {
        toggleUserRestriction(UserManager.DISALLOW_REMOVE_USER, isDisabled)
    }

    override suspend fun setSwitchUserDisabled(isDisabled: Boolean) = changeDeviceOwnerPolicy {
        toggleUserRestriction(UserManager.DISALLOW_USER_SWITCH, isDisabled)
    }

    override suspend fun setBluetoothDisabled(isDisabled: Boolean) = changeDeviceOwnerPolicy {
        toggleUserRestriction(UserManager.DISALLOW_BLUETOOTH, isDisabled)
    }

    override suspend fun setBluetoothConfigDisabled(isDisabled: Boolean) = changeDeviceOwnerPolicy {
        toggleUserRestriction(UserManager.DISALLOW_CONFIG_BLUETOOTH, isDisabled)
    }

    override suspend fun setMountPhysicalMediaDisabled(isDisabled: Boolean) =
        changeDeviceOwnerPolicy {
            toggleUserRestriction(UserManager.DISALLOW_MOUNT_PHYSICAL_MEDIA, isDisabled)
        }

    override suspend fun setLocationDisabled(isDisabled: Boolean) = changeDeviceOwnerPolicy {
        toggleUserRestriction(UserManager.DISALLOW_SHARE_LOCATION, isDisabled)
    }

    override suspend fun setOutgoingCallsDisabled(isDisabled: Boolean) = changeDeviceOwnerPolicy {
        toggleUserRestriction(UserManager.DISALLOW_OUTGOING_CALLS, isDisabled)
    }

    override suspend fun setSMSDisabled(isDisabled: Boolean) = changeDeviceOwnerPolicy {
        toggleUserRestriction(UserManager.DISALLOW_SMS, isDisabled)
    }

    override suspend fun setNFCDisabled(isDisabled: Boolean) = changeDeviceOwnerPolicy {
        if (isCanDisableNFC) {
            toggleUserRestriction(UserManager.DISALLOW_NEAR_FIELD_COMMUNICATION_RADIO, isDisabled)
        }
    }

    override suspend fun setWallpaperChangeDisabled(isDisabled: Boolean) = changeDeviceOwnerPolicy {
        toggleUserRestriction(UserManager.DISALLOW_SET_WALLPAPER, isDisabled)
    }

    override suspend fun setFunDisabled(isDisabled: Boolean) = changeDeviceOwnerPolicy {
        toggleUserRestriction(UserManager.DISALLOW_FUN, isDisabled)
    }

    override suspend fun setWifiConfigDisabled(isDisabled: Boolean) = changeDeviceOwnerPolicy {
        toggleUserRestriction(UserManager.DISALLOW_CONFIG_WIFI, isDisabled)
    }

    override suspend fun setWifiStateChangeDisabled(isDisabled: Boolean) = changeDeviceOwnerPolicy {
        if (isCanDisableChangeWifiState) {
            toggleUserRestriction(UserManager.DISALLOW_CHANGE_WIFI_STATE, isDisabled)
        }
    }

    override suspend fun setHotspotDisabled(isDisabled: Boolean) = changeDeviceOwnerPolicy {
        if(isCanDisableHotspot) {
            toggleUserRestriction(UserManager.DISALLOW_WIFI_TETHERING, isDisabled)
        }
    }

    override suspend fun setAddNewWifiNetworksDisabled(isDisabled: Boolean) = changeDeviceOwnerPolicy {
        if (isCanDisableAddNewWifiNetworks) {
            toggleUserRestriction(UserManager.DISALLOW_ADD_WIFI_CONFIG, isDisabled)
        }
    }

    override suspend fun setAirplaneModeDisabled(isDisabled: Boolean) = changeDeviceOwnerPolicy {
        toggleUserRestriction(UserManager.DISALLOW_AIRPLANE_MODE, isDisabled)
    }

    override suspend fun setConfigVPNDisabled(isDisabled: Boolean) = changeDeviceOwnerPolicy {
        toggleUserRestriction(UserManager.DISALLOW_CONFIG_VPN, isDisabled)
    }

    override suspend fun setConfigPrivateDNSDisabled(isDisabled: Boolean) = changeDeviceOwnerPolicy {
        if (isCanDisableConfigPrivateDNS) {
            toggleUserRestriction(UserManager.DISALLOW_CONFIG_PRIVATE_DNS, isDisabled)
        }
    }

    override suspend fun setRoamingDisabled(isDisabled: Boolean) = changeDeviceOwnerPolicy {
        toggleUserRestriction(UserManager.DISALLOW_DATA_ROAMING, isDisabled)
    }

    override suspend fun setConfigMobileDataDisabled(isDisabled: Boolean) = changeDeviceOwnerPolicy {
        toggleUserRestriction(UserManager.DISALLOW_CONFIG_MOBILE_NETWORKS, isDisabled)
    }

    override suspend fun set2GNetworkDisabled(isDisabled: Boolean) = changeDeviceOwnerPolicy {
        if (isCanDisable2GNetwork) {
            toggleUserRestriction(UserManager.DISALLOW_CELLULAR_2G, isDisabled)
        }
    }

    private suspend fun toggleUserRestriction(restrictionKey: String, isEnabled: Boolean) {
        if (isEnabled) {
            devicePolicyManager.addUserRestriction(deviceOwnerReceiver, restrictionKey)
        } else {
            devicePolicyManager.clearUserRestriction(deviceOwnerReceiver, restrictionKey)
        }
    }

    private suspend fun changeDeviceOwnerPolicy(block: suspend DevicePolicyManager.() -> Unit) {
        if (!checkIsAppDeviceOwner()) throw AppNotDeviceOwnerException()
        devicePolicyManager.block()
        updateRestrictionState()
    }

    private fun checkIsCameraDisabled() = try {
        devicePolicyManager.getCameraDisabled(deviceOwnerReceiver)
    } catch (_: SecurityException) {
        false
    }

    private fun checkIsScreenshotsDisabled() = try {
        devicePolicyManager.getScreenCaptureDisabled(deviceOwnerReceiver)
    } catch (_: SecurityException) {
        false
    }

    private fun checkIsUSBDataSignalDisabled() =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            !devicePolicyManager.isUsbDataSignalingEnabled
        } else {
            false
        }

    private fun <T> MutableStateFlow<T>.asAndroidDeviceOwnerFlow(): Flow<T> =
        asStateFlow().onSubscription { updateRestrictionState() }
}