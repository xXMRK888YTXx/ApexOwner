package com.xxmrk888ytxx.feature.managementmodule.apprestriction.contract

import kotlinx.coroutines.flow.Flow

interface RestrictionManageContract {
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
    val isMountPhysicalMediaDisabled: Flow<Boolean>
    val isBluetoothDisabled: Flow<Boolean>
    val isBluetoothConfigDisabled: Flow<Boolean>
    val isNFCDisabled: Flow<Boolean>
    val isLocationDisabled: Flow<Boolean>
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
    suspend fun setCameraDisabled(isDisabled: Boolean) : Result<Unit>
    suspend fun setMicrophoneDisabled(isDisabled: Boolean): Result<Unit>
    suspend fun setUSBDataSignalDisabled(isDisabled: Boolean): Result<Unit>
    suspend fun setUSBFileTransferDisabled(isDisabled: Boolean): Result<Unit>
    suspend fun setInstallAppsDisabled(isDisabled: Boolean): Result<Unit>
    suspend fun setInstallAppsFromUnknownSourcesDisabled(isDisabled: Boolean): Result<Unit>
    suspend fun setUninstallAppsDisabled(isDisabled: Boolean): Result<Unit>
    suspend fun setAppControlDisabled(isDisabled: Boolean): Result<Unit>
    suspend fun setScreenContentCaptureForAIDisabled(isDisabled: Boolean): Result<Unit>
    suspend fun setContentSuggestionDisabled(isDisabled: Boolean): Result<Unit>
    suspend fun setScreenshotsDisabled(isDisabled: Boolean): Result<Unit>
    suspend fun setDebugFeaturesDisabled(isDisabled: Boolean): Result<Unit>
    suspend fun setFactoryResetDisabled(isDisabled: Boolean): Result<Unit>
    suspend fun setSafeBootDisabled(isDisabled: Boolean): Result<Unit>
    suspend fun setAddUserDisabled(isDisabled: Boolean): Result<Unit>
    suspend fun setRemoveUserDisabled(isDisabled: Boolean): Result<Unit>
    suspend fun setSwitchUserDisabled(isDisabled: Boolean): Result<Unit>
    suspend fun setMountPhysicalMediaDisabled(isDisabled: Boolean): Result<Unit>
    suspend fun setBluetoothDisabled(isDisabled: Boolean): Result<Unit>
    suspend fun setBluetoothConfigDisabled(isDisabled: Boolean): Result<Unit>
    suspend fun setNFCDisabled(isDisabled: Boolean): Result<Unit>
    suspend fun setLocationDisabled(isDisabled: Boolean): Result<Unit>
    suspend fun setOutgoingCallsDisabled(isDisabled: Boolean): Result<Unit>
    suspend fun setSMSDisabled(isDisabled: Boolean): Result<Unit>
    suspend fun setWallpaperChangeDisabled(isDisabled: Boolean): Result<Unit>
    suspend fun setFunDisabled(isDisabled: Boolean): Result<Unit>
    suspend fun setWifiStateChangeDisabled(isDisabled: Boolean): Result<Unit>
    suspend fun setWifiConfigDisabled(isDisabled: Boolean): Result<Unit>
    suspend fun setRoamingDisabled(isDisabled: Boolean): Result<Unit>
    suspend fun setMobileDataDisabled(isDisabled: Boolean): Result<Unit>
    suspend fun setHotspotDisabled(isDisabled: Boolean): Result<Unit>
    suspend fun setConfigVPNDisabled(isDisabled: Boolean): Result<Unit>
    suspend fun setConfigPrivateDNSDisabled(isDisabled: Boolean): Result<Unit>
    suspend fun setAirplaneModeDisabled(isDisabled: Boolean): Result<Unit>
    suspend fun setAddNewWifiNetworksDisabled(isDisabled: Boolean): Result<Unit>
    suspend fun set2GNetworkDisabled(isDisabled: Boolean): Result<Unit>
}