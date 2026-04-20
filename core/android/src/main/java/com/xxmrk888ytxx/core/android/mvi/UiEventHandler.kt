package com.xxmrk888ytxx.core.android.mvi

interface UiEventHandler<EVENT : UiEvent> {
    fun onEvent(event: EVENT)
}