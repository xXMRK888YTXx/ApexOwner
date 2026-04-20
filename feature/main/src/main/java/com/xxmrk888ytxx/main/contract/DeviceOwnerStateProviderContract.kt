package com.xxmrk888ytxx.main.contract

import kotlinx.coroutines.flow.Flow

interface DeviceOwnerStateProviderContract {
    val isDeviceOwner: Flow<Boolean>
}