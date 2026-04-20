package com.xxmrk888ytxx.feature.managementmodule.apprestriction.model

import androidx.annotation.DrawableRes
import com.xxmrk888ytxx.core.base.android.uiText.UiText

data class Restriction(
    val id: Short,
    @param:DrawableRes val iconRes: Int,
    val title: UiText,
    val description: UiText,
    val isEnabled: Boolean,
    val isAvailable: Boolean,
    val unavailableMessage: UiText? = null,
    val uiEventForToggle: AppRestrictionModuleUiEvent
)
