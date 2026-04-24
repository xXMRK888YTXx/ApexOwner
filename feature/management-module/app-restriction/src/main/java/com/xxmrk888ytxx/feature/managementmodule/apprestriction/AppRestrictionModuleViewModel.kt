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
        restrictionManageContract.isUSBDataSignalDisabled,
        restrictionManageContract.isUSBFileTransferDisabled,
        restrictionManageContract.isInstallAppsDisabled,
        restrictionManageContract.isInstallAppsFromUnknownSourcesDisabled,
        restrictionManageContract.isUninstallAppsDisabled,
        restrictionManageContract.isAppControlDisabled,
        restrictionManageContract.isScreenContentCaptureForAIDisabled,
        restrictionManageContract.isContentSuggestionDisabled,
        restrictionManageContract.isScreenshotsDisabled,
        restrictionManageContract.isDebugFeaturesDisabled,
        restrictionManageContract.isFactoryResetDisabled,
        restrictionManageContract.isSafeBootDisabled,
        restrictionManageContract.isAddUserDisabled,
        restrictionManageContract.isRemoveUserDisabled,
        restrictionManageContract.isSwitchUserDisabled,
        restrictionManageContract.isBluetoothDisabled,
        restrictionManageContract.isBluetoothConfigDisabled,
        restrictionManageContract.isMountPhysicalMediaDisabled,
    ) { flowArray ->
        val isCameraDisabled = flowArray[0]
        val isMicrophoneDisabled = flowArray[1]
        val isUSBDataSignalDisabled = flowArray[2]
        val isUSBFileTransferDisabled = flowArray[3]
        val isInstallAppsDisabled = flowArray[4]
        val isInstallAppsFromUnknownSourcesDisabled = flowArray[5]
        val isUninstallAppsDisabled = flowArray[6]
        val isAppControlDisabled = flowArray[7]
        val isScreenContentCaptureForAIDisabled = flowArray[8]
        val isContentSuggestionDisabled = flowArray[9]
        val isScreenshotsDisabled = flowArray[10]
        val isDebugFeaturesDisabled = flowArray[11]
        val isFactoryResetDisabled = flowArray[12]
        val isSafeBootDisabled = flowArray[13]
        val isAddUserDisabled = flowArray[14]
        val isRemoveUserDisabled = flowArray[15]
        val isSwitchUserDisabled = flowArray[16]
        val isBluetoothDisabled = flowArray[17]
        val isBluetoothConfigDisabled = flowArray[18]
        val isMountPhysicalMediaDisabled = flowArray[19]




        ScreenState(
            isCameraDisabled = isCameraDisabled,
            isMicrophoneDisabled = isMicrophoneDisabled,
            isCanDisableUSBDataSignal = restrictionManageContract.isCanDisableUSBDataSignal,
            isUSBDataSignalDisabled = isUSBDataSignalDisabled,
            isUSBFileTransferDisabled = isUSBFileTransferDisabled,
            isInstallAppsDisabled = isInstallAppsDisabled,
            isInstallAppsFromUnknownSourcesDisabled = isInstallAppsFromUnknownSourcesDisabled,
            isUninstallAppsDisabled = isUninstallAppsDisabled,
            isAppControlDisabled = isAppControlDisabled,
            isCanDisableScreenContentCaptureForAI = restrictionManageContract.isCanDisableScreenContentCaptureForAI,
            isScreenContentCaptureForAIDisabled = isScreenContentCaptureForAIDisabled,
            isContentSuggestionDisabled = isContentSuggestionDisabled,
            isCanDisableContentSuggestion = restrictionManageContract.isCanDisableContentSuggestion,
            isScreenshotsDisabled = isScreenshotsDisabled,
            isDebugFeaturesDisabled = isDebugFeaturesDisabled,
            isFactoryResetDisabled = isFactoryResetDisabled,
            isSafeBootDisabled = isSafeBootDisabled,
            isAddUserDisabled = isAddUserDisabled,
            isRemoveUserDisabled = isRemoveUserDisabled,
            isSwitchUserDisabled = isSwitchUserDisabled,
            isBluetoothDisabled = isBluetoothDisabled,
            isBluetoothConfigDisabled = isBluetoothConfigDisabled,
            isMountPhysicalMediaDisabled = isMountPhysicalMediaDisabled
        )
    }.stateWhileSubscribed()


    override fun onEvent(event: AppRestrictionModuleUiEvent) {
        when (event) {
            AppRestrictionModuleUiEvent.ToggleCameraDisabled -> toggleCameraDisabled()
            AppRestrictionModuleUiEvent.ToggleMicrophoneDisabled -> toggleMicrophoneDisabled()
            AppRestrictionModuleUiEvent.ToggleUSBDataSignalDisable -> toggleUSBDataSignalDisable()
            AppRestrictionModuleUiEvent.ToggleUSBFileTransferDisabled -> toggleUSBFileTransferDisabled()
            AppRestrictionModuleUiEvent.ToggleInstallAppsDisabled -> toggleInstallAppsDisabled()
            AppRestrictionModuleUiEvent.ToggleInstallAppsFromUnknownSourcesDisabled -> toggleInstallAppsFromUnknownSourcesDisabled()
            AppRestrictionModuleUiEvent.ToggleUninstallAppsDisabled -> toggleUninstallAppsDisabled()
            AppRestrictionModuleUiEvent.ToggleAppControlDisabled -> toggleAppControlDisabled()
            AppRestrictionModuleUiEvent.ToggleScreenContentCaptureForAIDisabled -> toggleScreenContentCaptureForAIDisabled()
            AppRestrictionModuleUiEvent.ToggleContentSuggestionDisabled -> toggleContentSuggestionDisabled()
            AppRestrictionModuleUiEvent.ToggleScreenshotsDisabled -> toggleScreenshotsDisabled()
            AppRestrictionModuleUiEvent.ToggleDebugFeaturesDisabled -> toggleDebugFeaturesDisabled()
            AppRestrictionModuleUiEvent.ToggleFactoryResetDisabled -> toggleFactoryResetDisabled()
            AppRestrictionModuleUiEvent.ToggleSafeBootDisabled -> toggleSafeBootDisabled()
            AppRestrictionModuleUiEvent.ToggleAddUserDisabled -> toggleAddUserDisabled()
            AppRestrictionModuleUiEvent.ToggleRemoveUserDisabled -> toggleRemoveUserDisabled()
            AppRestrictionModuleUiEvent.ToggleSwitchUserDisabled -> toggleSwitchUserDisabled()
            AppRestrictionModuleUiEvent.ToggleBluetoothConfigDisabled -> toggleBluetoothConfigDisabled()
            AppRestrictionModuleUiEvent.ToggleBluetoothDisabled -> toggleBluetoothDisabled()
            AppRestrictionModuleUiEvent.ToggleMountPhysicalMediaDisabled -> toggleMountPhysicalMediaDisabled()
        }
    }

    private fun toggleMountPhysicalMediaDisabled() = changeRestrictionState {
        val isMountPhysicalMediaDisabled = state.value.isMountPhysicalMediaDisabled
        restrictionManageContract.setMountPhysicalMediaDisabled(!isMountPhysicalMediaDisabled)
    }

    private fun toggleBluetoothDisabled() = changeRestrictionState {
        val isBluetoothDisabled = state.value.isBluetoothDisabled
        restrictionManageContract.setBluetoothDisabled(!isBluetoothDisabled)
    }

    private fun toggleBluetoothConfigDisabled() = changeRestrictionState {
        val isBluetoothConfigDisabled = state.value.isBluetoothConfigDisabled
        restrictionManageContract.setBluetoothConfigDisabled(!isBluetoothConfigDisabled)
    }

    private fun toggleSwitchUserDisabled() = changeRestrictionState {
        val isSwitchUserDisabled = state.value.isSwitchUserDisabled
        restrictionManageContract.setSwitchUserDisabled(!isSwitchUserDisabled)
    }

    private fun toggleRemoveUserDisabled() = changeRestrictionState {
        val isRemoveUserDisabled = state.value.isRemoveUserDisabled
        restrictionManageContract.setRemoveUserDisabled(!isRemoveUserDisabled)
    }

    private fun toggleAddUserDisabled() = changeRestrictionState {
        val isAddUserDisabled = state.value.isAddUserDisabled
        restrictionManageContract.setAddUserDisabled(!isAddUserDisabled)
    }

    private fun toggleSafeBootDisabled() = changeRestrictionState {
        val isSafeBootDisabled = state.value.isSafeBootDisabled
        restrictionManageContract.setSafeBootDisabled(!isSafeBootDisabled)
    }

    private fun toggleFactoryResetDisabled() = changeRestrictionState {
        val isFactoryResetDisabled = state.value.isFactoryResetDisabled
        restrictionManageContract.setFactoryResetDisabled(!isFactoryResetDisabled)
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

    private fun toggleUSBFileTransferDisabled() = changeRestrictionState {
        val isUSBFileTransferDisabled = state.value.isUSBFileTransferDisabled
        restrictionManageContract.setUSBFileTransferDisabled(!isUSBFileTransferDisabled)
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