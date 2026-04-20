package com.xxmrk888ytxx.core.devicepolicy

import kotlinx.coroutines.flow.Flow

interface DeviceOwnerManager {
    val isDeviceOwner: Flow<Boolean>
    val isCameraDisabled: Flow<Boolean>
    suspend fun updateDeviceOwnerState()
    suspend fun setCameraDisabled(isDisabled: Boolean)
}