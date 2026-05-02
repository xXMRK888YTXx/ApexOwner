package com.xxmrk888ytxx.core.devicepolicy

import kotlinx.coroutines.flow.Flow

interface DeviceRestrictionOwnerComponentManager {
    val isCameraDisabled: Flow<Boolean>
    val isMicrophoneDisabled: Flow<Boolean>
    val isUSBDataSignalDisabled: Flow<Boolean>
    val isUSBFileTransferDisabled: Flow<Boolean>
    val isInstallAppsDisabled: Flow<Boolean>
    val isInstallAppsFromUnknownSourcesDisabled: Flow<Boolean>
    val isUninstallAppsDisabled: Flow<Boolean>
    val isAppControlDisabled: Flow<Boolean>
    val isScreenContentCaptureForAIDisabled: Flow<Boolean>
    val isContentSuggestionDisabled: Flow<Boolean>
    val isScreenshotsDisabled: Flow<Boolean>
    val isDebugFeaturesDisabled: Flow<Boolean>
    val isFactoryResetDisabled: Flow<Boolean>
    val isSafeBootDisabled: Flow<Boolean>
    val isAddUserDisabled: Flow<Boolean>
    val isRemoveUserDisabled: Flow<Boolean>
    val isSwitchUserDisabled: Flow<Boolean>
    val isBluetoothDisabled: Flow<Boolean>
    val isBluetoothConfigDisabled: Flow<Boolean>
    val isMountPhysicalMediaDisabled: Flow<Boolean>
    val isLocationDisabled: Flow<Boolean>
    val isNFCDisabled: Flow<Boolean>
    val isOutgoingCallsDisabled: Flow<Boolean>
    val isSMSDisabled: Flow<Boolean>
    val isWallpaperChangeDisabled: Flow<Boolean>
    val isFunDisabled: Flow<Boolean>
    val isWifiConfigDisabled: Flow<Boolean>
    val isWifiStateChangeDisabled: Flow<Boolean>
    val isHotspotDisabled: Flow<Boolean>
    val isAddNewWifiNetworksDisabled: Flow<Boolean>
    val isAirplaneModeDisabled: Flow<Boolean>
    val isConfigVPNDisabled: Flow<Boolean>
    val isConfigPrivateDNSDisabled: Flow<Boolean>
    val isRoamingDisabled: Flow<Boolean>
    val isConfigMobileDataDisabled: Flow<Boolean>
    val is2GNetworkDisabled: Flow<Boolean>
    val isCanDisableUSBDataSignal: Boolean
    val isCanDisableScreenContentCaptureForAI: Boolean
    val isCanDisableContentSuggestion: Boolean
    val isCanDisableNFC: Boolean
    val isCanDisableChangeWifiState: Boolean
    val isCanDisableHotspot: Boolean
    val isCanDisableAddNewWifiNetworks: Boolean
    val isCanDisableConfigPrivateDNS: Boolean
    val isCanDisable2GNetwork: Boolean
    suspend fun setCameraDisabled(isDisabled: Boolean)
    suspend fun setMicrophoneDisabled(isDisabled: Boolean)
    suspend fun setUSBDataSignalDisabled(isDisabled: Boolean)
    suspend fun setUSBFileTransferDisabled(isDisabled: Boolean)
    suspend fun setInstallAppsDisabled(isDisabled: Boolean)
    suspend fun setInstallAppsFromUnknownSourcesDisabled(isDisabled: Boolean)
    suspend fun setUninstallAppsDisabled(isDisabled: Boolean)
    suspend fun setAppControlDisabled(isDisabled: Boolean)
    suspend fun setScreenContentCaptureForAIDisabled(isDisabled: Boolean)
    suspend fun setContentSuggestionDisabled(isDisabled: Boolean)
    suspend fun setScreenshotsDisabled(isDisabled: Boolean)
    suspend fun setDebugFeaturesDisabled(isDisabled: Boolean)
    suspend fun setFactoryResetDisabled(isDisabled: Boolean)
    suspend fun setSafeBootDisabled(isDisabled: Boolean)
    suspend fun setAddUserDisabled(isDisabled: Boolean)
    suspend fun setRemoveUserDisabled(isDisabled: Boolean)
    suspend fun setSwitchUserDisabled(isDisabled: Boolean)
    suspend fun setBluetoothDisabled(isDisabled: Boolean)
    suspend fun setBluetoothConfigDisabled(isDisabled: Boolean)
    suspend fun setMountPhysicalMediaDisabled(isDisabled: Boolean)
    suspend fun setLocationDisabled(isDisabled: Boolean)
    suspend fun setOutgoingCallsDisabled(isDisabled: Boolean)
    suspend fun setSMSDisabled(isDisabled: Boolean)
    suspend fun setNFCDisabled(isDisabled: Boolean)
    suspend fun setWallpaperChangeDisabled(isDisabled: Boolean)
    suspend fun setFunDisabled(isDisabled: Boolean)
    suspend fun setWifiConfigDisabled(isDisabled: Boolean)
    suspend fun setWifiStateChangeDisabled(isDisabled: Boolean)
    suspend fun setHotspotDisabled(isDisabled: Boolean)
    suspend fun setAddNewWifiNetworksDisabled(isDisabled: Boolean)
    suspend fun setAirplaneModeDisabled(isDisabled: Boolean)
    suspend fun setConfigVPNDisabled(isDisabled: Boolean)
    suspend fun setConfigPrivateDNSDisabled(isDisabled: Boolean)
    suspend fun setRoamingDisabled(isDisabled: Boolean)
    suspend fun setConfigMobileDataDisabled(isDisabled: Boolean)
    suspend fun set2GNetworkDisabled(isDisabled: Boolean)
    suspend fun updateRestrictionState()
}