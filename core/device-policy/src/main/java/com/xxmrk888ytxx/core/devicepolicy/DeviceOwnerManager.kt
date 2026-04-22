package com.xxmrk888ytxx.core.devicepolicy

import kotlinx.coroutines.flow.Flow

interface DeviceOwnerManager {
    val isDeviceOwner: Flow<Boolean>
    val isCameraDisabled: Flow<Boolean>
    val isMicrophoneDisabled: Flow<Boolean>
    val isUSBDataSignalDisabled: Flow<Boolean>
    val isUSBFileTransferDisabled: Flow<Boolean>
    val isInstallAppsDisabled: Flow<Boolean>
    val isInstallAppsFromUnknownSourcesDisabled: Flow<Boolean>
    val isUninstallAppsDisabled: Flow<Boolean>
    val isAppControlDisabled: Flow<Boolean>
    val isScreenContentCaptureForAIDisabled: Flow<Boolean>
    val isCanDisableUSBDataSignal: Boolean
    val isCanDisableScreenContentCaptureForAI: Boolean
    suspend fun setCameraDisabled(isDisabled: Boolean)
    suspend fun setMicrophoneDisabled(isDisabled: Boolean)
    suspend fun setUSBDataSignalDisabled(isDisabled: Boolean)
    suspend fun setUSBFileTransferDisabled(isDisabled: Boolean)
    suspend fun setInstallAppsDisabled(isDisabled: Boolean)
    suspend fun setInstallAppsFromUnknownSourcesDisabled(isDisabled: Boolean)
    suspend fun setUninstallAppsDisabled(isDisabled: Boolean)
    suspend fun setAppControlDisabled(isDisabled: Boolean)
    suspend fun setScreenContentCaptureForAIDisabled(isDisabled: Boolean)
    suspend fun updateDeviceOwnerState()
}