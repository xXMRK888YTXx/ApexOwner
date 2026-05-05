package com.xxmrk888ytxx.apexowner.featureContract.main

import com.xxmrk888ytxx.apexowner.core.navigation.Screen
import com.xxmrk888ytxx.apexowner.domain.NavigationManager
import com.xxmrk888ytxx.feature.main.contract.NavigateToModuleContract
import javax.inject.Inject

class NavigateToModuleContractImpl @Inject constructor(
    private val navigationManager: NavigationManager
) : NavigateToModuleContract {
    override suspend fun toDeviceRestriction() {
        navigationManager.navigate(Screen.DeviceRestrictionModuleScreen)
    }
}