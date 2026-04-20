package com.xxmrk888ytxx.feature.main.model

import com.xxmrk888ytxx.core.android.mvi.UiState

data class ScreenState(
    val isOwnerPermissionGranted: Boolean = false,
) : UiState
