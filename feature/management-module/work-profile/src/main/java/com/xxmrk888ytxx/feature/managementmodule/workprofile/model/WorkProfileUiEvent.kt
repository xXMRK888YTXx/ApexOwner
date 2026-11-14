package com.xxmrk888ytxx.feature.managementmodule.workprofile.model

import com.xxmrk888ytxx.core.base.android.mvi.UiEvent

sealed interface WorkProfileUiEvent : UiEvent {
    data class OnSearchQueryChanged(val query: String) : WorkProfileUiEvent
    data class OnToggleSystemApps(val show: Boolean) : WorkProfileUiEvent
    data class OnToggleFreezeApp(val packageName: String, val shouldFreeze: Boolean) : WorkProfileUiEvent
    data class OnCloneApp(val packageName: String) : WorkProfileUiEvent
    data class OnLaunchApp(val packageName: String) : WorkProfileUiEvent
    data class OnUninstallApp(val packageName: String) : WorkProfileUiEvent
    data object OnRefresh : WorkProfileUiEvent
    data object OnNavigateBack : WorkProfileUiEvent
    data object OnOpenWorkProfileApp : WorkProfileUiEvent
    data object OnRemoveWorkProfile : WorkProfileUiEvent
    data class OnManagePermissions(val packageName: String, val appName: String) : WorkProfileUiEvent
    data class OnToggleOneShot(val packageName: String, val isOneShot: Boolean) : WorkProfileUiEvent
    data class OnResetOneShot(val packageName: String) : WorkProfileUiEvent
}
