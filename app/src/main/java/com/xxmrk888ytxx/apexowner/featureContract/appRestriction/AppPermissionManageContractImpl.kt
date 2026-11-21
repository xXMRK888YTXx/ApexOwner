package com.xxmrk888ytxx.apexowner.featureContract.appRestriction

import com.xxmrk888ytxx.core.devicepolicy.PermissionManagerOwnerComponent
import com.xxmrk888ytxx.feature.managementmodule.apprestriction.permission.app.contract.AppPermissionManageContract
import com.xxmrk888ytxx.feature.managementmodule.apprestriction.permission.app.model.AppPermissionPolicy
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import com.xxmrk888ytxx.core.devicepolicy.model.AppPermissionPolicy as OwnerAppPermissionPolicy

class AppPermissionManageContractImpl @Inject constructor(
    private val permissionManagerOwnerComponent: PermissionManagerOwnerComponent
) : AppPermissionManageContract {
    override val isAppDeviceOwner: Boolean
        get() = permissionManagerOwnerComponent.isAppDeviceOwner

    override val isAppProfileOwner: Boolean
        get() = permissionManagerOwnerComponent.isAppProfileOwner

    override val globallyDisabledPermissions: Flow<Set<String>>
        get() = permissionManagerOwnerComponent.disabledPermissions

    override fun getAppPermissionPolicies(packageName: String): Flow<Map<String, AppPermissionPolicy>> {
        return permissionManagerOwnerComponent.getAppPermissionPolicies(packageName).map { map ->
            map.mapValues { (_, v) ->
                when (v) {
                    OwnerAppPermissionPolicy.DEFAULT -> AppPermissionPolicy.DEFAULT
                    OwnerAppPermissionPolicy.USER_CONTROLLED -> AppPermissionPolicy.USER_CONTROLLED
                    OwnerAppPermissionPolicy.FORCE_DENIED -> AppPermissionPolicy.FORCE_DENIED
                    OwnerAppPermissionPolicy.FORCE_GRANTED -> AppPermissionPolicy.FORCE_GRANTED
                }
            }
        }
    }

    override fun getAppRequestedPermissions(packageName: String): Set<String> {
        return permissionManagerOwnerComponent.getAppRequestedPermissions(packageName)
    }

    override suspend fun setAppPermissionPolicy(packageName: String, permissionId: String, policy: AppPermissionPolicy) {
        val ownerPolicy = when (policy) {
            AppPermissionPolicy.DEFAULT -> OwnerAppPermissionPolicy.DEFAULT
            AppPermissionPolicy.USER_CONTROLLED -> OwnerAppPermissionPolicy.USER_CONTROLLED
            AppPermissionPolicy.FORCE_DENIED -> OwnerAppPermissionPolicy.FORCE_DENIED
            AppPermissionPolicy.FORCE_GRANTED -> OwnerAppPermissionPolicy.FORCE_GRANTED
        }
        permissionManagerOwnerComponent.setAppPermissionPolicy(packageName, permissionId, ownerPolicy)
    }
}
