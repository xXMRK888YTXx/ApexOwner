package com.xxmrk888ytxx.feature.main.model

import android.content.Intent
import com.xxmrk888ytxx.core.base.android.mvi.SideEffect

sealed interface MainScreenSideEffect : SideEffect {
    data class SendIntentForCreateWorkProfile(val createWorkProfileIntent: Intent): MainScreenSideEffect
}