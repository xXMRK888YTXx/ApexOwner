package com.xxmrk888ytxx.apexowner.featureContract.appRestriction

import com.xxmrk888ytxx.core.devicepolicy.PermissionManagerOwnerComponent
import com.xxmrk888ytxx.feature.managementmodule.apprestriction.permission.contract.GlobalPermissionManageContract
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GlobalPermissionManageContractImpl @Inject constructor(
    private val permissionManagerOwnerComponent: PermissionManagerOwnerComponent
) : GlobalPermissionManageContract {
    override val isAppDeviceOwner: Boolean
        get() = permissionManagerOwnerComponent.isAppDeviceOwner

    override val isAppProfileOwner: Boolean
        get() = permissionManagerOwnerComponent.isAppProfileOwner

    override val disabledPermissions: Flow<Set<String>>
        get() = permissionManagerOwnerComponent.disabledPermissions

    override suspend fun setPermissionGloballyDisabled(permissionId: String, isDisable: Boolean) {
        permissionManagerOwnerComponent.setPermissionGloballyDisabled(permissionId, isDisable)
    }
}
