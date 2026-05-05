package com.xxmrk888ytxx.feature.main.model

import com.xxmrk888ytxx.core.base.android.mvi.UiEvent

sealed interface MainScreenEvent : UiEvent {
    data object OnHowGrantPermissionClicked : MainScreenEvent
    data object OnDeviveRestrictionButtonClicked : MainScreenEvent
    data object OnWorkProfileButtonClicked : MainScreenEvent
    data object OnWorkProfileCreatedSuccessfully: MainScreenEvent
    data object OnWorkProfileCreationFailed : MainScreenEvent
}