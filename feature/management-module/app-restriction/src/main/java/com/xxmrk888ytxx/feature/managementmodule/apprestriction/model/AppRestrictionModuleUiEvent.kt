package com.xxmrk888ytxx.feature.managementmodule.apprestriction.model

import com.xxmrk888ytxx.core.base.android.mvi.UiEvent

sealed interface AppRestrictionModuleUiEvent : UiEvent {
    data object ToggleCameraDisabled : AppRestrictionModuleUiEvent
    data object ToggleMicrophoneDisabled : AppRestrictionModuleUiEvent
    data object ToggleUSBDataSignalDisable : AppRestrictionModuleUiEvent
    data object ToggleUSBFileTransferDisabled : AppRestrictionModuleUiEvent
    data object ToggleInstallAppsDisabled : AppRestrictionModuleUiEvent
    data object ToggleInstallAppsFromUnknownSourcesDisabled : AppRestrictionModuleUiEvent
    data object ToggleUninstallAppsDisabled : AppRestrictionModuleUiEvent
    data object ToggleAppControlDisabled : AppRestrictionModuleUiEvent
    data object ToggleScreenContentCaptureForAIDisabled : AppRestrictionModuleUiEvent
    data object ToggleContentSuggestionDisabled : AppRestrictionModuleUiEvent
    data object ToggleScreenshotsDisabled : AppRestrictionModuleUiEvent
    data object ToggleDebugFeaturesDisabled : AppRestrictionModuleUiEvent
    data object ToggleFactoryResetDisabled : AppRestrictionModuleUiEvent
    data object ToggleSafeBootDisabled : AppRestrictionModuleUiEvent
    data object ToggleAddUserDisabled : AppRestrictionModuleUiEvent
    data object ToggleRemoveUserDisabled : AppRestrictionModuleUiEvent
    data object ToggleSwitchUserDisabled : AppRestrictionModuleUiEvent
    data object ToggleBluetoothDisabled : AppRestrictionModuleUiEvent
    data object ToggleBluetoothConfigDisabled : AppRestrictionModuleUiEvent
    data object ToggleMountPhysicalMediaDisabled : AppRestrictionModuleUiEvent
    data object ToggleLocationDisabled : AppRestrictionModuleUiEvent
    data object ToggleNFCDisabled : AppRestrictionModuleUiEvent
}