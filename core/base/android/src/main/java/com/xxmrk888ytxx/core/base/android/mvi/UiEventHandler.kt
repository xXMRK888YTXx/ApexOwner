package com.xxmrk888ytxx.core.base.android.mvi

interface UiEventHandler<EVENT : UiEvent> {
    fun onEvent(event: EVENT)
}