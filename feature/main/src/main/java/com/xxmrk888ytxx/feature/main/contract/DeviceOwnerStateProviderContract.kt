package com.xxmrk888ytxx.feature.main.contract

import com.xxmrk888ytxx.feature.main.model.MainOperatingMode
import kotlinx.coroutines.flow.Flow

interface DeviceOwnerStateProviderContract {
    val isDeviceOwner: Flow<Boolean>
    val operatingMode: Flow<MainOperatingMode>
    val hasWorkProfile: Flow<Boolean>
    val isInsideWorkProfile: Flow<Boolean>
    val isWorkProfileQuietMode: Flow<Boolean>
    suspend fun setWorkProfileQuietMode(isQuietMode: Boolean): Result<Unit>
}
