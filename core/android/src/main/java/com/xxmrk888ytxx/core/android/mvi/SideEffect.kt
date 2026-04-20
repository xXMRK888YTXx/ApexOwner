package com.xxmrk888ytxx.core.android.mvi

import com.xxmrk888ytxx.core.android.Navigator
import com.xxmrk888ytxx.core.android.uiText.UiText

interface SideEffect


sealed interface DefaultSideEffect : SideEffect {
    data class ShowToast(val message: UiText) : DefaultSideEffect
    object NavigationBack : DefaultSideEffect
    data class NavigationAction(val action: Navigator.() -> Unit) : DefaultSideEffect
}