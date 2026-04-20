package com.xxmrk888ytxx.apexowner.featureContract.main

import com.xxmrk888ytxx.main.contract.DeviceOwnerStateProviderContract
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class DeviceOwnerStateProviderContractImpl @Inject constructor(

) : DeviceOwnerStateProviderContract {
    //TODO implement this
    override val isDeviceOwner: Flow<Boolean> = flowOf(false)
}