package com.xxmrk888ytxx.feature.main.model

import com.xxmrk888ytxx.core.base.android.mvi.UiState

data class ScreenState(
    val operatingMode: MainOperatingMode = MainOperatingMode.PERSONAL_HOST,
    val isDeviceOwner: Boolean = false,
    val hasWorkProfile: Boolean = false,
    val isWorkProfileQuietMode: Boolean = false
) : UiState
