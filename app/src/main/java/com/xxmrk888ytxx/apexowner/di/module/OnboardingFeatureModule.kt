package com.xxmrk888ytxx.apexowner.di.module

import com.xxmrk888ytxx.apexowner.featureContract.onboarding.FinishOnboardingContractImpl
import com.xxmrk888ytxx.feature.onboarding.contract.FinishOnboardingContract
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface OnboardingFeatureModule {
    @Binds
    fun bindsFinishOnboardingContract(
        finishOnboardingContractImpl: FinishOnboardingContractImpl
    ) : FinishOnboardingContract
}