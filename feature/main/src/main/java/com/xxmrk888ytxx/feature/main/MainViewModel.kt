package com.xxmrk888ytxx.feature.main

import androidx.lifecycle.viewModelScope
import com.xxmrk888ytxx.core.base.android.uiText.uiText
import com.xxmrk888ytxx.core.base.android.viewModel.ApexOwnerViewModel
import com.xxmrk888ytxx.feature.main.contract.DeviceOwnerStateProviderContract
import com.xxmrk888ytxx.feature.main.contract.NavigateToModuleContract
import com.xxmrk888ytxx.feature.main.contract.ProvideIntentForWorkProfileCreationContract
import com.xxmrk888ytxx.feature.main.model.MainScreenEvent
import com.xxmrk888ytxx.feature.main.model.MainScreenSideEffect
import com.xxmrk888ytxx.feature.main.model.ScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val deviceOwnerStateProvider: DeviceOwnerStateProviderContract,
    private val navigateToModuleContract: NavigateToModuleContract,
    private val provideIntentForWorkProfileCreationContract: ProvideIntentForWorkProfileCreationContract
) : ApexOwnerViewModel<ScreenState, MainScreenEvent>(ScreenState()) {

    override val state: StateFlow<ScreenState> = combine(
        deviceOwnerStateProvider.operatingMode,
        deviceOwnerStateProvider.isDeviceOwner,
        deviceOwnerStateProvider.hasWorkProfile,
        deviceOwnerStateProvider.isWorkProfileQuietMode
    ) { mode, isDO, hasProfile, isQuiet ->
        ScreenState(
            operatingMode = mode,
            isDeviceOwner = isDO,
            hasWorkProfile = hasProfile,
            isWorkProfileQuietMode = isQuiet
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ScreenState())

    override fun onEvent(event: MainScreenEvent) {
        when (event) {
            MainScreenEvent.OnDeviveRestrictionButtonClicked -> toDeviceRestrictionModule()
            MainScreenEvent.OnWorkProfileManagementClicked -> toWorkProfileManagement()
            MainScreenEvent.OnHowGrantPermissionClicked -> {
                // How to grant
            }

            MainScreenEvent.OnWorkProfileButtonClicked -> sendSideEffect(
                MainScreenSideEffect.SendIntentForCreateWorkProfile(
                    provideIntentForWorkProfileCreationContract.intentForWorkProfileCreation
                )
            )

            is MainScreenEvent.OnToggleWorkProfileQuietMode -> toggleQuietMode(event.isQuiet)
            MainScreenEvent.OnWorkProfileCreatedSuccessfully -> sendToastSideEffect(R.string.work_profile_created_successfully.uiText())
            MainScreenEvent.OnWorkProfileCreationFailed -> sendToastSideEffect(R.string.couldn_t_create_a_work_profile.uiText())
        }
    }

    private fun toDeviceRestrictionModule() = viewModelScope.launch {
        navigateToModuleContract.toDeviceRestriction()
    }

    private fun toWorkProfileManagement() = viewModelScope.launch {
        navigateToModuleContract.toWorkProfileManagement()
    }

    private fun toggleQuietMode(isQuiet: Boolean) = viewModelScope.launch {
        deviceOwnerStateProvider.setWorkProfileQuietMode(isQuiet)
    }
}
