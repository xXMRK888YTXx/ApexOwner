package com.xxmrk888ytxx.apexowner.featureContract.appRestriction

import com.xxmrk888ytxx.core.base.android.extension.coRunCatching
import com.xxmrk888ytxx.core.devicepolicy.DeviceRestrictionOwnerComponentManager
import com.xxmrk888ytxx.feature.managementmodule.apprestriction.contract.RestrictionManageContract
import com.xxmrk888ytxx.feature.managementmodule.apprestriction.exception.AppNotDeviceOwnerException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RestrictionManageContractImpl @Inject constructor(
    private val deviceRestrictionOwnerComponentManager: DeviceRestrictionOwnerComponentManager
) : RestrictionManageContract {
    override val isCameraDisabled: Flow<Boolean> = deviceRestrictionOwnerComponentManager.isCameraDisabled
    override val isMicrophoneDisabled: Flow<Boolean> = deviceRestrictionOwnerComponentManager.isMicrophoneDisabled
    override val isUSBDataSignalDisabled: Flow<Boolean> =
        deviceRestrictionOwnerComponentManager.isUSBDataSignalDisabled
    override val isUSBFileTransferDisabled: Flow<Boolean> =
        deviceRestrictionOwnerComponentManager.isUSBFileTransferDisabled
    override val isInstallAppsDisabled: Flow<Boolean> =
        deviceRestrictionOwnerComponentManager.isInstallAppsDisabled
    override val isInstallAppsFromUnknownSourcesDisabled: Flow<Boolean> =
        deviceRestrictionOwnerComponentManager.isInstallAppsFromUnknownSourcesDisabled
    override val isUninstallAppsDisabled: Flow<Boolean> =
        deviceRestrictionOwnerComponentManager.isUninstallAppsDisabled
    override val isAppControlDisabled: Flow<Boolean> = deviceRestrictionOwnerComponentManager.isAppControlDisabled
    override val isScreenContentCaptureForAIDisabled: Flow<Boolean> =
        deviceRestrictionOwnerComponentManager.isScreenContentCaptureForAIDisabled
    override val isContentSuggestionDisabled: Flow<Boolean> =
        deviceRestrictionOwnerComponentManager.isContentSuggestionDisabled
    override val isScreenshotsDisabled: Flow<Boolean> =
        deviceRestrictionOwnerComponentManager.isScreenshotsDisabled
    override val isDebugFeaturesDisabled: Flow<Boolean> =
        deviceRestrictionOwnerComponentManager.isDebugFeaturesDisabled
    override val isFactoryResetDisabled: Flow<Boolean> =
        deviceRestrictionOwnerComponentManager.isFactoryResetDisabled
    override val isSafeBootDisabled: Flow<Boolean> = deviceRestrictionOwnerComponentManager.isSafeBootDisabled
    override val isAddUserDisabled: Flow<Boolean> = deviceRestrictionOwnerComponentManager.isAddUserDisabled
    override val isRemoveUserDisabled: Flow<Boolean> = deviceRestrictionOwnerComponentManager.isRemoveUserDisabled
    override val isSwitchUserDisabled: Flow<Boolean> = deviceRestrictionOwnerComponentManager.isSwitchUserDisabled
    override val isMountPhysicalMediaDisabled: Flow<Boolean> =
        deviceRestrictionOwnerComponentManager.isMountPhysicalMediaDisabled
    override val isBluetoothDisabled: Flow<Boolean> = deviceRestrictionOwnerComponentManager.isBluetoothDisabled
    override val isBluetoothConfigDisabled: Flow<Boolean> =
        deviceRestrictionOwnerComponentManager.isBluetoothConfigDisabled
    override val isNFCDisabled: Flow<Boolean> = deviceRestrictionOwnerComponentManager.isNFCDisabled
    override val isLocationDisabled: Flow<Boolean> = deviceRestrictionOwnerComponentManager.isLocationDisabled
    override val isOutgoingCallsDisabled: Flow<Boolean> =
        deviceRestrictionOwnerComponentManager.isOutgoingCallsDisabled
    override val isSMSDisabled: Flow<Boolean> = deviceRestrictionOwnerComponentManager.isSMSDisabled
    override val isWallpaperChangeDisabled: Flow<Boolean> =
        deviceRestrictionOwnerComponentManager.isWallpaperChangeDisabled
    override val isFunDisabled: Flow<Boolean> = deviceRestrictionOwnerComponentManager.isFunDisabled
    override val isWifiConfigDisabled: Flow<Boolean> = deviceRestrictionOwnerComponentManager.isWifiConfigDisabled
    override val isWifiStateChangeDisabled: Flow<Boolean> = deviceRestrictionOwnerComponentManager.isWifiStateChangeDisabled
    override val isHotspotDisabled: Flow<Boolean> = deviceRestrictionOwnerComponentManager.isHotspotDisabled
    override val isAddNewWifiNetworksDisabled: Flow<Boolean> = deviceRestrictionOwnerComponentManager.isAddNewWifiNetworksDisabled
    override val isAirplaneModeDisabled: Flow<Boolean> = deviceRestrictionOwnerComponentManager.isAirplaneModeDisabled
    override val isConfigVPNDisabled: Flow<Boolean> = deviceRestrictionOwnerComponentManager.isConfigVPNDisabled
    override val isConfigPrivateDNSDisabled: Flow<Boolean> = deviceRestrictionOwnerComponentManager.isConfigPrivateDNSDisabled
    override val isRoamingDisabled: Flow<Boolean> = deviceRestrictionOwnerComponentManager.isRoamingDisabled
    override val isConfigMobileDataDisabled: Flow<Boolean> = deviceRestrictionOwnerComponentManager.isConfigMobileDataDisabled
    override val is2GNetworkDisabled: Flow<Boolean> = deviceRestrictionOwnerComponentManager.is2GNetworkDisabled


    override val isCanDisableUSBDataSignal: Boolean
        get() = deviceRestrictionOwnerComponentManager.isCanDisableUSBDataSignal
    override val isCanDisableScreenContentCaptureForAI: Boolean
        get() = deviceRestrictionOwnerComponentManager.isCanDisableScreenContentCaptureForAI
    override val isCanDisableContentSuggestion: Boolean
        get() = deviceRestrictionOwnerComponentManager.isCanDisableContentSuggestion
    override val isCanDisableNFC: Boolean
        get() = deviceRestrictionOwnerComponentManager.isCanDisableNFC
    override val isCanDisableChangeWifiState: Boolean
        get()  = deviceRestrictionOwnerComponentManager.isCanDisableChangeWifiState
    override val isCanDisableHotspot: Boolean
        get() = deviceRestrictionOwnerComponentManager.isCanDisableHotspot
    override val isCanDisableAddNewWifiNetworks: Boolean
        get() = deviceRestrictionOwnerComponentManager.isCanDisableAddNewWifiNetworks
    override val isCanDisableConfigPrivateDNS: Boolean
        get() = deviceRestrictionOwnerComponentManager.isCanDisableConfigPrivateDNS
    override val isCanDisable2GNetwork: Boolean
        get() = deviceRestrictionOwnerComponentManager.isCanDisable2GNetwork

    private val exceptionMapper: (Throwable) -> Throwable = {
        when (it) {
            is com.xxmrk888ytxx.core.devicepolicy.exception.AppNotDeviceOwnerException -> AppNotDeviceOwnerException()
            else -> it
        }
    }

    override suspend fun setCameraDisabled(isDisabled: Boolean): Result<Unit> =
        coRunCatching(Dispatchers.Default, onMapException = exceptionMapper) {
            deviceRestrictionOwnerComponentManager.setCameraDisabled(isDisabled)
        }

    override suspend fun setMicrophoneDisabled(isDisabled: Boolean): Result<Unit> =
        coRunCatching(Dispatchers.Default, onMapException = exceptionMapper) {
            deviceRestrictionOwnerComponentManager.setMicrophoneDisabled(isDisabled)
        }

    override suspend fun setUSBDataSignalDisabled(isDisabled: Boolean): Result<Unit> =
        coRunCatching(Dispatchers.Default, onMapException = exceptionMapper) {
            deviceRestrictionOwnerComponentManager.setUSBDataSignalDisabled(isDisabled)
        }

    override suspend fun setUSBFileTransferDisabled(isDisabled: Boolean): Result<Unit> =
        coRunCatching(Dispatchers.Default, onMapException = exceptionMapper) {
            deviceRestrictionOwnerComponentManager.setUSBFileTransferDisabled(isDisabled)
        }

    override suspend fun setInstallAppsDisabled(isDisabled: Boolean): Result<Unit> =
        coRunCatching(Dispatchers.Default, onMapException = exceptionMapper) {
            deviceRestrictionOwnerComponentManager.setInstallAppsDisabled(isDisabled)
        }

    override suspend fun setInstallAppsFromUnknownSourcesDisabled(isDisabled: Boolean): Result<Unit> =
        coRunCatching(Dispatchers.Default, onMapException = exceptionMapper) {
            deviceRestrictionOwnerComponentManager.setInstallAppsFromUnknownSourcesDisabled(isDisabled)
        }

    override suspend fun setUninstallAppsDisabled(isDisabled: Boolean): Result<Unit> =
        coRunCatching(Dispatchers.Default, onMapException = exceptionMapper) {
            deviceRestrictionOwnerComponentManager.setUninstallAppsDisabled(isDisabled)
        }

    override suspend fun setAppControlDisabled(isDisabled: Boolean): Result<Unit> =
        coRunCatching(Dispatchers.Default, onMapException = exceptionMapper) {
            deviceRestrictionOwnerComponentManager.setAppControlDisabled(isDisabled)
        }

    override suspend fun setScreenContentCaptureForAIDisabled(isDisabled: Boolean): Result<Unit> =
        coRunCatching(Dispatchers.Default, onMapException = exceptionMapper) {
            deviceRestrictionOwnerComponentManager.setScreenContentCaptureForAIDisabled(isDisabled)
        }

    override suspend fun setContentSuggestionDisabled(isDisabled: Boolean): Result<Unit> =
        coRunCatching(Dispatchers.Default, onMapException = exceptionMapper) {
            deviceRestrictionOwnerComponentManager.setContentSuggestionDisabled(isDisabled)
        }

    override suspend fun setScreenshotsDisabled(isDisabled: Boolean): Result<Unit> =
        coRunCatching(Dispatchers.Default, onMapException = exceptionMapper) {
            deviceRestrictionOwnerComponentManager.setScreenshotsDisabled(isDisabled)
        }

    override suspend fun setDebugFeaturesDisabled(isDisabled: Boolean): Result<Unit> =
        coRunCatching(Dispatchers.Default, onMapException = exceptionMapper) {
            deviceRestrictionOwnerComponentManager.setDebugFeaturesDisabled(isDisabled)
        }

    override suspend fun setFactoryResetDisabled(isDisabled: Boolean): Result<Unit> =
        coRunCatching(Dispatchers.Default, onMapException = exceptionMapper) {
            deviceRestrictionOwnerComponentManager.setFactoryResetDisabled(isDisabled)
        }

    override suspend fun setSafeBootDisabled(isDisabled: Boolean): Result<Unit> =
        coRunCatching(Dispatchers.Default, onMapException = exceptionMapper) {
            deviceRestrictionOwnerComponentManager.setSafeBootDisabled(isDisabled)
        }

    override suspend fun setAddUserDisabled(isDisabled: Boolean): Result<Unit> =
        coRunCatching(Dispatchers.Default, onMapException = exceptionMapper) {
            deviceRestrictionOwnerComponentManager.setAddUserDisabled(isDisabled)
        }

    override suspend fun setRemoveUserDisabled(isDisabled: Boolean): Result<Unit> =
        coRunCatching(Dispatchers.Default, onMapException = exceptionMapper) {
            deviceRestrictionOwnerComponentManager.setRemoveUserDisabled(isDisabled)
        }

    override suspend fun setSwitchUserDisabled(isDisabled: Boolean): Result<Unit> =
        coRunCatching(Dispatchers.Default, onMapException = exceptionMapper) {
            deviceRestrictionOwnerComponentManager.setSwitchUserDisabled(isDisabled)
        }

    override suspend fun setMountPhysicalMediaDisabled(isDisabled: Boolean): Result<Unit> =
        coRunCatching(Dispatchers.Default, onMapException = exceptionMapper) {
            deviceRestrictionOwnerComponentManager.setMountPhysicalMediaDisabled(isDisabled)
        }

    override suspend fun setBluetoothDisabled(isDisabled: Boolean): Result<Unit> =
        coRunCatching(Dispatchers.Default, onMapException = exceptionMapper) {
            deviceRestrictionOwnerComponentManager.setBluetoothDisabled(isDisabled)
        }

    override suspend fun setBluetoothConfigDisabled(isDisabled: Boolean): Result<Unit> =
        coRunCatching(Dispatchers.Default, onMapException = exceptionMapper) {
            deviceRestrictionOwnerComponentManager.setBluetoothConfigDisabled(isDisabled)
        }

    override suspend fun setNFCDisabled(isDisabled: Boolean): Result<Unit> =
        coRunCatching(Dispatchers.Default, onMapException = exceptionMapper) {
            deviceRestrictionOwnerComponentManager.setNFCDisabled(isDisabled)
        }

    override suspend fun setLocationDisabled(isDisabled: Boolean): Result<Unit> =
        coRunCatching(Dispatchers.Default, onMapException = exceptionMapper) {
            deviceRestrictionOwnerComponentManager.setLocationDisabled(isDisabled)
        }

    override suspend fun setOutgoingCallsDisabled(isDisabled: Boolean): Result<Unit> =
        coRunCatching(Dispatchers.Default, onMapException = exceptionMapper) {
            deviceRestrictionOwnerComponentManager.setOutgoingCallsDisabled(isDisabled)
        }

    override suspend fun setSMSDisabled(isDisabled: Boolean): Result<Unit> =
        coRunCatching(Dispatchers.Default, onMapException = exceptionMapper) {
            deviceRestrictionOwnerComponentManager.setSMSDisabled(isDisabled)
        }

    override suspend fun setWallpaperChangeDisabled(isDisabled: Boolean): Result<Unit> =
        coRunCatching(Dispatchers.Default, onMapException = exceptionMapper) {
            deviceRestrictionOwnerComponentManager.setWallpaperChangeDisabled(isDisabled)
        }

    override suspend fun setFunDisabled(isDisabled: Boolean): Result<Unit> =
        coRunCatching(Dispatchers.Default, onMapException = exceptionMapper) {
            deviceRestrictionOwnerComponentManager.setFunDisabled(isDisabled)
        }

    override suspend fun setWifiStateChangeDisabled(isDisabled: Boolean): Result<Unit> =
        coRunCatching(Dispatchers.Default, onMapException = exceptionMapper) {
            deviceRestrictionOwnerComponentManager.setWifiStateChangeDisabled(isDisabled)
        }

    override suspend fun setWifiConfigDisabled(isDisabled: Boolean): Result<Unit> =
        coRunCatching(Dispatchers.Default, onMapException = exceptionMapper) {
            deviceRestrictionOwnerComponentManager.setWifiConfigDisabled(isDisabled)
        }

    override suspend fun setRoamingDisabled(isDisabled: Boolean): Result<Unit> =
        coRunCatching(Dispatchers.Default, onMapException = exceptionMapper) {
            deviceRestrictionOwnerComponentManager.setRoamingDisabled(isDisabled)
        }

    override suspend fun setMobileDataDisabled(isDisabled: Boolean): Result<Unit> =
        coRunCatching(Dispatchers.Default, onMapException = exceptionMapper) {
            deviceRestrictionOwnerComponentManager.setConfigMobileDataDisabled(isDisabled)
        }

    override suspend fun setHotspotDisabled(isDisabled: Boolean): Result<Unit> =
        coRunCatching(Dispatchers.Default, onMapException = exceptionMapper) {
            deviceRestrictionOwnerComponentManager.setHotspotDisabled(isDisabled)
        }

    override suspend fun setConfigVPNDisabled(isDisabled: Boolean): Result<Unit> =
        coRunCatching(Dispatchers.Default, onMapException = exceptionMapper) {
            deviceRestrictionOwnerComponentManager.setConfigVPNDisabled(isDisabled)
        }

    override suspend fun setConfigPrivateDNSDisabled(isDisabled: Boolean): Result<Unit> =
        coRunCatching(Dispatchers.Default, onMapException = exceptionMapper) {
            deviceRestrictionOwnerComponentManager.setConfigPrivateDNSDisabled(isDisabled)
        }

    override suspend fun setAirplaneModeDisabled(isDisabled: Boolean): Result<Unit> =
        coRunCatching(Dispatchers.Default, onMapException = exceptionMapper) {
            deviceRestrictionOwnerComponentManager.setAirplaneModeDisabled(isDisabled)
        }

    override suspend fun setAddNewWifiNetworksDisabled(isDisabled: Boolean): Result<Unit> =
        coRunCatching(Dispatchers.Default, onMapException = exceptionMapper) {
            deviceRestrictionOwnerComponentManager.setAddNewWifiNetworksDisabled(isDisabled)
        }

    override suspend fun set2GNetworkDisabled(isDisabled: Boolean): Result<Unit> =
        coRunCatching(Dispatchers.Default, onMapException = exceptionMapper) {
            deviceRestrictionOwnerComponentManager.set2GNetworkDisabled(isDisabled)
        }
}