package com.xxmrk888ytxx.feature.managementmodule.apprestriction.model

import com.xxmrk888ytxx.core.base.android.mvi.UiEvent

sealed interface AppRestrictionModuleUiEvent : UiEvent {
    data object ToggleCameraDisabled : AppRestrictionModuleUiEvent
    data object ToggleMicrophoneDisabled : AppRestrictionModuleUiEvent
    data object ToggleUSBDataSignalDisable: AppRestrictionModuleUiEvent
    data object ToggleUSBFileTransferDisabled: AppRestrictionModuleUiEvent
    data object ToggleInstallAppsDisabled: AppRestrictionModuleUiEvent
    data object ToggleInstallAppsFromUnknownSourcesDisabled: AppRestrictionModuleUiEvent
}