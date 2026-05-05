package com.xxmrk888ytxx.core.base.android.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xxmrk888ytxx.core.base.android.Navigator
import com.xxmrk888ytxx.core.base.android.mvi.DefaultSideEffect
import com.xxmrk888ytxx.core.base.android.mvi.SideEffect
import com.xxmrk888ytxx.core.base.android.mvi.SideEffectSender
import com.xxmrk888ytxx.core.base.android.mvi.UiEvent
import com.xxmrk888ytxx.core.base.android.mvi.UiModel
import com.xxmrk888ytxx.core.base.android.mvi.UiState
import com.xxmrk888ytxx.core.base.android.uiText.UiText
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.stateIn

abstract class ApexOwnerViewModel<STATE : UiState, EVENT : UiEvent>(protected val initialState: STATE) : ViewModel(),
    UiModel<STATE, EVENT>, SideEffectSender<SideEffect> {

    private val _sideEffectFlow = MutableSharedFlow<SideEffect>(extraBufferCapacity = Int.MAX_VALUE)
    override val sideEffectFlow: Flow<SideEffect> = _sideEffectFlow.asSharedFlow()

    protected fun <T> Flow<T>.stateWhileSubscribed(
        defaultStated: T
    ) = stateIn(viewModelScope, SharingStarted.Companion.WhileSubscribed(5000), defaultStated)

    protected fun Flow<STATE>.stateWhileSubscribed(): StateFlow<STATE> = stateWhileSubscribed(initialState)

    protected fun sendNavigateUpSideEffect() {
        sendSideEffect(DefaultSideEffect.NavigationBack)
    }

    protected fun sendToastSideEffect(uiText: UiText) {
        sendSideEffect(DefaultSideEffect.ShowToast(uiText))
    }

    protected fun sendNavigationAction(action: Navigator.() -> Unit) {
        sendSideEffect(DefaultSideEffect.NavigationAction(action))
    }

    protected fun sendSideEffect(sideEffect: SideEffect) {
        _sideEffectFlow.tryEmit(sideEffect)
    }
}