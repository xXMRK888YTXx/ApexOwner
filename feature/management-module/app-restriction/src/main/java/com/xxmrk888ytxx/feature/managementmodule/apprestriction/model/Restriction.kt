package com.xxmrk888ytxx.feature.managementmodule.apprestriction.model

import androidx.annotation.DrawableRes
import com.xxmrk888ytxx.core.base.android.uiText.UiText
import com.xxmrk888ytxx.core.base.android.uiText.uiText
import com.xxmrk888ytxx.feature.managementmodule.apprestriction.R

data class Restriction(
    val id: Short,
    @param:DrawableRes val iconRes: Int,
    val title: UiText,
    val description: UiText,
    val isEnabled: Boolean,
    val isAvailable: Boolean = true,
    val unavailableMessage: UiText = R.string.not_available_for_your_android_version.uiText(),
    val uiEventForToggle: AppRestrictionModuleUiEvent
)
