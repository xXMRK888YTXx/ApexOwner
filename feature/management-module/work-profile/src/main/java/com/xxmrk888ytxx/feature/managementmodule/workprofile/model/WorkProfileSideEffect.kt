package com.xxmrk888ytxx.feature.managementmodule.workprofile.model

import com.xxmrk888ytxx.core.base.android.mvi.SideEffect

sealed interface WorkProfileSideEffect : SideEffect {
    data object NavigateBack : WorkProfileSideEffect
}
