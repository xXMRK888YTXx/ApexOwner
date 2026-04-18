package com.xxmrk888ytxx.android.mvi

interface UiModel<STATE : UiState, EVENT : UiEvent> : UiEventHandler<EVENT>, UiStateHolder<STATE>