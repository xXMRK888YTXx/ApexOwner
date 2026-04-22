package com.xxmrk888ytxx.feature.managementmodule.apprestriction.model

import com.xxmrk888ytxx.core.base.android.mvi.UiState

data class ScreenState(
    val isCameraDisabled: Boolean = false,
    val isMicrophoneDisabled: Boolean = false,
    val isCanDisableUSBDataSignal: Boolean = true,
    val isUSBDataSignalDisabled: Boolean = false,
    val isUSBFileTransferDisabled: Boolean = false,
    val isInstallAppsDisabled: Boolean = false,
    val isInstallAppsFromUnknownSourcesDisabled: Boolean = false,
    val isUninstallAppsDisabled: Boolean = false,
) : UiState
