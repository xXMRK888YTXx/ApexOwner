package com.xxmrk888ytxx.core.devicepolicy

import android.app.admin.DevicePolicyManager
import android.content.Context
import com.xxmrk888ytxx.core.devicepolicy.model.PolicyOperatingMode
import com.xxmrk888ytxx.core.devicepolicy.model.PolicyState
import com.xxmrk888ytxx.core.devicepolicy.ownerComponent.BaseOwnerComponent
import com.xxmrk888ytxx.core.devicepolicy.ownerComponent.DeviceRestrictionOwnerComponentManagerImpl
import com.xxmrk888ytxx.core.devicepolicy.ownerComponent.PermissionManagerOwnerComponentImpl
import com.xxmrk888ytxx.core.devicepolicy.ownerComponent.WorkProfileOwnerComponentManagerImpl
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onSubscription
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class AndroidDeviceOwnerManager @Inject constructor(
    private val deviceRestrictionOwnerComponentManager: DeviceRestrictionOwnerComponentManagerImpl,
    private val workProfileOwnerComponentManagerImpl: WorkProfileOwnerComponentManagerImpl,
    private val permissionManagerOwnerComponentImpl: PermissionManagerOwnerComponentImpl,
    devicePolicyManager: DevicePolicyManager,
    @ApplicationContext context: Context
) : BaseOwnerComponent(devicePolicyManager, context),
    DeviceOwnerManager,
    DeviceRestrictionOwnerComponentManager by deviceRestrictionOwnerComponentManager,
    WorkProfileOwnerComponentManager by workProfileOwnerComponentManagerImpl,
    PermissionManagerOwnerComponent by permissionManagerOwnerComponentImpl {

    override val isAppDeviceOwner: Boolean
        get() = checkIsAppDeviceOwner()

    override val isAppProfileOwner: Boolean
        get() = checkIsAppProfileOwner()

    private val _isDeviceOwner = MutableStateFlow(checkIsAppDeviceOwner())
    private val _policyState = MutableStateFlow(buildCurrentPolicyState())

    override val isDeviceOwner: Flow<Boolean> = _isDeviceOwner
        .asStateFlow()
        .onSubscription {
            updateDeviceOwnerState()
        }

    override val policyState: Flow<PolicyState> = _policyState
        .asStateFlow()
        .onSubscription { updatePolicyState() }

    override suspend fun updateDeviceOwnerState() {
        val isOwner = checkIsAppDeviceOwner()
        _isDeviceOwner.value = isOwner
        if (checkCanManageOwnerPolicies()) {
            updateRestrictionState()
        }
        updatePolicyState()
    }

    override suspend fun updatePolicyState() {
        workProfileOwnerComponentManagerImpl.updateProfileOwnerState()
        if (checkCanManageOwnerPolicies()) {
            updateRestrictionState()
        }
        _policyState.value = buildCurrentPolicyState()
    }

    private fun buildCurrentPolicyState(): PolicyState {
        val isDO = checkIsAppDeviceOwner()
        val isPO = checkIsAppProfileOwner()
        val isInside = workProfileOwnerComponentManagerImpl.isInsideWorkProfile
        val hasProfile = workProfileOwnerComponentManagerImpl.getWorkProfileUserHandle() != null || isInside
        val mode = when {
            isDO -> PolicyOperatingMode.DEVICE_OWNER
            isPO -> PolicyOperatingMode.PROFILE_OWNER
            else -> PolicyOperatingMode.PERSONAL_HOST
        }
        return PolicyState(
            operatingMode = mode,
            isDeviceOwner = isDO,
            isProfileOwner = isPO,
            isInsideWorkProfile = isInside,
            hasWorkProfile = hasProfile
        )
    }
}
