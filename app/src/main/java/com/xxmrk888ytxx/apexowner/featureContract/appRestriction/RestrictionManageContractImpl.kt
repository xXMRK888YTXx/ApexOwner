package com.xxmrk888ytxx.apexowner.featureContract.appRestriction

import com.xxmrk888ytxx.core.base.android.extension.coRunCatching
import com.xxmrk888ytxx.core.devicepolicy.DeviceOwnerManager
import com.xxmrk888ytxx.feature.managementmodule.apprestriction.contract.RestrictionManageContract
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class RestrictionManageContractImpl @Inject constructor(
    private val deviceOwnerManager: DeviceOwnerManager
) : RestrictionManageContract {
    override val isCameraDisabled: Flow<Boolean> = deviceOwnerManager.isCameraDisabled
    override val isMicrophoneDisabled: Flow<Boolean> = deviceOwnerManager.isMicrophoneDisabled

    override suspend fun setCameraDisabled(isDisabled: Boolean): Result<Unit> = coRunCatching(Dispatchers.Default) {
        deviceOwnerManager.setCameraDisabled(isDisabled)
    }

    override suspend fun setMicrophoneDisabled(isDisabled: Boolean): Result<Unit> = coRunCatching(Dispatchers.Default) {
        deviceOwnerManager.setMicrophoneDisabled(isDisabled)
    }
}