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
    override val isCanDisableUSBDataSignal: Boolean
        get() = deviceOwnerManager.isCanDisableUSBDataSignal

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
}