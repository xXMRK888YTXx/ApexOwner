package com.xxmrk888ytxx.feature.main.contract

interface NavigateToModuleContract {
    suspend fun toDeviceRestriction()
    suspend fun toWorkProfileManagement()
    suspend fun toGlobalPermissions()
}
