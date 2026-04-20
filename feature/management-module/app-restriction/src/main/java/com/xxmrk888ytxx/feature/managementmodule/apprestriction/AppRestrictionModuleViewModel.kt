package com.xxmrk888ytxx.feature.managementmodule.apprestriction

import androidx.lifecycle.viewModelScope
import com.xxmrk888ytxx.core.base.android.uiText.uiText
import com.xxmrk888ytxx.core.base.android.viewModel.ApexOwnerViewModel
import com.xxmrk888ytxx.feature.managementmodule.apprestriction.contract.RestrictionManageContract
import com.xxmrk888ytxx.feature.managementmodule.apprestriction.exception.AppNotDeviceOwnerException
import com.xxmrk888ytxx.feature.managementmodule.apprestriction.model.AppRestrictionModuleUiEvent
import com.xxmrk888ytxx.feature.managementmodule.apprestriction.model.ScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppRestrictionModuleViewModel @Inject constructor(
    private val restrictionManageContract: RestrictionManageContract
) : ApexOwnerViewModel<ScreenState, AppRestrictionModuleUiEvent>(
    ScreenState()
) {
    override val state: StateFlow<ScreenState> = combine(
        restrictionManageContract.isCameraDisabled,
        restrictionManageContract.isMicrophoneDisabled,
        restrictionManageContract.isUSBDataSignalDisabled
    ) { flowArray ->
        val isCameraDisabled = flowArray[0]
        val isMicrophoneDisabled = flowArray[1]
        val isUSBDataSignalDisabled = flowArray[2]

        ScreenState(
            isCameraDisabled = isCameraDisabled,
            isMicrophoneDisabled = isMicrophoneDisabled,
            isCanDisableUSBDataSignal = restrictionManageContract.isCanDisableUSBDataSignal,
            isUSBDataSignalDisabled = isUSBDataSignalDisabled
        )
    }.stateWhileSubscribed()


    override fun onEvent(event: AppRestrictionModuleUiEvent) {
        when (event) {
            AppRestrictionModuleUiEvent.ToggleCameraDisabled -> toggleCameraDisabled()
            AppRestrictionModuleUiEvent.ToggleMicrophoneDisabled -> toggleMicrophoneDisabled()
            AppRestrictionModuleUiEvent.ToggleUSBDataSignalDisable -> toggleUSBDataSignalDisable()
        }
    }

    private fun toggleUSBDataSignalDisable() = changeRestrictionState {
        val isUSBDataSignalDisabled = state.value.isUSBDataSignalDisabled
        restrictionManageContract.setUSBDataSignalDisabled(!isUSBDataSignalDisabled)
    }

    private fun toggleMicrophoneDisabled() = changeRestrictionState {
        val isMicrophoneDisabled = state.value.isMicrophoneDisabled
        restrictionManageContract.setMicrophoneDisabled(!isMicrophoneDisabled)
    }

    private fun toggleCameraDisabled() = changeRestrictionState {
        val isCameraDisabled = state.value.isCameraDisabled
        restrictionManageContract.setCameraDisabled(!isCameraDisabled)
    }

    private fun changeRestrictionState(
        block: suspend () -> Result<Unit>
    ) = viewModelScope.launch {
        block()
            .onFailure {
                when (it) {
                    is AppNotDeviceOwnerException -> sendToastSideEffect(R.string.app_not_device_owner.uiText())
                    else -> sendToastSideEffect(R.string.an_error_has_occurred.uiText())
                }
            }
    }
}