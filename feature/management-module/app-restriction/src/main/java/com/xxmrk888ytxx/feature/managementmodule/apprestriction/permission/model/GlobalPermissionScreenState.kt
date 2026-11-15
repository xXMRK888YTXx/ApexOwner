package com.xxmrk888ytxx.feature.managementmodule.apprestriction.permission.model

import com.xxmrk888ytxx.core.base.android.mvi.UiState

data class GlobalPermissionScreenState(
    val isDeviceOwner: Boolean = false,
    val isProfileOwner: Boolean = false,
    val searchQuery: String = "",
    val permissions: List<GlobalPermissionUiModel> = emptyList()
) : UiState
