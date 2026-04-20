package com.xxmrk888ytxx.feature.managementmodule.apprestriction.contract

import kotlinx.coroutines.flow.Flow

interface RestrictionManageContract {
    val isCameraDisabled: Flow<Boolean>
    val isMicrophoneDisabled: Flow<Boolean>
    suspend fun setCameraDisabled(isDisabled: Boolean) : Result<Unit>
    suspend fun setMicrophoneDisabled(isDisabled: Boolean): Result<Unit>
}