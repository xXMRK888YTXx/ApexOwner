package com.xxmrk888ytxx.feature.managementmodule.apprestriction

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.xxmrk888ytxx.core.base.android.mvi.SideEffect
import com.xxmrk888ytxx.feature.managementmodule.apprestriction.model.AppRestrictionModuleUiEvent
import com.xxmrk888ytxx.feature.managementmodule.apprestriction.model.ScreenState
import kotlinx.coroutines.flow.Flow

@Composable
fun AppRestrictionModuleScreen(
    screenState: ScreenState,
    onEvent: (AppRestrictionModuleUiEvent) -> Unit,
    sideEffect: Flow<SideEffect>
) {
    Text("123")
}