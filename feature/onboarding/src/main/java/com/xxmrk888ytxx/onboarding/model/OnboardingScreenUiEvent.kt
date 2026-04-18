package com.xxmrk888ytxx.onboarding.model

import com.xxmrk888ytxx.android.mvi.UiEvent

sealed interface OnboardingScreenUiEvent : UiEvent {
    object NextPage : OnboardingScreenUiEvent
}