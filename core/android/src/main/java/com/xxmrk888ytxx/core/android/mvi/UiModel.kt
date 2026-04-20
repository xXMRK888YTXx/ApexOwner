package com.xxmrk888ytxx.core.android.mvi

interface UiModel<STATE : UiState, EVENT : UiEvent> : UiEventHandler<EVENT>, UiStateHolder<STATE>