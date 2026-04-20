package com.xxmrk888ytxx.apexowner.di.module

import com.xxmrk888ytxx.apexowner.featureContract.main.DeviceOwnerStateProviderContractImpl
import com.xxmrk888ytxx.main.contract.DeviceOwnerStateProviderContract
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface MainModule {
    @Binds
    fun bindDeviceOwnerStateProviderContract(deviceOwnerStateProviderContractImpl: DeviceOwnerStateProviderContractImpl): DeviceOwnerStateProviderContract
}