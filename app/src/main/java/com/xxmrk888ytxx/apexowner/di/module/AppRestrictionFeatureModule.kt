package com.xxmrk888ytxx.apexowner.di.module

import com.xxmrk888ytxx.apexowner.featureContract.appRestriction.RestrictionManageContractImpl
import com.xxmrk888ytxx.feature.managementmodule.apprestriction.contract.RestrictionManageContract
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface AppRestrictionFeatureModule {
    @Binds
    fun bindsRestrictionManageContract(restrictionManageContractImpl: RestrictionManageContractImpl): RestrictionManageContract
}