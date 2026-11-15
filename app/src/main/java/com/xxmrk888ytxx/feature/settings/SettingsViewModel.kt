package com.xxmrk888ytxx.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xxmrk888ytxx.core.devicepolicy.ProfileOwnerManager
import com.xxmrk888ytxx.feature.managementmodule.workprofile.contract.WorkProfileContract
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsScreenState(
    val isDeviceOwner: Boolean = false,
    val hasWorkProfile: Boolean = false,
    val isBusy: Boolean = false,
    val actionFailed: Boolean = false
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val profileOwnerManager: ProfileOwnerManager,
    private val workProfileContract: WorkProfileContract
) : ViewModel() {
    private val deviceOwnerCleared = MutableStateFlow(false)
    private val workProfileRemoved = MutableStateFlow(false)
    private val isBusy = MutableStateFlow(false)
    private val actionFailed = MutableStateFlow(false)

    val state: StateFlow<SettingsScreenState> = combine(
        workProfileContract.hasWorkProfile,
        workProfileRemoved,
        deviceOwnerCleared,
        isBusy,
        actionFailed
    ) { hasWorkProfile, removed, cleared, busy, failed ->
        SettingsScreenState(
            isDeviceOwner = !cleared && profileOwnerManager.isAppDeviceOwner,
            hasWorkProfile = (hasWorkProfile || workProfileContract.isInsideWorkProfile) && !removed,
            isBusy = busy,
            actionFailed = failed
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        SettingsScreenState(isDeviceOwner = profileOwnerManager.isAppDeviceOwner)
    )

    fun clearDeviceOwner() {
        viewModelScope.launch {
            isBusy.value = true
            actionFailed.value = false
            profileOwnerManager.clearDeviceOwner()
                .onSuccess { deviceOwnerCleared.value = true }
                .onFailure { actionFailed.value = true }
            isBusy.value = false
        }
    }

    fun removeWorkProfile() {
        viewModelScope.launch {
            isBusy.value = true
            actionFailed.value = false
            workProfileContract.removeWorkProfile()
                .onSuccess { workProfileRemoved.value = true }
                .onFailure { actionFailed.value = true }
            isBusy.value = false
        }
    }
}
