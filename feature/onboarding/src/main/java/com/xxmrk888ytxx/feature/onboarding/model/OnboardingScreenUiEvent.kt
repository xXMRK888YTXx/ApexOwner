package com.xxmrk888ytxx.feature.onboarding.model

import com.xxmrk888ytxx.core.base.android.mvi.UiEvent

sealed interface OnboardingScreenUiEvent : UiEvent {
    object NextPage : OnboardingScreenUiEvent
}