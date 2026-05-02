package com.xxmrk888ytxx.core.devicepolicy

import kotlinx.coroutines.flow.Flow

interface DeviceRestrictionOwnerComponentManager {
    val isCameraDisabled: Flow<Boolean>
    val isMicrophoneDisabled: Flow<Boolean>
    val isInstallAppsDisabled: Flow<Boolean>
    val isInstallAppsFromUnknownSourcesDisabled: Flow<Boolean>
    val isUninstallAppsDisabled: Flow<Boolean>
    val isAppControlDisabled: Flow<Boolean>
    val isScreenContentCaptureForAIDisabled: Flow<Boolean>
    val isContentSuggestionDisabled: Flow<Boolean>
    val isScreenshotsDisabled: Flow<Boolean>
    val isDebugFeaturesDisabled: Flow<Boolean>


    @DeprecatedDeviceOwnerRestriction
    val isUSBDataSignalDisabled: Flow<Boolean>
    @DeprecatedDeviceOwnerRestriction
    val isUSBFileTransferDisabled: Flow<Boolean>
    @DeprecatedDeviceOwnerRestriction
    val isFactoryResetDisabled: Flow<Boolean>
    @DeprecatedDeviceOwnerRestriction
    val isSafeBootDisabled: Flow<Boolean>
    @DeprecatedDeviceOwnerRestriction
    val isAddUserDisabled: Flow<Boolean>
    @DeprecatedDeviceOwnerRestriction
    val isRemoveUserDisabled: Flow<Boolean>
    @DeprecatedDeviceOwnerRestriction
    val isSwitchUserDisabled: Flow<Boolean>
    @DeprecatedDeviceOwnerRestriction
    val isBluetoothDisabled: Flow<Boolean>
    @DeprecatedDeviceOwnerRestriction
    val isBluetoothConfigDisabled: Flow<Boolean>
    @DeprecatedDeviceOwnerRestriction
    val isMountPhysicalMediaDisabled: Flow<Boolean>
    val isLocationDisabled: Flow<Boolean>
    @DeprecatedDeviceOwnerRestriction
    val isNFCDisabled: Flow<Boolean>
    @DeprecatedDeviceOwnerRestriction
    val isOutgoingCallsDisabled: Flow<Boolean>
    @DeprecatedDeviceOwnerRestriction
    val isSMSDisabled: Flow<Boolean>
    @DeprecatedDeviceOwnerRestriction
    val isWallpaperChangeDisabled: Flow<Boolean>
    @DeprecatedDeviceOwnerRestriction
    val isFunDisabled: Flow<Boolean>
    @DeprecatedDeviceOwnerRestriction
    val isWifiConfigDisabled: Flow<Boolean>
    @DeprecatedDeviceOwnerRestriction
    val isWifiStateChangeDisabled: Flow<Boolean>
    @DeprecatedDeviceOwnerRestriction
    val isHotspotDisabled: Flow<Boolean>
    @DeprecatedDeviceOwnerRestriction
    val isAddNewWifiNetworksDisabled: Flow<Boolean>
    @DeprecatedDeviceOwnerRestriction
    val isAirplaneModeDisabled: Flow<Boolean>
    val isConfigVPNDisabled: Flow<Boolean>
    @DeprecatedDeviceOwnerRestriction
    val isConfigPrivateDNSDisabled: Flow<Boolean>
    @DeprecatedDeviceOwnerRestriction
    val isRoamingDisabled: Flow<Boolean>
    @DeprecatedDeviceOwnerRestriction
    val isConfigMobileDataDisabled: Flow<Boolean>
    @DeprecatedDeviceOwnerRestriction
    val is2GNetworkDisabled: Flow<Boolean>

    val isCanDisableScreenContentCaptureForAI: Boolean
    val isCanDisableContentSuggestion: Boolean


    @DeprecatedDeviceOwnerRestriction
    val isCanDisableUSBDataSignal: Boolean
    @DeprecatedDeviceOwnerRestriction
    val isCanDisableNFC: Boolean
    @DeprecatedDeviceOwnerRestriction
    val isCanDisableChangeWifiState: Boolean
    @DeprecatedDeviceOwnerRestriction
    val isCanDisableHotspot: Boolean
    @DeprecatedDeviceOwnerRestriction
    val isCanDisableAddNewWifiNetworks: Boolean
    @DeprecatedDeviceOwnerRestriction
    val isCanDisableConfigPrivateDNS: Boolean
    @DeprecatedDeviceOwnerRestriction
    val isCanDisable2GNetwork: Boolean

