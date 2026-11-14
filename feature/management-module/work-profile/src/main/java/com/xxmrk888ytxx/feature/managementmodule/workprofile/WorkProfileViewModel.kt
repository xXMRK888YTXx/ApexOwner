package com.xxmrk888ytxx.feature.managementmodule.workprofile

import androidx.lifecycle.viewModelScope
import com.xxmrk888ytxx.core.base.android.uiText.uiText
import com.xxmrk888ytxx.core.base.android.viewModel.ApexOwnerViewModel
import com.xxmrk888ytxx.feature.managementmodule.workprofile.contract.WorkProfileContract
import com.xxmrk888ytxx.feature.managementmodule.workprofile.model.WorkProfileScreenState
import com.xxmrk888ytxx.feature.managementmodule.workprofile.model.WorkProfileSideEffect
import com.xxmrk888ytxx.feature.managementmodule.workprofile.model.WorkProfileUiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WorkProfileViewModel @Inject constructor(
    private val contract: WorkProfileContract
) : ApexOwnerViewModel<WorkProfileScreenState, WorkProfileUiEvent>(WorkProfileScreenState()) {

    private val searchQuery = MutableStateFlow("")
    private val showSystemApps = MutableStateFlow(false)
    private val isLoading = MutableStateFlow(false)

    private data class FilterState(
        val query: String,
        val showSystem: Boolean,
        val loading: Boolean
    )

    private val filterState = combine(
        searchQuery,
        showSystemApps,
        isLoading
    ) { query, systemApps, loading ->
        FilterState(query, systemApps, loading)
    }

    override val state: StateFlow<WorkProfileScreenState> = combine(
        filterState,
        contract.apps,
        contract.hasWorkProfile
    ) { filter, apps, hasProfile ->
        WorkProfileScreenState(
            searchQuery = filter.query,
            showSystemApps = filter.showSystem,
            apps = apps,
            profileApps = apps,
            isLoading = filter.loading,
            hasWorkProfile = hasProfile,
            isInsideWorkProfile = contract.isInsideWorkProfile,
            isDeviceOwner = contract.isDeviceOwner,
            canFreezeInCurrentProfile = contract.canFreezeInCurrentProfile
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        WorkProfileScreenState(
            isInsideWorkProfile = contract.isInsideWorkProfile,
            isDeviceOwner = contract.isDeviceOwner,
            canFreezeInCurrentProfile = contract.canFreezeInCurrentProfile
        )
    )

    override fun onEvent(event: WorkProfileUiEvent) {
        when (event) {
            is WorkProfileUiEvent.OnSearchQueryChanged -> searchQuery.value = event.query
            is WorkProfileUiEvent.OnToggleSystemApps -> showSystemApps.value = event.show
            is WorkProfileUiEvent.OnToggleFreezeApp -> toggleFreeze(event.packageName, event.shouldFreeze)
            is WorkProfileUiEvent.OnCloneApp -> cloneApp(event.packageName)
            is WorkProfileUiEvent.OnLaunchApp -> launchApp(event.packageName)
            is WorkProfileUiEvent.OnUninstallApp -> uninstallApp(event.packageName)
            WorkProfileUiEvent.OnOpenWorkProfileApp -> openWorkProfileApp()
            WorkProfileUiEvent.OnRemoveWorkProfile -> removeWorkProfile()
            WorkProfileUiEvent.OnRefresh -> refresh()
            WorkProfileUiEvent.OnNavigateBack -> sendSideEffect(WorkProfileSideEffect.NavigateBack)
            is WorkProfileUiEvent.OnManagePermissions -> {
                viewModelScope.launch {
                    contract.openAppPermissions(event.packageName, event.appName)
                }
            }
            is WorkProfileUiEvent.OnToggleOneShot -> toggleOneShot(event.packageName, event.isOneShot)
            is WorkProfileUiEvent.OnResetOneShot -> resetOneShot(event.packageName)
        }
    }

    private fun toggleFreeze(packageName: String, shouldFreeze: Boolean) {
        viewModelScope.launch {
            isLoading.value = true
            contract.toggleFreeze(packageName, shouldFreeze).fold(
                onSuccess = {
                    sendToastSideEffect(R.string.app_freeze_success.uiText())
                    contract.refresh()
                },
                onFailure = {
                    sendToastSideEffect(R.string.app_action_failed.uiText())
                }
            )
            isLoading.value = false
        }
    }

    private fun toggleOneShot(packageName: String, isOneShot: Boolean) {
        viewModelScope.launch {
            isLoading.value = true
            contract.toggleOneShot(packageName, isOneShot).fold(
                onSuccess = {
                    sendToastSideEffect(
                        if (isOneShot) R.string.one_shot_enabled.uiText()
                        else R.string.one_shot_disabled.uiText()
                    )
                    contract.refresh()
                },
                onFailure = {
                    sendToastSideEffect(R.string.app_action_failed.uiText())
                }
            )
            isLoading.value = false
        }
    }

    private fun resetOneShot(packageName: String) {
        viewModelScope.launch {
            isLoading.value = true
            contract.resetOneShot(packageName).fold(
                onSuccess = {
                    sendToastSideEffect(R.string.one_shot_reset_success.uiText())
                    contract.refresh()
                },
                onFailure = {
                    sendToastSideEffect(R.string.app_action_failed.uiText())
                }
            )
            isLoading.value = false
        }
    }

    private fun cloneApp(packageName: String) {
        viewModelScope.launch {
            isLoading.value = true
            contract.cloneApp(packageName).fold(
                onSuccess = {
                    sendToastSideEffect(R.string.app_cloned_success.uiText())
                    contract.refresh()
                },
                onFailure = {
                    sendToastSideEffect(R.string.app_action_failed.uiText())
                }
            )
            isLoading.value = false
        }
    }

    private fun launchApp(packageName: String) {
        viewModelScope.launch {
            contract.launchApp(packageName).onFailure {
                sendToastSideEffect(R.string.app_action_failed.uiText())
            }
        }
    }

    private fun uninstallApp(packageName: String) {
        viewModelScope.launch {
            contract.uninstallApp(packageName).fold(
                onSuccess = {
                    delay(500)
                    contract.refresh()
                    delay(1000)
                    contract.refresh()
                },
                onFailure = {
                    sendToastSideEffect(R.string.app_action_failed.uiText())
                }
            )
        }
    }

    private fun openWorkProfileApp() {
        viewModelScope.launch {
            contract.openWorkProfileApp().onFailure {
                sendToastSideEffect(R.string.app_action_failed.uiText())
            }
        }
    }

    private fun removeWorkProfile() {
        viewModelScope.launch {
            isLoading.value = true
            contract.removeWorkProfile().fold(
                onSuccess = {
                    sendToastSideEffect(R.string.remove_work_profile_success.uiText())
                    sendSideEffect(WorkProfileSideEffect.NavigateBack)
                },
                onFailure = {
                    sendToastSideEffect(R.string.app_action_failed.uiText())
                }
            )
            isLoading.value = false
        }
    }

    private fun refresh() {
        viewModelScope.launch {
            isLoading.value = true
            contract.refresh()
            isLoading.value = false
        }
    }

    init {
        refresh()
    }
}
