package com.xxmrk888ytxx.core.devicepolicy

import kotlinx.coroutines.flow.Flow

interface DeviceOwnerManager : DeviceRestrictionOwnerComponentManager, WorkProfileOwnerComponentManager {
    val isDeviceOwner: Flow<Boolean>
    suspend fun updateDeviceOwnerState()
}