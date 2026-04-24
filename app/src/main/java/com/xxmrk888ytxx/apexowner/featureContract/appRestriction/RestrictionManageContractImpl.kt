package com.xxmrk888ytxx.apexowner.featureContract.appRestriction

import com.xxmrk888ytxx.core.base.android.extension.coRunCatching
import com.xxmrk888ytxx.core.devicepolicy.DeviceOwnerManager
import com.xxmrk888ytxx.feature.managementmodule.apprestriction.contract.RestrictionManageContract
import com.xxmrk888ytxx.feature.managementmodule.apprestriction.exception.AppNotDeviceOwnerException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RestrictionManageContractImpl @Inject constructor(
    private val deviceOwnerManager: DeviceOwnerManager
) : RestrictionManageContract {
    override val isCameraDisabled: Flow<Boolean> = deviceOwnerManager.isCameraDisabled
    override val isMicrophoneDisabled: Flow<Boolean> = deviceOwnerManager.isMicrophoneDisabled
    override val isUSBDataSignalDisabled: Flow<Boolean> = deviceOwnerManager.isUSBDataSignalDisabled
    override val isUSBFileTransferDisabled: Flow<Boolean> = deviceOwnerManager.isUSBFileTransferDisabled
    override val isInstallAppsDisabled: Flow<Boolean> = deviceOwnerManager.isInstallAppsDisabled
    override val isInstallAppsFromUnknownSourcesDisabled: Flow<Boolean> = deviceOwnerManager.isInstallAppsFromUnknownSourcesDisabled
    override val isUninstallAppsDisabled: Flow<Boolean> = deviceOwnerManager.isUninstallAppsDisabled
    override val isAppControlDisabled: Flow<Boolean> = deviceOwnerManager.isAppControlDisabled
    override val isScreenContentCaptureForAIDisabled: Flow<Boolean> = deviceOwnerManager.isScreenContentCaptureForAIDisabled
    override val isContentSuggestionDisabled: Flow<Boolean> = deviceOwnerManager.isContentSuggestionDisabled
    override val isScreenshotsDisabled: Flow<Boolean> = deviceOwnerManager.isScreenshotsDisabled
    override val isDebugFeaturesDisabled: Flow<Boolean> = deviceOwnerManager.isDebugFeaturesDisabled
    override val isFactoryResetDisabled: Flow<Boolean> = deviceOwnerManager.isFactoryResetDisabled
    override val isSafeBootDisabled: Flow<Boolean> = deviceOwnerManager.isSafeBootDisabled
    override val isAddUserDisabled: Flow<Boolean> = deviceOwnerManager.isAddUserDisabled
    override val isRemoveUserDisabled: Flow<Boolean> = deviceOwnerManager.isRemoveUserDisabled
    override val isSwitchUserDisabled: Flow<Boolean> = deviceOwnerManager.isSwitchUserDisabled
    override val isMountPhysicalMediaDisabled: Flow<Boolean> = deviceOwnerManager.isMountPhysicalMediaDisabled
    override val isBluetoothDisabled: Flow<Boolean> = deviceOwnerManager.isBluetoothDisabled
    override val isBluetoothConfigDisabled: Flow<Boolean> = deviceOwnerManager.isBluetoothConfigDisabled


    override val isCanDisableUSBDataSignal: Boolean
        get() = deviceOwnerManager.isCanDisableUSBDataSignal
    override val isCanDisableScreenContentCaptureForAI: Boolean
        get() = deviceOwnerManager.isCanDisableScreenContentCaptureForAI
    override val isCanDisableContentSuggestion: Boolean
        get() = deviceOwnerManager.isCanDisableContentSuggestion

    private val exceptionMapper: (Throwable) -> Throwable = {
        when (it) {
            is com.xxmrk888ytxx.core.devicepolicy.exception.AppNotDeviceOwnerException -> AppNotDeviceOwnerException()
            else -> it
        }
    }

    override suspend fun setCameraDisabled(isDisabled: Boolean): Result<Unit> =
        coRunCatching(Dispatchers.Default, onMapException = exceptionMapper) {
            deviceOwnerManager.setCameraDisabled(isDisabled)
        }

    override suspend fun setMicrophoneDisabled(isDisabled: Boolean): Result<Unit> =
        coRunCatching(Dispatchers.Default, onMapException = exceptionMapper) {
            deviceOwnerManager.setMicrophoneDisabled(isDisabled)
        }

    override suspend fun setUSBDataSignalDisabled(isDisabled: Boolean): Result<Unit> = coRunCatching(Dispatchers.Default, onMapException = exceptionMapper) {
        deviceOwnerManager.setUSBDataSignalDisabled(isDisabled)
    }

    override suspend fun setUSBFileTransferDisabled(isDisabled: Boolean): Result<Unit> = coRunCatching(Dispatchers.Default, onMapException = exceptionMapper) {
        deviceOwnerManager.setUSBFileTransferDisabled(isDisabled)
    }

    override suspend fun setInstallAppsDisabled(isDisabled: Boolean): Result<Unit> = coRunCatching(Dispatchers.Default, onMapException = exceptionMapper) {
        deviceOwnerManager.setInstallAppsDisabled(isDisabled)
    }

    override suspend fun setInstallAppsFromUnknownSourcesDisabled(isDisabled: Boolean): Result<Unit> = coRunCatching(Dispatchers.Default, onMapException = exceptionMapper) {
        deviceOwnerManager.setInstallAppsFromUnknownSourcesDisabled(isDisabled)
    }

    override suspend fun setUninstallAppsDisabled(isDisabled: Boolean): Result<Unit> = coRunCatching(Dispatchers.Default, onMapException = exceptionMapper) {
        deviceOwnerManager.setUninstallAppsDisabled(isDisabled)
    }

    override suspend fun setAppControlDisabled(isDisabled: Boolean): Result<Unit> = coRunCatching(Dispatchers.Default, onMapException = exceptionMapper) {
        deviceOwnerManager.setAppControlDisabled(isDisabled)
    }

    override suspend fun setScreenContentCaptureForAIDisabled(isDisabled: Boolean): Result<Unit> = coRunCatching(Dispatchers.Default, onMapException = exceptionMapper) {
        deviceOwnerManager.setScreenContentCaptureForAIDisabled(isDisabled)
    }

    override suspend fun setContentSuggestionDisabled(isDisabled: Boolean): Result<Unit> = coRunCatching(Dispatchers.Default, onMapException = exceptionMapper) {
        deviceOwnerManager.setContentSuggestionDisabled(isDisabled)
    }

    override suspend fun setScreenshotsDisabled(isDisabled: Boolean): Result<Unit> = coRunCatching(Dispatchers.Default, onMapException = exceptionMapper) {
        deviceOwnerManager.setScreenshotsDisabled(isDisabled)
    }

    override suspend fun setDebugFeaturesDisabled(isDisabled: Boolean): Result<Unit> = coRunCatching(Dispatchers.Default, onMapException = exceptionMapper) {
        deviceOwnerManager.setDebugFeaturesDisabled(isDisabled)
    }

    override suspend fun setFactoryResetDisabled(isDisabled: Boolean): Result<Unit> =
        coRunCatching(Dispatchers.Default, onMapException = exceptionMapper) {
            deviceOwnerManager.setFactoryResetDisabled(isDisabled)
        }

    override suspend fun setSafeBootDisabled(isDisabled: Boolean): Result<Unit> = coRunCatching(Dispatchers.Default, onMapException = exceptionMapper)  {
        deviceOwnerManager.setSafeBootDisabled(isDisabled)
    }

    override suspend fun setAddUserDisabled(isDisabled: Boolean): Result<Unit> = coRunCatching(Dispatchers.Default, onMapException = exceptionMapper) {
        deviceOwnerManager.setAddUserDisabled(isDisabled)
    }

    override suspend fun setRemoveUserDisabled(isDisabled: Boolean): Result<Unit> = coRunCatching(Dispatchers.Default, onMapException = exceptionMapper) {
        deviceOwnerManager.setRemoveUserDisabled(isDisabled)
    }

    override suspend fun setSwitchUserDisabled(isDisabled: Boolean): Result<Unit> = coRunCatching(Dispatchers.Default, onMapException = exceptionMapper) {
        deviceOwnerManager.setSwitchUserDisabled(isDisabled)
    }

    override suspend fun setMountPhysicalMediaDisabled(isDisabled: Boolean): Result<Unit> = coRunCatching(Dispatchers.Default, onMapException = exceptionMapper) {
        deviceOwnerManager.setMountPhysicalMediaDisabled(isDisabled)
    }

    override suspend fun setBluetoothDisabled(isDisabled: Boolean): Result<Unit> = coRunCatching(Dispatchers.Default, onMapException = exceptionMapper) {
        deviceOwnerManager.setBluetoothDisabled(isDisabled)
    }

    override suspend fun setBluetoothConfigDisabled(isDisabled: Boolean): Result<Unit> = coRunCatching(Dispatchers.Default, onMapException = exceptionMapper) {
        deviceOwnerManager.setBluetoothConfigDisabled(isDisabled)
    }
}