package com.xxmrk888ytxx.feature.managementmodule.apprestriction.permission.model

import com.xxmrk888ytxx.core.base.android.uiText.UiText

data class GlobalPermissionUiModel(
    val id: String,
    val technicalName: String,
    val title: UiText,
    val description: UiText,
    val groupName: UiText,
    val iconRes: Int,
    val isDisabled: Boolean
)
