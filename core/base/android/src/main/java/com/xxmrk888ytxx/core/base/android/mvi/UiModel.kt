package com.xxmrk888ytxx.core.base.android.mvi

interface UiModel<STATE : UiState, EVENT : UiEvent> : UiEventHandler<EVENT>, UiStateHolder<STATE>