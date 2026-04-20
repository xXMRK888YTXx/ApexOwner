package com.xxmrk888ytxx.core.devicepolicy

import kotlinx.coroutines.flow.Flow

interface DeviceOwnerManager {
    val isDeviceOwner: Flow<Boolean>
    suspend fun updateDeviceOwnerState()
}