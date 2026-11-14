package com.xxmrk888ytxx.feature.managementmodule.workprofile.model

import com.xxmrk888ytxx.core.base.android.mvi.UiState

enum class WorkProfileTab {
    PROFILE_APPS,
    CLONE_APPS
}

data class WorkProfileScreenState(
    val selectedTab: WorkProfileTab = WorkProfileTab.PROFILE_APPS,
    val searchQuery: String = "",
    val showSystemApps: Boolean = false,
    val profileApps: List<WorkProfileApp> = emptyList(),
    val personalApps: List<WorkProfileApp> = emptyList(),
    val isLoading: Boolean = false,
    val hasWorkProfile: Boolean = true,
    val isInsideWorkProfile: Boolean = false
) : UiState
