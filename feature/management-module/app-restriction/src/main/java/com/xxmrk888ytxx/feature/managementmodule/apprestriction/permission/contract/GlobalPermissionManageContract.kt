package com.xxmrk888ytxx.feature.managementmodule.apprestriction.permission.contract

import kotlinx.coroutines.flow.Flow

interface GlobalPermissionManageContract {
    val isAppDeviceOwner: Boolean
    val isAppProfileOwner: Boolean
    val disabledPermissions: Flow<Set<String>>
    suspend fun setPermissionGloballyDisabled(permissionId: String, isDisable: Boolean)
}
