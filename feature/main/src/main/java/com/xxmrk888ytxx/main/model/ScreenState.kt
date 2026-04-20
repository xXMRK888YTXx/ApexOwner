package com.xxmrk888ytxx.main.model

import com.xxmrk888ytxx.android.mvi.UiState

data class ScreenState(
    val isOwnerPermissionGranted: Boolean = false,
) : UiState
