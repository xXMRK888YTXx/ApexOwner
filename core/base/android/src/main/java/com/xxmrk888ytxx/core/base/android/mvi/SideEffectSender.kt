package com.xxmrk888ytxx.core.base.android.mvi

import kotlinx.coroutines.flow.Flow

interface SideEffectSender<EFFECT : SideEffect> {
    val sideEffectFlow: Flow<EFFECT>
}