    suspend fun setCameraDisabled(isDisabled: Boolean)
    suspend fun setMicrophoneDisabled(isDisabled: Boolean)
    suspend fun setInstallAppsDisabled(isDisabled: Boolean)
    suspend fun setInstallAppsFromUnknownSourcesDisabled(isDisabled: Boolean)
    suspend fun setUninstallAppsDisabled(isDisabled: Boolean)
    suspend fun setAppControlDisabled(isDisabled: Boolean)
    suspend fun setScreenContentCaptureForAIDisabled(isDisabled: Boolean)
    suspend fun setContentSuggestionDisabled(isDisabled: Boolean)
    suspend fun setScreenshotsDisabled(isDisabled: Boolean)
    suspend fun setDebugFeaturesDisabled(isDisabled: Boolean)
    suspend fun setConfigVPNDisabled(isDisabled: Boolean)

    @DeprecatedDeviceOwnerRestriction
    suspend fun setUSBDataSignalDisabled(isDisabled: Boolean)
    @DeprecatedDeviceOwnerRestriction
    suspend fun setUSBFileTransferDisabled(isDisabled: Boolean)
    @DeprecatedDeviceOwnerRestriction
    suspend fun setFactoryResetDisabled(isDisabled: Boolean)
    @DeprecatedDeviceOwnerRestriction
    suspend fun setSafeBootDisabled(isDisabled: Boolean)
    @DeprecatedDeviceOwnerRestriction
    suspend fun setAddUserDisabled(isDisabled: Boolean)
    @DeprecatedDeviceOwnerRestriction
    suspend fun setRemoveUserDisabled(isDisabled: Boolean)
    @DeprecatedDeviceOwnerRestriction
    suspend fun setSwitchUserDisabled(isDisabled: Boolean)
    @DeprecatedDeviceOwnerRestriction
    suspend fun setBluetoothDisabled(isDisabled: Boolean)
    @DeprecatedDeviceOwnerRestriction
    suspend fun setBluetoothConfigDisabled(isDisabled: Boolean)
    @DeprecatedDeviceOwnerRestriction
    suspend fun setMountPhysicalMediaDisabled(isDisabled: Boolean)
    suspend fun setLocationDisabled(isDisabled: Boolean)
    @DeprecatedDeviceOwnerRestriction
    suspend fun setOutgoingCallsDisabled(isDisabled: Boolean)
    @DeprecatedDeviceOwnerRestriction
    suspend fun setSMSDisabled(isDisabled: Boolean)
    @DeprecatedDeviceOwnerRestriction
    suspend fun setNFCDisabled(isDisabled: Boolean)
    @DeprecatedDeviceOwnerRestriction
    suspend fun setWallpaperChangeDisabled(isDisabled: Boolean)
    @DeprecatedDeviceOwnerRestriction
    suspend fun setFunDisabled(isDisabled: Boolean)
    @DeprecatedDeviceOwnerRestriction
    suspend fun setWifiConfigDisabled(isDisabled: Boolean)
    @DeprecatedDeviceOwnerRestriction
    suspend fun setWifiStateChangeDisabled(isDisabled: Boolean)
    @DeprecatedDeviceOwnerRestriction
    suspend fun setHotspotDisabled(isDisabled: Boolean)
    @DeprecatedDeviceOwnerRestriction
    suspend fun setAddNewWifiNetworksDisabled(isDisabled: Boolean)
    @DeprecatedDeviceOwnerRestriction
    suspend fun setAirplaneModeDisabled(isDisabled: Boolean)
    @DeprecatedDeviceOwnerRestriction
    suspend fun setConfigPrivateDNSDisabled(isDisabled: Boolean)
    @DeprecatedDeviceOwnerRestriction
    suspend fun setRoamingDisabled(isDisabled: Boolean)
    @DeprecatedDeviceOwnerRestriction
    suspend fun setConfigMobileDataDisabled(isDisabled: Boolean)
    @DeprecatedDeviceOwnerRestriction
    suspend fun set2GNetworkDisabled(isDisabled: Boolean)

    suspend fun updateRestrictionState()
}