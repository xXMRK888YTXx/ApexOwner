package com.xxmrk888ytxx.feature.main.model

import com.xxmrk888ytxx.core.android.mvi.UiEvent

sealed interface MainScreenEvent : UiEvent {
    data object OnHowGrantPermissionClicked : MainScreenEvent
    data object OnDeviveRestrictionButtonClicked : MainScreenEvent
}