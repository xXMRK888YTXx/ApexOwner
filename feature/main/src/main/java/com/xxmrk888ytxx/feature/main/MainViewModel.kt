package com.xxmrk888ytxx.feature.main

import com.xxmrk888ytxx.core.base.android.viewModel.ApexOwnerViewModel
import com.xxmrk888ytxx.feature.main.contract.DeviceOwnerStateProviderContract
import com.xxmrk888ytxx.feature.main.model.MainScreenEvent
import com.xxmrk888ytxx.feature.main.model.ScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    deviceOwnerStateProvider: DeviceOwnerStateProviderContract
) : ApexOwnerViewModel<ScreenState, MainScreenEvent>(ScreenState()) {

    override val state: StateFlow<ScreenState> =
        deviceOwnerStateProvider.isDeviceOwner.map { isGranted ->
            ScreenState(isGranted)
        }.stateWhileSubscribed()

    override fun onEvent(event: MainScreenEvent) {
        when (event) {
            MainScreenEvent.OnDeviveRestrictionButtonClicked -> TODO()
            MainScreenEvent.OnHowGrantPermissionClicked -> {
                // TODO
            }
        }
    }

}