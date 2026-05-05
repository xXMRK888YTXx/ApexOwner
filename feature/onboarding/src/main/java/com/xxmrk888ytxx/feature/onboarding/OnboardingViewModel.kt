package com.xxmrk888ytxx.feature.onboarding

import androidx.lifecycle.viewModelScope
import com.xxmrk888ytxx.core.base.android.mvi.UiEvent
import com.xxmrk888ytxx.core.base.android.viewModel.stub.StubViewModel
import com.xxmrk888ytxx.feature.onboarding.contract.FinishOnboardingContract
import com.xxmrk888ytxx.feature.onboarding.model.OnboardingScreenSideEffect
import com.xxmrk888ytxx.feature.onboarding.model.OnboardingScreenUiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val finishOnboardingContract: FinishOnboardingContract
) : StubViewModel() {

    private val _effect = MutableSharedFlow<OnboardingScreenSideEffect>(extraBufferCapacity = 1)

    override fun onEvent(event: UiEvent) {
        when(event) {
            OnboardingScreenUiEvent.NextPage -> viewModelScope.launch {
                finishOnboardingContract.execute()
            }
        }
    }
}