package com.xxmrk888ytxx.feature.managementmodule.apprestriction.permission.app.model

import com.xxmrk888ytxx.core.base.android.mvi.UiEvent

sealed interface AppPermissionUiEvent : UiEvent {
    data class Init(val packageName: String, val appName: String) : AppPermissionUiEvent
    data class ChangeSearchQuery(val query: String) : AppPermissionUiEvent
    data class ToggleOnlyRequested(val onlyRequested: Boolean) : AppPermissionUiEvent
    data class ChangePolicy(val permissionId: String, val policy: AppPermissionPolicy) : AppPermissionUiEvent
}
