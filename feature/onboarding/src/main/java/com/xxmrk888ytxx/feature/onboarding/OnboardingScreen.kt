package com.xxmrk888ytxx.feature.onboarding

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.xxmrk888ytxx.core.android.mvi.SideEffect
import com.xxmrk888ytxx.core.android.mvi.UiEvent
import com.xxmrk888ytxx.core.android.viewModel.stub.Stub
import com.xxmrk888ytxx.core.compose.extension.HandleSideEffects
import com.xxmrk888ytxx.feature.onboarding.model.OnboardingScreenSideEffect
import com.xxmrk888ytxx.feature.onboarding.model.OnboardingScreenUiEvent
import kotlinx.coroutines.flow.Flow
import com.xxmrk888ytxx.core.compose.LocalNavigator

@Composable
fun OnboardingScreen(
    state: Stub,
    onEvent: (UiEvent) -> Unit,
    sideEffect: Flow<SideEffect>
) {

    val navigator = LocalNavigator.current
    HandleSideEffects<OnboardingScreenSideEffect>(sideEffect) { }

    Column(Modifier.fillMaxSize()) {
        Text("OnboardingScreen")
        Button(onClick = { onEvent(OnboardingScreenUiEvent.NextPage) }) {
            Text("Next")
        }
    }

}