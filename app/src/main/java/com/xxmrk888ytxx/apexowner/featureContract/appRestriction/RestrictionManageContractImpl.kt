package com.xxmrk888ytxx.apexowner.featureContract.appRestriction

import com.xxmrk888ytxx.core.base.android.extension.coRunCatching
import com.xxmrk888ytxx.core.devicepolicy.DeviceRestrictionManager
import com.xxmrk888ytxx.feature.managementmodule.apprestriction.contract.RestrictionManageContract
import com.xxmrk888ytxx.feature.managementmodule.apprestriction.exception.AppNotDeviceOwnerException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RestrictionManageContractImpl @Inject constructor(
    private val deviceRestrictionManager: DeviceRestrictionManager
) : RestrictionManageContract {
    override val isCameraDisabled: Flow<Boolean> = deviceRestrictionManager.isCameraDisabled
    override val isMicrophoneDisabled: Flow<Boolean> = deviceRestrictionManager.isMicrophoneDisabled
    override val isUSBDataSignalDisabled: Flow<Boolean> = deviceRestrictionManager.isUSBDataSignalDisabled
    override val isUSBFileTransferDisabled: Flow<Boolean> = deviceRestrictionManager.isUSBFileTransferDisabled
    override val isInstallAppsDisabled: Flow<Boolean> = deviceRestrictionManager.isInstallAppsDisabled
    override val isInstallAppsFromUnknownSourcesDisabled: Flow<Boolean> = deviceRestrictionManager.isInstallAppsFromUnknownSourcesDisabled
    override val isUninstallAppsDisabled: Flow<Boolean> = deviceRestrictionManager.isUninstallAppsDisabled
    override val isAppControlDisabled: Flow<Boolean> = deviceRestrictionManager.isAppControlDisabled
    override val isScreenContentCaptureForAIDisabled: Flow<Boolean> = deviceRestrictionManager.isScreenContentCaptureForAIDisabled
    override val isContentSuggestionDisabled: Flow<Boolean> = deviceRestrictionManager.isContentSuggestionDisabled
    override val isScreenshotsDisabled: Flow<Boolean> = deviceRestrictionManager.isScreenshotsDisabled
    override val isDebugFeaturesDisabled: Flow<Boolean> = deviceRestrictionManager.isDebugFeaturesDisabled
    override val isFactoryResetDisabled: Flow<Boolean> = deviceRestrictionManager.isFactoryResetDisabled
    override val isSafeBootDisabled: Flow<Boolean> = deviceRestrictionManager.isSafeBootDisabled
    override val isAddUserDisabled: Flow<Boolean> = deviceRestrictionManager.isAddUserDisabled
    override val isRemoveUserDisabled: Flow<Boolean> = deviceRestrictionManager.isRemoveUserDisabled
    override val isSwitchUserDisabled: Flow<Boolean> = deviceRestrictionManager.isSwitchUserDisabled
    override val isMountPhysicalMediaDisabled: Flow<Boolean> = deviceRestrictionManager.isMountPhysicalMediaDisabled
    override val isBluetoothDisabled: Flow<Boolean> = deviceRestrictionManager.isBluetoothDisabled
    override val isBluetoothConfigDisabled: Flow<Boolean> = deviceRestrictionManager.isBluetoothConfigDisabled
    override val isNFCDisabled: Flow<Boolean> = deviceRestrictionManager.isNFCDisabled
    override val isLocationDisabled: Flow<Boolean> = deviceRestrictionManager.isLocationDisabled
    override val isOutgoingCallsDisabled: Flow<Boolean> = deviceRestrictionManager.isOutgoingCallsDisabled
    override val isSMSDisabled: Flow<Boolean> = deviceRestrictionManager.isSMSDisabled
    override val isWallpaperChangeDisabled: Flow<Boolean> = deviceRestrictionManager.isWallpaperChangeDisabled
    override val isFunDisabled: Flow<Boolean> = deviceRestrictionManager.isFunDisabled


    override val isCanDisableUSBDataSignal: Boolean
        get() = deviceRestrictionManager.isCanDisableUSBDataSignal
    override val isCanDisableScreenContentCaptureForAI: Boolean
        get() = deviceRestrictionManager.isCanDisableScreenContentCaptureForAI
    override val isCanDisableContentSuggestion: Boolean
        get() = deviceRestrictionManager.isCanDisableContentSuggestion
    override val isCanDisableNFC: Boolean
        get() = deviceRestrictionManager.isCanDisableNFC

    private val exceptionMapper: (Throwable) -> Throwable = {
        when (it) {
            is com.xxmrk888ytxx.core.devicepolicy.exception.AppNotDeviceOwnerException -> AppNotDeviceOwnerException()
            else -> it
        }
    }

    override suspend fun setCameraDisabled(isDisabled: Boolean): Result<Unit> =
        coRunCatching(Dispatchers.Default, onMapException = exceptionMapper) {
            deviceRestrictionManager.setCameraDisabled(isDisabled)
        }

    override suspend fun setMicrophoneDisabled(isDisabled: Boolean): Result<Unit> =
        coRunCatching(Dispatchers.Default, onMapException = exceptionMapper) {
            deviceRestrictionManager.setMicrophoneDisabled(isDisabled)
        }

    override suspend fun setUSBDataSignalDisabled(isDisabled: Boolean): Result<Unit> = coRunCatching(Dispatchers.Default, onMapException = exceptionMapper) {
        deviceRestrictionManager.setUSBDataSignalDisabled(isDisabled)
    }

    override suspend fun setUSBFileTransferDisabled(isDisabled: Boolean): Result<Unit> = coRunCatching(Dispatchers.Default, onMapException = exceptionMapper) {
        deviceRestrictionManager.setUSBFileTransferDisabled(isDisabled)
    }

    override suspend fun setInstallAppsDisabled(isDisabled: Boolean): Result<Unit> = coRunCatching(Dispatchers.Default, onMapException = exceptionMapper) {
        deviceRestrictionManager.setInstallAppsDisabled(isDisabled)
    }

    override suspend fun setInstallAppsFromUnknownSourcesDisabled(isDisabled: Boolean): Result<Unit> = coRunCatching(Dispatchers.Default, onMapException = exceptionMapper) {
        deviceRestrictionManager.setInstallAppsFromUnknownSourcesDisabled(isDisabled)
    }

    override suspend fun setUninstallAppsDisabled(isDisabled: Boolean): Result<Unit> = coRunCatching(Dispatchers.Default, onMapException = exceptionMapper) {
        deviceRestrictionManager.setUninstallAppsDisabled(isDisabled)
    }

    override suspend fun setAppControlDisabled(isDisabled: Boolean): Result<Unit> = coRunCatching(Dispatchers.Default, onMapException = exceptionMapper) {
        deviceRestrictionManager.setAppControlDisabled(isDisabled)
    }

    override suspend fun setScreenContentCaptureForAIDisabled(isDisabled: Boolean): Result<Unit> = coRunCatching(Dispatchers.Default, onMapException = exceptionMapper) {
        deviceRestrictionManager.setScreenContentCaptureForAIDisabled(isDisabled)
    }

    override suspend fun setContentSuggestionDisabled(isDisabled: Boolean): Result<Unit> = coRunCatching(Dispatchers.Default, onMapException = exceptionMapper) {
        deviceRestrictionManager.setContentSuggestionDisabled(isDisabled)
    }

    override suspend fun setScreenshotsDisabled(isDisabled: Boolean): Result<Unit> = coRunCatching(Dispatchers.Default, onMapException = exceptionMapper) {
        deviceRestrictionManager.setScreenshotsDisabled(isDisabled)
    }

    override suspend fun setDebugFeaturesDisabled(isDisabled: Boolean): Result<Unit> = coRunCatching(Dispatchers.Default, onMapException = exceptionMapper) {
        deviceRestrictionManager.setDebugFeaturesDisabled(isDisabled)
    }

    override suspend fun setFactoryResetDisabled(isDisabled: Boolean): Result<Unit> =
        coRunCatching(Dispatchers.Default, onMapException = exceptionMapper) {
            deviceRestrictionManager.setFactoryResetDisabled(isDisabled)
        }

    override suspend fun setSafeBootDisabled(isDisabled: Boolean): Result<Unit> = coRunCatching(Dispatchers.Default, onMapException = exceptionMapper)  {
        deviceRestrictionManager.setSafeBootDisabled(isDisabled)
    }

    override suspend fun setAddUserDisabled(isDisabled: Boolean): Result<Unit> = coRunCatching(Dispatchers.Default, onMapException = exceptionMapper) {
        deviceRestrictionManager.setAddUserDisabled(isDisabled)
    }

    override suspend fun setRemoveUserDisabled(isDisabled: Boolean): Result<Unit> = coRunCatching(Dispatchers.Default, onMapException = exceptionMapper) {
        deviceRestrictionManager.setRemoveUserDisabled(isDisabled)
    }

    override suspend fun setSwitchUserDisabled(isDisabled: Boolean): Result<Unit> = coRunCatching(Dispatchers.Default, onMapException = exceptionMapper) {
        deviceRestrictionManager.setSwitchUserDisabled(isDisabled)
    }

    override suspend fun setMountPhysicalMediaDisabled(isDisabled: Boolean): Result<Unit> = coRunCatching(Dispatchers.Default, onMapException = exceptionMapper) {
        deviceRestrictionManager.setMountPhysicalMediaDisabled(isDisabled)
    }

    override suspend fun setBluetoothDisabled(isDisabled: Boolean): Result<Unit> = coRunCatching(Dispatchers.Default, onMapException = exceptionMapper) {
        deviceRestrictionManager.setBluetoothDisabled(isDisabled)
    }

    override suspend fun setBluetoothConfigDisabled(isDisabled: Boolean): Result<Unit> = coRunCatching(Dispatchers.Default, onMapException = exceptionMapper) {
        deviceRestrictionManager.setBluetoothConfigDisabled(isDisabled)
    }

    override suspend fun setNFCDisabled(isDisabled: Boolean): Result<Unit> = coRunCatching(Dispatchers.Default, onMapException = exceptionMapper) {
        deviceRestrictionManager.setNFCDisabled(isDisabled)
    }

    override suspend fun setLocationDisabled(isDisabled: Boolean): Result<Unit> = coRunCatching(Dispatchers.Default, onMapException = exceptionMapper) {
        deviceRestrictionManager.setLocationDisabled(isDisabled)
    }

    override suspend fun setOutgoingCallsDisabled(isDisabled: Boolean): Result<Unit> = coRunCatching(Dispatchers.Default, onMapException = exceptionMapper) {
        deviceRestrictionManager.setOutgoingCallsDisabled(isDisabled)
    }

    override suspend fun setSMSDisabled(isDisabled: Boolean): Result<Unit> = coRunCatching(Dispatchers.Default, onMapException = exceptionMapper) {
        deviceRestrictionManager.setSMSDisabled(isDisabled)
    }

    override suspend fun setWallpaperChangeDisabled(isDisabled: Boolean): Result<Unit> = coRunCatching(Dispatchers.Default, onMapException = exceptionMapper) {
        deviceRestrictionManager.setWallpaperChangeDisabled(isDisabled)
    }

    override suspend fun setFunDisabled(isDisabled: Boolean): Result<Unit> = coRunCatching(Dispatchers.Default, onMapException = exceptionMapper) {
        deviceRestrictionManager.setFunDisabled(isDisabled)
    }
}