@file:Suppress("DEPRECATION")

package com.xxmrk888ytxx.apexowner.view.mainActivity

import androidx.lifecycle.viewModelScope
import com.xxmrk888ytxx.apexowner.core.navigation.Screen
import com.xxmrk888ytxx.apexowner.domain.NavigationManager
import com.xxmrk888ytxx.apexowner.domain.SettingsRepository
import com.xxmrk888ytxx.apexowner.view.mainActivity.model.MainActivityEvent
import com.xxmrk888ytxx.core.base.android.viewModel.ApexOwnerViewModel
import com.xxmrk888ytxx.core.base.android.viewModel.stub.Stub
import com.xxmrk888ytxx.core.devicepolicy.DeviceOwnerManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val navigationManager: NavigationManager,
    private val settingsRepository: SettingsRepository,
    private val deviceOwnerManager: DeviceOwnerManager
) : ApexOwnerViewModel<Stub, MainActivityEvent>(Stub) {

    override val state: StateFlow<Stub> = MutableStateFlow(Stub)
    val backStack = navigationManager.backStack
    val navigator = navigationManager

    private val prepareScreenScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private val isAppReady = MutableStateFlow(false)
    private val isNavigationReady = navigationManager.backStack.map { !it.contains(Screen.Stub) }

    val isScreenLoading = combine(isAppReady, isNavigationReady) { isAppReady, isNavigationReady ->
        !isAppReady || !isNavigationReady
    }.stateIn(viewModelScope, SharingStarted.Eagerly,true)

    fun prepareScreen() {
        if (isAppReady.value) return
        prepareScreenScope.launch {
            val isOnboardingPassed = settingsRepository.settings.map { it.isOnboardingPassed }.first()
            if (isOnboardingPassed) {
                navigationManager.setStartDestination(Screen.MainScreen)
            } else {
                navigationManager.setStartDestination(Screen.OnboardingScreen)
            }
            isAppReady.value = true
            prepareScreenScope.cancel()
        }
    }

    override fun onEvent(event: MainActivityEvent) {
        when(event) {
            is MainActivityEvent.BottomItemClicked -> {
                // TODO
            }
            MainActivityEvent.NavigationUp -> navigationManager.navigateUp()
            MainActivityEvent.OnResume -> updateDeviceOwnerState()
        }
    }

    private fun updateDeviceOwnerState() = viewModelScope.launch {
        deviceOwnerManager.updateDeviceOwnerState()
    }

    init {
        prepareScreen()
    }
}