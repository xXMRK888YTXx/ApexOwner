package com.xxmrk888ytxx.feature.managementmodule.apprestriction.permission.model

import com.xxmrk888ytxx.core.base.android.mvi.UiEvent

sealed interface GlobalPermissionUiEvent : UiEvent {
    data class TogglePermission(val permissionId: String, val isDisable: Boolean) : GlobalPermissionUiEvent
    data class ChangeSearchQuery(val query: String) : GlobalPermissionUiEvent
}
