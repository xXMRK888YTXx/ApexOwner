package com.xxmrk888ytxx.android.mvi

interface UiEventHandler<EVENT : UiEvent> {
    fun onEvent(event: EVENT)
}