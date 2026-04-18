package com.xxmrk888ytxx.apexowner.featureContract.onboarding

import com.xxmrk888ytxx.apexowner.core.Screen
import com.xxmrk888ytxx.apexowner.domain.NavigationManager
import com.xxmrk888ytxx.apexowner.domain.SettingsRepository
import com.xxmrk888ytxx.onboarding.contract.FinishOnboardingContract
import javax.inject.Inject

class FinishOnboardingContractImpl @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val navigationManager: NavigationManager
): FinishOnboardingContract {
    override suspend fun execute() {
        settingsRepository.markOnboardingAsPassed()
        navigationManager.replaceCurrentScreenAndNavigate(Screen.MainScreen)
    }
}