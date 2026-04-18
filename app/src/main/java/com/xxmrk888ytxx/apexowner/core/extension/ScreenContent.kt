package com.xxmrk888ytxx.apexowner.core.extension

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.xxmrk888ytxx.android.mvi.SideEffect
import com.xxmrk888ytxx.android.mvi.UiEvent
import com.xxmrk888ytxx.android.mvi.UiState
import com.xxmrk888ytxx.android.viewModel.ApexOwnerViewModel
import kotlinx.coroutines.flow.Flow

@Composable
inline fun <STATE : UiState, EVENT : UiEvent, reified PVM : ApexOwnerViewModel<STATE, EVENT>> ScreenContent(
    content: @Composable (state: STATE, onEvent: (EVENT) -> Unit, sideEffect: Flow<SideEffect>) -> Unit,
){
    val viewModel: PVM = hiltViewModel<PVM>()
    val state by viewModel.state.collectAsState()
    content(state, viewModel::onEvent, viewModel.sideEffectFlow)
}