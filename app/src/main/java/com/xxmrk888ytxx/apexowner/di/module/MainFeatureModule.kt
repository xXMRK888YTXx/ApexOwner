package com.xxmrk888ytxx.apexowner.di.module

import com.xxmrk888ytxx.apexowner.featureContract.main.DeviceOwnerStateProviderContractImpl
import com.xxmrk888ytxx.apexowner.featureContract.main.NavigateToModuleContractImpl
import com.xxmrk888ytxx.apexowner.featureContract.main.ProvideIntentForWorkProfileCreationContractImpl
import com.xxmrk888ytxx.feature.main.contract.DeviceOwnerStateProviderContract
import com.xxmrk888ytxx.feature.main.contract.NavigateToModuleContract
import com.xxmrk888ytxx.feature.main.contract.ProvideIntentForWorkProfileCreationContract
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface MainFeatureModule {
    @Binds
    fun bindsDeviceOwnerStateProviderContract(deviceOwnerStateProviderContractImpl: DeviceOwnerStateProviderContractImpl): DeviceOwnerStateProviderContract

    @Binds
    fun bindsNavigateToModuleContract(
        navigateToModuleContractImpl: NavigateToModuleContractImpl
    ) : NavigateToModuleContract

    @Binds
    fun bindsProvideIntentForWorkProfileCreationContract(
        provideIntentForWorkProfileCreationContractImpl: ProvideIntentForWorkProfileCreationContractImpl
    ) : ProvideIntentForWorkProfileCreationContract
}