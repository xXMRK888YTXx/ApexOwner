package com.xxmrk888ytxx.main

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.xxmrk888ytxx.android.mvi.SideEffect
import com.xxmrk888ytxx.main.model.MainScreenEvent
import com.xxmrk888ytxx.main.model.ScreenState
import kotlinx.coroutines.flow.Flow

@Composable
fun MainScreen(
    screenState: ScreenState,
    onEvent: (MainScreenEvent) -> Unit,
    sideEffect: Flow<SideEffect>
) {
    Text("MainScreen")
}