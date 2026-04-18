package com.xxmrk888ytxx.android.mvi

import kotlinx.coroutines.flow.Flow

interface SideEffectSender<EFFECT : SideEffect> {
    val sideEffectFlow: Flow<EFFECT>
}