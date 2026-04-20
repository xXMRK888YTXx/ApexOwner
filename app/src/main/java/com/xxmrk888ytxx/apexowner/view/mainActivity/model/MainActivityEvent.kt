package com.xxmrk888ytxx.apexowner.view.mainActivity.model

import com.xxmrk888ytxx.core.android.mvi.UiEvent

sealed interface MainActivityEvent : UiEvent {
    data class BottomItemClicked(val item: ApexOwnerBottomBarItem) : MainActivityEvent
    data object NavigationUp : MainActivityEvent
}