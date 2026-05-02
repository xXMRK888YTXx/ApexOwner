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
    override val isLocationDisabled: Flow<Boolean> = deviceRestrictionOwnerComponentManager.isLocationDisabled
    override val isConfigVPNDisabled: Flow<Boolean> = deviceRestrictionOwnerComponentManager.isConfigVPNDisabled

    override val isCanDisableScreenContentCaptureForAI: Boolean
        get() = deviceRestrictionOwnerComponentManager.isCanDisableScreenContentCaptureForAI
    override val isCanDisableContentSuggestion: Boolean
        get() = deviceRestrictionOwnerComponentManager.isCanDisableContentSuggestion

    private val exceptionMapper: (Throwable) -> Throwable = {
        when (it) {
            is com.xxmrk888ytxx.core.devicepolicy.exception.AppNotProfileOwnerException -> AppNotDeviceOwnerException()
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

    override suspend fun setLocationDisabled(isDisabled: Boolean): Result<Unit> =
        coRunCatching(Dispatchers.Default, onMapException = exceptionMapper) {
            deviceRestrictionOwnerComponentManager.setLocationDisabled(isDisabled)
        }

    override suspend fun setConfigVPNDisabled(isDisabled: Boolean): Result<Unit> = coRunCatching(Dispatchers.Default, onMapException = exceptionMapper) {
        deviceRestrictionOwnerComponentManager.setConfigVPNDisabled(isDisabled)
    }
}