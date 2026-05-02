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
        restrictionManageContract.isInstallAppsDisabled,
        restrictionManageContract.isInstallAppsFromUnknownSourcesDisabled,
        restrictionManageContract.isUninstallAppsDisabled,
        restrictionManageContract.isAppControlDisabled,
        restrictionManageContract.isScreenContentCaptureForAIDisabled,
        restrictionManageContract.isContentSuggestionDisabled,
        restrictionManageContract.isScreenshotsDisabled,
        restrictionManageContract.isDebugFeaturesDisabled,
        restrictionManageContract.isLocationDisabled,
        restrictionManageContract.isConfigVPNDisabled
    ) { flowArray ->
        val isCameraDisabled = flowArray[0]
        val isMicrophoneDisabled = flowArray[1]
        val isInstallAppsDisabled = flowArray[2]
        val isInstallAppsFromUnknownSourcesDisabled = flowArray[3]
        val isUninstallAppsDisabled = flowArray[4]
        val isAppControlDisabled = flowArray[5]
        val isScreenContentCaptureForAIDisabled = flowArray[6]
        val isContentSuggestionDisabled = flowArray[7]
        val isScreenshotsDisabled = flowArray[8]
        val isDebugFeaturesDisabled = flowArray[9]
        val isLocationDisabled = flowArray[10]
        val isConfigVPNDisabled = flowArray[11]




        ScreenState(
            isCameraDisabled = isCameraDisabled,
            isMicrophoneDisabled = isMicrophoneDisabled,
            isInstallAppsDisabled = isInstallAppsDisabled,
            isInstallAppsFromUnknownSourcesDisabled = isInstallAppsFromUnknownSourcesDisabled,
            isUninstallAppsDisabled = isUninstallAppsDisabled,
            isAppControlDisabled = isAppControlDisabled,
            isCanDisableScreenContentCaptureForAI = restrictionManageContract.isCanDisableScreenContentCaptureForAI,
            isScreenContentCaptureForAIDisabled = isScreenContentCaptureForAIDisabled,
            isContentSuggestionDisabled = isContentSuggestionDisabled,
            isCanDisableContentSuggestion = restrictionManageContract.isCanDisableContentSuggestion,
            isScreenshotsDisabled = isScreenshotsDisabled,
            isLocationDisabled = isLocationDisabled,
            isDebugFeaturesDisabled = isDebugFeaturesDisabled,
            isConfigVPNDisabled = isConfigVPNDisabled,
        )
    }.stateWhileSubscribed()


    override fun onEvent(event: AppRestrictionModuleUiEvent) {
        when (event) {
            AppRestrictionModuleUiEvent.ToggleCameraDisabled -> toggleCameraDisabled()
            AppRestrictionModuleUiEvent.ToggleMicrophoneDisabled -> toggleMicrophoneDisabled()
            AppRestrictionModuleUiEvent.ToggleInstallAppsDisabled -> toggleInstallAppsDisabled()
            AppRestrictionModuleUiEvent.ToggleInstallAppsFromUnknownSourcesDisabled -> toggleInstallAppsFromUnknownSourcesDisabled()
            AppRestrictionModuleUiEvent.ToggleUninstallAppsDisabled -> toggleUninstallAppsDisabled()
            AppRestrictionModuleUiEvent.ToggleAppControlDisabled -> toggleAppControlDisabled()
            AppRestrictionModuleUiEvent.ToggleScreenContentCaptureForAIDisabled -> toggleScreenContentCaptureForAIDisabled()
            AppRestrictionModuleUiEvent.ToggleContentSuggestionDisabled -> toggleContentSuggestionDisabled()
            AppRestrictionModuleUiEvent.ToggleScreenshotsDisabled -> toggleScreenshotsDisabled()
            AppRestrictionModuleUiEvent.ToggleDebugFeaturesDisabled -> toggleDebugFeaturesDisabled()
            AppRestrictionModuleUiEvent.ToggleConfigVPNDisabled -> toggleConfigVPNDisabled()
            AppRestrictionModuleUiEvent.ToggleLocationDisabled -> toggleLocationDisabled()
        }
    }

    private fun toggleLocationDisabled() = changeRestrictionState {
        val isLocationDisabled = state.value.isLocationDisabled
        restrictionManageContract.setLocationDisabled(!isLocationDisabled)
    }

    private fun toggleConfigVPNDisabled() = changeRestrictionState {
        val isConfigVPNDisabled = state.value.isConfigVPNDisabled
        restrictionManageContract.setConfigVPNDisabled(!isConfigVPNDisabled)
    }

    private fun toggleDebugFeaturesDisabled() = changeRestrictionState {
        val isDebugFeaturesDisabled = state.value.isDebugFeaturesDisabled
        restrictionManageContract.setDebugFeaturesDisabled(!isDebugFeaturesDisabled)
    }

    private fun toggleScreenshotsDisabled() = changeRestrictionState {
        val isScreenshotsDisabled = state.value.isScreenshotsDisabled
        restrictionManageContract.setScreenshotsDisabled(!isScreenshotsDisabled)
    }

    private fun toggleContentSuggestionDisabled() = changeRestrictionState {
        val isContentSuggestionDisabled = state.value.isContentSuggestionDisabled
        restrictionManageContract.setContentSuggestionDisabled(!isContentSuggestionDisabled)
    }

    private fun toggleScreenContentCaptureForAIDisabled() = changeRestrictionState {
        val isScreenContentCaptureForAI = state.value.isScreenContentCaptureForAIDisabled
        restrictionManageContract.setScreenContentCaptureForAIDisabled(!isScreenContentCaptureForAI)
    }

    private fun toggleAppControlDisabled() = changeRestrictionState {
        val isAppControlDisabled = state.value.isAppControlDisabled
        restrictionManageContract.setAppControlDisabled(!isAppControlDisabled)
    }

    private fun toggleUninstallAppsDisabled() = changeRestrictionState {
        val isUninstallAppsDisabled = state.value.isUninstallAppsDisabled
        restrictionManageContract.setUninstallAppsDisabled(!isUninstallAppsDisabled)
    }

    private fun toggleInstallAppsFromUnknownSourcesDisabled() = changeRestrictionState {
        val isInstallAppsFromUnknownSourcesDisabled = state.value.isInstallAppsFromUnknownSourcesDisabled
        restrictionManageContract.setInstallAppsFromUnknownSourcesDisabled(!isInstallAppsFromUnknownSourcesDisabled)
    }

    private fun toggleInstallAppsDisabled() = changeRestrictionState {
        val isInstallAppsDisabled = state.value.isInstallAppsDisabled
        restrictionManageContract.setInstallAppsDisabled(!isInstallAppsDisabled)
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