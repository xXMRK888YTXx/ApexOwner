package com.xxmrk888ytxx.apexowner.featureContract.main

import com.xxmrk888ytxx.core.devicepolicy.DeviceOwnerManager
import com.xxmrk888ytxx.feature.main.contract.DeviceOwnerStateProviderContract
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DeviceOwnerStateProviderContractImpl @Inject constructor(
    deviceOwnerManager: DeviceOwnerManager
) : DeviceOwnerStateProviderContract {
    override val isDeviceOwner: Flow<Boolean> = deviceOwnerManager.isDeviceOwner
}