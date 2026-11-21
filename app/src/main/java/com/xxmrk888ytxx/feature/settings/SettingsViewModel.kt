package com.xxmrk888ytxx.feature.settings

import android.content.Context
import androidx.biometric.BiometricManager
import androidx.lifecycle.viewModelScope
import com.xxmrk888ytxx.apexowner.domain.AppLockRepository
import com.xxmrk888ytxx.apexowner.location.LocationSpoofManager
import com.xxmrk888ytxx.apexowner.location.LocationSpoofState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xxmrk888ytxx.core.devicepolicy.ProfileOwnerManager
import com.xxmrk888ytxx.feature.managementmodule.workprofile.contract.WorkProfileContract
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
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
    val actionFailed: Boolean = false,
    val hasPassword: Boolean = false,
    val biometricEnabled: Boolean = false,
    val biometricAvailable: Boolean = false,
    val locationSpoof: LocationSpoofState = LocationSpoofState()
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val profileOwnerManager: ProfileOwnerManager,
    private val workProfileContract: WorkProfileContract,
    private val appLockRepository: AppLockRepository,
    private val locationSpoofManager: LocationSpoofManager,
    @ApplicationContext context: Context
) : ViewModel() {
    private val deviceOwnerCleared = MutableStateFlow(false)
    private val workProfileRemoved = MutableStateFlow(false)
    private val isBusy = MutableStateFlow(false)
    private val actionFailed = MutableStateFlow(false)
    private val biometricAvailable = BiometricManager.from(context).canAuthenticate(
        BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.BIOMETRIC_WEAK
    ) == BiometricManager.BIOMETRIC_SUCCESS

    val state: StateFlow<SettingsScreenState> = combine(
        workProfileContract.hasWorkProfile,
        workProfileRemoved,
        deviceOwnerCleared,
        isBusy,
        actionFailed,
        appLockRepository.hasPassword,
        appLockRepository.biometricEnabled,
        locationSpoofManager.state
    ) { values ->
        SettingsScreenState(
            isDeviceOwner = !(values[2] as Boolean) && profileOwnerManager.isAppDeviceOwner,
            hasWorkProfile = ((values[0] as Boolean) || workProfileContract.isInsideWorkProfile) && !(values[1] as Boolean),
            isBusy = values[3] as Boolean,
            actionFailed = values[4] as Boolean,
            hasPassword = values[5] as Boolean,
            biometricEnabled = values[6] as Boolean,
            biometricAvailable = biometricAvailable,
            locationSpoof = values[7] as LocationSpoofState
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

    fun setPassword(password: String) = viewModelScope.launch {
        if (password.length < 4) return@launch
        appLockRepository.setPassword(password)
    }

    fun clearPassword() = viewModelScope.launch {
        appLockRepository.clearPassword()
    }

    fun setBiometricEnabled(enabled: Boolean) = viewModelScope.launch {
        if (state.value.hasPassword && biometricAvailable) {
            appLockRepository.setBiometricEnabled(enabled)
        }
    }

    fun setLocationCoordinates(latitude: String, longitude: String) {
        locationSpoofManager.setCoordinates(latitude, longitude)
    }

    fun setLocationSpoofEnabled(enabled: Boolean) {
        locationSpoofManager.toggle(enabled)
    }

    fun refreshLocationSpoof() {
        locationSpoofManager.refresh()
    }

    fun mockLocationSettingsIntent() = locationSpoofManager.openMockLocationSettings()
}
