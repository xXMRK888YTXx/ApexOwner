package com.xxmrk888ytxx.feature.managementmodule.apprestriction

import com.xxmrk888ytxx.core.base.android.viewModel.ApexOwnerViewModel
import com.xxmrk888ytxx.feature.managementmodule.apprestriction.model.AppRestrictionModuleUiEvent
import com.xxmrk888ytxx.feature.managementmodule.apprestriction.model.ScreenState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class AppRestrictionModuleViewModel @Inject constructor(

) : ApexOwnerViewModel<ScreenState, AppRestrictionModuleUiEvent>(
    ScreenState()
) {
    override val state: StateFlow<ScreenState> = MutableStateFlow(ScreenState())


    override fun onEvent(event: AppRestrictionModuleUiEvent) {
        when(event) {
            else -> {}
        }
    }
}