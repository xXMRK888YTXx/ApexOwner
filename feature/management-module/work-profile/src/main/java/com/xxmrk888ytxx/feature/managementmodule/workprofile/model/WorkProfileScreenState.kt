package com.xxmrk888ytxx.feature.managementmodule.workprofile.model

import com.xxmrk888ytxx.core.base.android.mvi.UiState

data class WorkProfileScreenState(
    val searchQuery: String = "",
    val showSystemApps: Boolean = false,
    val apps: List<WorkProfileApp> = emptyList(),
    val profileApps: List<WorkProfileApp> = emptyList(),
    val personalApps: List<WorkProfileApp> = emptyList(),
    val isLoading: Boolean = false,
    val hasWorkProfile: Boolean = true,
    val isInsideWorkProfile: Boolean = false,
    val isDeviceOwner: Boolean = false,
    val canFreezeInCurrentProfile: Boolean = false
) : UiState
