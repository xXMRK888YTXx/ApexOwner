package com.xxmrk888ytxx.core.devicepolicy

import com.xxmrk888ytxx.core.devicepolicy.model.AppPermissionPolicy
import kotlinx.coroutines.flow.Flow

interface PermissionManagerOwnerComponent {
    val isAppDeviceOwner: Boolean
    val isAppProfileOwner: Boolean
    val disabledPermissions: Flow<Set<String>>
    suspend fun setPermissionGloballyDisabled(permissionId: String, isDisable: Boolean)
    suspend fun applyPermissionsToPackage(packageName: String)

    fun getAppPermissionPolicies(packageName: String): Flow<Map<String, AppPermissionPolicy>>
    suspend fun setAppPermissionPolicy(packageName: String, permissionId: String, policy: AppPermissionPolicy)
    fun getAppRequestedPermissions(packageName: String): Set<String>
}
