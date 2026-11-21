package com.xxmrk888ytxx.apexowner.feature.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xxmrk888ytxx.apexowner.domain.AppLockRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AuthState(
    val isReady: Boolean = false,
    val hasPassword: Boolean = false,
    val biometricEnabled: Boolean = false,
    val isLocked: Boolean = false,
    val error: Boolean = false
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val appLockRepository: AppLockRepository
) : ViewModel() {
    private val ready = MutableStateFlow(false)
    private val locked = MutableStateFlow(false)
    private val error = MutableStateFlow(false)

    val state: StateFlow<AuthState> = combine(
        appLockRepository.hasPassword,
        appLockRepository.biometricEnabled,
        ready,
        locked,
        error
    ) { hasPassword, biometricEnabled, isReady, isLocked, hasError ->
        AuthState(isReady, hasPassword, biometricEnabled, isLocked, hasError)
    }.stateIn(viewModelScope, SharingStarted.Eagerly, AuthState())

    init {
        viewModelScope.launch {
            appLockRepository.hasPassword.collect { hasPassword ->
                if (!ready.value) {
                    locked.value = hasPassword
                    ready.value = true
                } else if (!hasPassword) {
                    locked.value = false
                }
            }
        }
    }

    fun unlock(password: String) {
        if (password.isEmpty()) return
        viewModelScope.launch {
            val valid = appLockRepository.verifyPassword(password)
            locked.value = !valid
            error.value = !valid
        }
    }

    fun unlockWithBiometric() {
        locked.value = false
        error.value = false
    }

    fun lock() {
        if (state.value.hasPassword) {
            locked.value = true
            error.value = false
        }
    }
}
