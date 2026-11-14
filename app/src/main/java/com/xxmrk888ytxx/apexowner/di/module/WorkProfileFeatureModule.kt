package com.xxmrk888ytxx.apexowner.di.module

import com.xxmrk888ytxx.apexowner.featureContract.workProfile.WorkProfileContractImpl
import com.xxmrk888ytxx.feature.managementmodule.workprofile.contract.WorkProfileContract
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface WorkProfileFeatureModule {

    @Binds
    @Singleton
    fun bindsWorkProfileContract(impl: WorkProfileContractImpl): WorkProfileContract
}
