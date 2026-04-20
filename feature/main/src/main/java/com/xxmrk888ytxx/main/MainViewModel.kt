package com.xxmrk888ytxx.main

import com.xxmrk888ytxx.android.viewModel.ApexOwnerViewModel
import com.xxmrk888ytxx.main.model.MainScreenEvent
import com.xxmrk888ytxx.main.model.ScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor() : ApexOwnerViewModel<ScreenState, MainScreenEvent>(ScreenState()) {

    override val state: StateFlow<ScreenState> = MutableStateFlow(ScreenState())

    override fun onEvent(event: MainScreenEvent) {

    }

}