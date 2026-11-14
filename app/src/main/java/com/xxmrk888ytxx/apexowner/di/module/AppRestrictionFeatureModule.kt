package com.xxmrk888ytxx.apexowner.di.module

import com.xxmrk888ytxx.apexowner.featureContract.appRestriction.AppPermissionManageContractImpl
import com.xxmrk888ytxx.apexowner.featureContract.appRestriction.GlobalPermissionManageContractImpl
import com.xxmrk888ytxx.apexowner.featureContract.appRestriction.RestrictionManageContractImpl
import com.xxmrk888ytxx.feature.managementmodule.apprestriction.contract.RestrictionManageContract
import com.xxmrk888ytxx.feature.managementmodule.apprestriction.permission.app.contract.AppPermissionManageContract
import com.xxmrk888ytxx.feature.managementmodule.apprestriction.permission.contract.GlobalPermissionManageContract
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface AppRestrictionFeatureModule {
    @Binds
    fun bindsRestrictionManageContract(restrictionManageContractImpl: RestrictionManageContractImpl): RestrictionManageContract

    @Binds
    fun bindsGlobalPermissionManageContract(globalPermissionManageContractImpl: GlobalPermissionManageContractImpl): GlobalPermissionManageContract

    @Binds
    fun bindsAppPermissionManageContract(appPermissionManageContractImpl: AppPermissionManageContractImpl): AppPermissionManageContract
}
