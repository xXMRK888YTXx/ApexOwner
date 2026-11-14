package com.xxmrk888ytxx.feature.managementmodule.apprestriction.permission.app.contract

import com.xxmrk888ytxx.feature.managementmodule.apprestriction.permission.app.model.AppPermissionPolicy
import kotlinx.coroutines.flow.Flow

interface AppPermissionManageContract {
    val isAppDeviceOwner: Boolean
    val isAppProfileOwner: Boolean
    val globallyDisabledPermissions: Flow<Set<String>>
    fun getAppPermissionPolicies(packageName: String): Flow<Map<String, AppPermissionPolicy>>
    fun getAppRequestedPermissions(packageName: String): Set<String>
    suspend fun setAppPermissionPolicy(packageName: String, permissionId: String, policy: AppPermissionPolicy)
}
