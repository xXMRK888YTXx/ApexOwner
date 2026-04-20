package com.xxmrk888ytxx.core.base.android.mvi

import kotlinx.coroutines.flow.StateFlow

interface UiStateHolder<out STATE> {
    val state: StateFlow<STATE>
}