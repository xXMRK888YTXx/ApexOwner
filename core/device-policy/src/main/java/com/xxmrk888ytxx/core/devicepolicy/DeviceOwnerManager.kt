package com.xxmrk888ytxx.core.devicepolicy

interface DeviceOwnerManager : DeviceRestrictionManager {
    suspend fun updateDeviceOwnerState()
}