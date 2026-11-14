package com.xxmrk888ytxx.apexowner.featureContract.main

import com.xxmrk888ytxx.core.devicepolicy.DeviceOwnerManager
import com.xxmrk888ytxx.core.devicepolicy.model.PolicyOperatingMode
import com.xxmrk888ytxx.feature.main.contract.DeviceOwnerStateProviderContract
import com.xxmrk888ytxx.feature.main.model.MainOperatingMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DeviceOwnerStateProviderContractImpl @Inject constructor(
    private val deviceOwnerManager: DeviceOwnerManager
) : DeviceOwnerStateProviderContract {

    override val isDeviceOwner: Flow<Boolean> = deviceOwnerManager.isDeviceOwner

    override val operatingMode: Flow<MainOperatingMode> = deviceOwnerManager.policyState.map { state ->
        when (state.operatingMode) {
            PolicyOperatingMode.PERSONAL_HOST -> MainOperatingMode.PERSONAL_HOST
            PolicyOperatingMode.PROFILE_OWNER -> MainOperatingMode.PROFILE_OWNER
            PolicyOperatingMode.DEVICE_OWNER -> MainOperatingMode.DEVICE_OWNER
        }
    }

    override val hasWorkProfile: Flow<Boolean> = deviceOwnerManager.hasWorkProfile

    override val isWorkProfileQuietMode: Flow<Boolean> = deviceOwnerManager.isWorkProfileQuietMode

    override suspend fun setWorkProfileQuietMode(isQuietMode: Boolean): Result<Unit> =
        deviceOwnerManager.setWorkProfileQuietMode(isQuietMode)
}
