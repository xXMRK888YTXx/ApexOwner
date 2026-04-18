package com.xxmrk888ytxx.apexowner.core

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface Screen : NavKey {
    @Serializable
    object Stub : Screen
    @Serializable
    object OnboardingScreen : Screen
    @Serializable
    data object MainScreen : Screen
}