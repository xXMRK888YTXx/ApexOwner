package com.xxmrk888ytxx.feature.managementmodule.apprestriction.permission.app.model

import com.xxmrk888ytxx.core.base.android.mvi.UiState

data class AppPermissionScreenState(
    val packageName: String = "",
    val appName: String = "",
    val isDeviceOwner: Boolean = false,
    val isProfileOwner: Boolean = false,
    val searchQuery: String = "",
    val onlyRequested: Boolean = false,
    val permissions: List<AppPermissionUiModel> = emptyList(),
    val totalRequestedCount: Int = 0,
    val userControlledCount: Int = 0,
    val forceGrantedCount: Int = 0,
    val forceDeniedCount: Int = 0
) : UiState
