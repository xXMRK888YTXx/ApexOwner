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
    val isCanDisableUSBDataSignal: Boolean
    suspend fun setCameraDisabled(isDisabled: Boolean)
    suspend fun setMicrophoneDisabled(isDisabled: Boolean)
    suspend fun setUSBDataSignalDisabled(isDisabled: Boolean)
    suspend fun setUSBFileTransferDisabled(isDisabled: Boolean)
    suspend fun setInstallAppsDisabled(isDisabled: Boolean)
    suspend fun setInstallAppsFromUnknownSourcesDisabled(isDisabled: Boolean)
    suspend fun updateDeviceOwnerState()
}