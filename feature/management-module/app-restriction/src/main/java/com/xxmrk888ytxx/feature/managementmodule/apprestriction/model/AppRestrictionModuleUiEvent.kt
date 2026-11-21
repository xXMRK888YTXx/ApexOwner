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
    data object ToggleOutgoingCallsDisabled : AppRestrictionModuleUiEvent
    data object ToggleSMSDisabled : AppRestrictionModuleUiEvent
    data object ToggleWallpaperChangeDisabled : AppRestrictionModuleUiEvent
    data object ToggleFunDisabled : AppRestrictionModuleUiEvent
    data object ToggleWifiConfigDisabled : AppRestrictionModuleUiEvent
    data object ToggleWifiStateChangeDisabled : AppRestrictionModuleUiEvent
    data object ToggleHotspotDisabled : AppRestrictionModuleUiEvent
    data object ToggleAddNewWifiNetworksDisabled : AppRestrictionModuleUiEvent
    data object ToggleAirplaneModeDisabled : AppRestrictionModuleUiEvent
    data object ToggleConfigVPNDisabled : AppRestrictionModuleUiEvent
    data object ToggleConfigPrivateDNSDisabled : AppRestrictionModuleUiEvent
    data object ToggleRoamingDisabled : AppRestrictionModuleUiEvent
    data object ToggleMobileDataDisabled: AppRestrictionModuleUiEvent
    data object Toggle2GNetworkDisabled : AppRestrictionModuleUiEvent
    data class ToggleUserRestriction(val restrictionKey: String) : AppRestrictionModuleUiEvent
}

object AdditionalRestrictionKeys {
    const val ASSIST_CONTENT = "no_assist_content"
    const val AUTOFILL = "no_autofill"
    const val CONFIG_CREDENTIALS = "no_config_credentials"
    const val CROSS_PROFILE_COPY_PASTE = "no_cross_profile_copy_paste"
    const val MODIFY_ACCOUNTS = "no_modify_accounts"
    const val OUTGOING_BEAM = "no_outgoing_beam"
    const val PRINTING = "no_printing"
    const val SET_USER_ICON = "no_set_user_icon"
    const val SHARE_INTO_MANAGED_PROFILE = "no_sharing_into_profile"
    const val UNIFIED_PASSWORD = "no_unified_password"
    const val VERIFY_APPS = "ensure_verify_apps"
}
