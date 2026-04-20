package com.xxmrk888ytxx.main.model

import com.xxmrk888ytxx.android.mvi.UiEvent

sealed interface MainScreenEvent : UiEvent {
    data object OnHowGrantPermissionClicked : MainScreenEvent
    data object OnDeviveRestrictionButtonClicked : MainScreenEvent
}