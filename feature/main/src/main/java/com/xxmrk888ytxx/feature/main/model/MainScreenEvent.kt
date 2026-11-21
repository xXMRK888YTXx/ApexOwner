package com.xxmrk888ytxx.feature.main.model

import com.xxmrk888ytxx.core.base.android.mvi.UiEvent

sealed interface MainScreenEvent : UiEvent {
    data object OnDeviveRestrictionButtonClicked : MainScreenEvent
    data object OnWorkProfileManagementClicked : MainScreenEvent
    data object OnGlobalPermissionsButtonClicked : MainScreenEvent
    data object OnLocationSpoofButtonClicked : MainScreenEvent
    data object OnWorkProfileButtonClicked : MainScreenEvent
    data object OnHowGrantPermissionClicked : MainScreenEvent
    data class OnToggleWorkProfileQuietMode(val isQuiet: Boolean) : MainScreenEvent
    data object OnWorkProfileCreatedSuccessfully : MainScreenEvent
    data object OnWorkProfileCreationFailed : MainScreenEvent
}
