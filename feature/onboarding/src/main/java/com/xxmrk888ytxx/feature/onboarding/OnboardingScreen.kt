package com.xxmrk888ytxx.feature.onboarding

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.xxmrk888ytxx.core.base.android.mvi.SideEffect
import com.xxmrk888ytxx.core.base.android.mvi.UiEvent
import com.xxmrk888ytxx.core.base.android.viewModel.stub.Stub
import com.xxmrk888ytxx.core.base.compose.LocalNavigator
import com.xxmrk888ytxx.core.base.compose.extension.HandleSideEffects
import com.xxmrk888ytxx.feature.onboarding.model.OnboardingScreenSideEffect
import com.xxmrk888ytxx.feature.onboarding.model.OnboardingScreenUiEvent
import kotlinx.coroutines.flow.Flow

@Composable
fun OnboardingScreen(
    state: Stub,
    onEvent: (UiEvent) -> Unit,
    sideEffect: Flow<SideEffect>
) {

    val navigator = LocalNavigator.current
    HandleSideEffects<OnboardingScreenSideEffect>(sideEffect) { }

    Column(Modifier.fillMaxSize()) {
        Text(stringResource(R.string.onboarding_screen_title))
        Button(onClick = { onEvent(OnboardingScreenUiEvent.NextPage) }) {
            Text(stringResource(R.string.onboarding_next))
        }
    }

}
