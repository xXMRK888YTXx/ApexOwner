package com.xxmrk888ytxx.feature.managementmodule.apprestriction.permission.app.model

import com.xxmrk888ytxx.core.base.android.uiText.UiText

data class AppPermissionUiModel(
    val id: String,
    val technicalName: String,
    val title: UiText,
    val description: UiText,
    val groupName: UiText,
    val iconRes: Int,
    val isRequestedByApp: Boolean,
    val policy: AppPermissionPolicy,
    val isGloballyDisabled: Boolean,
    val canForceGrant: Boolean = true
)
