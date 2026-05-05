package com.xxmrk888ytxx.feature.main.contract

import kotlinx.coroutines.flow.Flow

interface DeviceOwnerStateProviderContract {
    val isDeviceOwner: Flow<Boolean>
}