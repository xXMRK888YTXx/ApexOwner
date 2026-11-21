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
        restrictionManageContract.isNFCDisabled,
        restrictionManageContract.isLocationDisabled,
        restrictionManageContract.isOutgoingCallsDisabled,
        restrictionManageContract.isSMSDisabled,
        restrictionManageContract.isWallpaperChangeDisabled,
        restrictionManageContract.isFunDisabled,
        //
        restrictionManageContract.isWifiStateChangeDisabled,
        restrictionManageContract.isWifiConfigDisabled,
        restrictionManageContract.isHotspotDisabled,
        restrictionManageContract.isAddNewWifiNetworksDisabled,
        restrictionManageContract.isAirplaneModeDisabled,
        restrictionManageContract.isConfigVPNDisabled,
        restrictionManageContract.isConfigPrivateDNSDisabled,
        restrictionManageContract.isRoamingDisabled,
        restrictionManageContract.isConfigMobileDataDisabled,
        restrictionManageContract.is2GNetworkDisabled,
        restrictionManageContract.enabledUserRestrictions,
    ) { flowArray ->
        val isCameraDisabled = flowArray[0] as Boolean
        val isMicrophoneDisabled = flowArray[1] as Boolean
        val isUSBDataSignalDisabled = flowArray[2] as Boolean
        val isUSBFileTransferDisabled = flowArray[3] as Boolean
        val isInstallAppsDisabled = flowArray[4] as Boolean
        val isInstallAppsFromUnknownSourcesDisabled = flowArray[5] as Boolean
        val isUninstallAppsDisabled = flowArray[6] as Boolean
        val isAppControlDisabled = flowArray[7] as Boolean
        val isScreenContentCaptureForAIDisabled = flowArray[8] as Boolean
        val isContentSuggestionDisabled = flowArray[9] as Boolean
        val isScreenshotsDisabled = flowArray[10] as Boolean
        val isDebugFeaturesDisabled = flowArray[11] as Boolean
        val isFactoryResetDisabled = flowArray[12] as Boolean
        val isSafeBootDisabled = flowArray[13] as Boolean
        val isAddUserDisabled = flowArray[14] as Boolean
        val isRemoveUserDisabled = flowArray[15] as Boolean
        val isSwitchUserDisabled = flowArray[16] as Boolean
        val isBluetoothDisabled = flowArray[17] as Boolean
        val isBluetoothConfigDisabled = flowArray[18] as Boolean
        val isMountPhysicalMediaDisabled = flowArray[19] as Boolean
        val isNFCDisabled = flowArray[20] as Boolean
        val isLocationDisabled = flowArray[21] as Boolean
        val isOutgoingCallsDisabled = flowArray[22] as Boolean
        val isSMSDisabled = flowArray[23] as Boolean
        val isWallpaperChangeDisabled = flowArray[24] as Boolean
        val isFunDisabled = flowArray[25] as Boolean
        val isWifiStateChangeDisabled = flowArray[26] as Boolean
        val isWifiConfigDisabled = flowArray[27] as Boolean
        val isHotspotDisabled = flowArray[28] as Boolean
        val isAddNewWifiNetworksDisabled = flowArray[29] as Boolean
        val isAirplaneModeDisabled = flowArray[30] as Boolean
        val isConfigVPNDisabled = flowArray[31] as Boolean
        val isConfigPrivateDNSDisabled = flowArray[32] as Boolean
        val isRoamingDisabled = flowArray[33] as Boolean
        val isConfigMobileDataDisabled = flowArray[34] as Boolean
        val is2GNetworkDisabled = flowArray[35] as Boolean
        @Suppress("UNCHECKED_CAST")
        val enabledUserRestrictions = flowArray[36] as Set<String>

        ScreenState(
            isDeviceOwner = restrictionManageContract.isAppDeviceOwner,
            isProfileOwner = restrictionManageContract.isAppProfileOwner,
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
            isMountPhysicalMediaDisabled = isMountPhysicalMediaDisabled,
            isNFCDisabled = isNFCDisabled,
            isLocationDisabled = isLocationDisabled,
            isCanDisableNFC = restrictionManageContract.isCanDisableNFC,
            isOutgoingCallsDisabled = isOutgoingCallsDisabled,
            isSMSDisabled = isSMSDisabled,
            isWallpaperChangeDisabled = isWallpaperChangeDisabled,
            isFunDisabled = isFunDisabled,
            isWifiStateChangeDisabled = isWifiStateChangeDisabled,
            isWifiConfigDisabled = isWifiConfigDisabled,
            isHotspotDisabled = isHotspotDisabled,
            isAddNewWifiNetworksDisabled = isAddNewWifiNetworksDisabled,
            isAirplaneModeDisabled = isAirplaneModeDisabled,
            isConfigVPNDisabled = isConfigVPNDisabled,
            isConfigPrivateDNSDisabled = isConfigPrivateDNSDisabled,
            isRoamingDisabled = isRoamingDisabled,
            isConfigMobileDataDisabled = isConfigMobileDataDisabled,
            is2GNetworkDisabled = is2GNetworkDisabled,
            enabledUserRestrictions = enabledUserRestrictions,
            isCanDisableChangeWifiState = restrictionManageContract.isCanDisableChangeWifiState,
            isCanDisableHotspot = restrictionManageContract.isCanDisableHotspot,
            isCanDisableAddNewWifiNetworks = restrictionManageContract.isCanDisableAddNewWifiNetworks,
            isCanDisableConfigPrivateDNS = restrictionManageContract.isCanDisableConfigPrivateDNS,
            isCanDisable2GNetwork = restrictionManageContract.isCanDisable2GNetwork,
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
            AppRestrictionModuleUiEvent.ToggleLocationDisabled -> toggleLocationDisabled()
            AppRestrictionModuleUiEvent.ToggleNFCDisabled -> toggleNFCDisabled()
            AppRestrictionModuleUiEvent.ToggleOutgoingCallsDisabled -> toggleOutgoingCallsDisabled()
            AppRestrictionModuleUiEvent.ToggleSMSDisabled -> toggleSMSDisabled()
            AppRestrictionModuleUiEvent.ToggleFunDisabled -> toggleFunDisabled()
            AppRestrictionModuleUiEvent.ToggleWallpaperChangeDisabled -> toggleWallpaperChangeDisabled()
            AppRestrictionModuleUiEvent.Toggle2GNetworkDisabled -> toggle2GNetworkDisabled()
            AppRestrictionModuleUiEvent.ToggleAddNewWifiNetworksDisabled -> toggleAddNewWifiNetworksDisabled()
            AppRestrictionModuleUiEvent.ToggleAirplaneModeDisabled -> toggleAirplaneModeDisabled()
            AppRestrictionModuleUiEvent.ToggleConfigPrivateDNSDisabled -> toggleConfigPrivateDNSDisabled()
            AppRestrictionModuleUiEvent.ToggleConfigVPNDisabled -> toggleConfigVPNDisabled()
            AppRestrictionModuleUiEvent.ToggleHotspotDisabled -> toggleHotspotDisabled()
            AppRestrictionModuleUiEvent.ToggleMobileDataDisabled ->  toggleMobileDataDisabled()
            AppRestrictionModuleUiEvent.ToggleRoamingDisabled -> toggleRoamingDisabled()
            AppRestrictionModuleUiEvent.ToggleWifiConfigDisabled -> toggleWifiConfigDisabled()
            AppRestrictionModuleUiEvent.ToggleWifiStateChangeDisabled -> toggleWifiStateChangeDisabled()
            is AppRestrictionModuleUiEvent.ToggleUserRestriction -> toggleUserRestriction(event.restrictionKey)
        }
    }

    private fun toggleWifiStateChangeDisabled() = changeRestrictionState {
        val isWifiStateChangeDisabled = state.value.isWifiStateChangeDisabled
        restrictionManageContract.setWifiStateChangeDisabled(!isWifiStateChangeDisabled)
    }

    private fun toggleWifiConfigDisabled() = changeRestrictionState {
        val isWifiConfigDisabled = state.value.isWifiConfigDisabled
        restrictionManageContract.setWifiConfigDisabled(!isWifiConfigDisabled)
    }

    private fun toggleRoamingDisabled() = changeRestrictionState {
        val isRoamingDisabled = state.value.isRoamingDisabled
        restrictionManageContract.setRoamingDisabled(!isRoamingDisabled)
    }

    private fun toggleMobileDataDisabled() = changeRestrictionState {
        val isConfigMobileDataDisabled = state.value.isConfigMobileDataDisabled
        restrictionManageContract.setMobileDataDisabled(!isConfigMobileDataDisabled)
    }

    private fun toggleHotspotDisabled() = changeRestrictionState {
        val isHotspotDisabled = state.value.isHotspotDisabled
        restrictionManageContract.setHotspotDisabled(!isHotspotDisabled)
    }

    private fun toggleConfigVPNDisabled() = changeRestrictionState {
        val isConfigVPNDisabled = state.value.isConfigVPNDisabled
        restrictionManageContract.setConfigVPNDisabled(!isConfigVPNDisabled)
    }

    private fun toggleConfigPrivateDNSDisabled() = changeRestrictionState {
        val isConfigPrivateDNSDisabled = state.value.isConfigPrivateDNSDisabled
        restrictionManageContract.setConfigPrivateDNSDisabled(!isConfigPrivateDNSDisabled)
    }

    private fun toggleAirplaneModeDisabled() = changeRestrictionState {
        val isAirplaneModeDisabled = state.value.isAirplaneModeDisabled
        restrictionManageContract.setAirplaneModeDisabled(!isAirplaneModeDisabled)
    }

    private fun toggleAddNewWifiNetworksDisabled() = changeRestrictionState {
        val isAddNewWifiNetworksDisabled = state.value.isAddNewWifiNetworksDisabled
        restrictionManageContract.setAddNewWifiNetworksDisabled(!isAddNewWifiNetworksDisabled)
    }

    private fun toggle2GNetworkDisabled() = changeRestrictionState {
        val is2GNetworkDisabled = state.value.is2GNetworkDisabled
        restrictionManageContract.set2GNetworkDisabled(!is2GNetworkDisabled)
    }

    private fun toggleWallpaperChangeDisabled() = changeRestrictionState {
        val isWallpaperChangeDisabled = state.value.isWallpaperChangeDisabled
        restrictionManageContract.setWallpaperChangeDisabled(!isWallpaperChangeDisabled)
    }

    private fun toggleFunDisabled() = changeRestrictionState {
        val isFunDisabled = state.value.isFunDisabled
        restrictionManageContract.setFunDisabled(!isFunDisabled)
    }

    private fun toggleOutgoingCallsDisabled() = changeRestrictionState {
        val isOutgoingCallsDisabled = state.value.isOutgoingCallsDisabled
        restrictionManageContract.setOutgoingCallsDisabled(!isOutgoingCallsDisabled)
    }

    private fun toggleSMSDisabled() = changeRestrictionState {
        val isSMSDisabled = state.value.isSMSDisabled
        restrictionManageContract.setSMSDisabled(!isSMSDisabled)
    }

    private fun toggleNFCDisabled() = changeRestrictionState {
        val isNFCDisabled = state.value.isNFCDisabled
        restrictionManageContract.setNFCDisabled(!isNFCDisabled)
    }

    private fun toggleLocationDisabled() = changeRestrictionState {
        val isLocationDisabled = state.value.isLocationDisabled
        restrictionManageContract.setLocationDisabled(!isLocationDisabled)
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

    private fun toggleUserRestriction(restrictionKey: String) = changeRestrictionState {
        restrictionManageContract.setUserRestriction(
            restrictionKey,
            restrictionKey !in state.value.enabledUserRestrictions
        )
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
