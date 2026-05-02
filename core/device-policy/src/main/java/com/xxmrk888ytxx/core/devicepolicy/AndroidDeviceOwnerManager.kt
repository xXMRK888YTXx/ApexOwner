package com.xxmrk888ytxx.core.devicepolicy

import android.app.admin.DevicePolicyManager
import android.content.Context
import com.xxmrk888ytxx.core.devicepolicy.ownerComponent.BaseOwnerComponent
import com.xxmrk888ytxx.core.devicepolicy.ownerComponent.DeviceRestrictionOwnerComponentManagerImpl
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
    devicePolicyManager: DevicePolicyManager,
    @ApplicationContext context: Context,
    deviceRestrictionOwnerComponentManagerImpl: DeviceRestrictionOwnerComponentManagerImpl,
    workProfileOwnerComponentManagerImpl: WorkProfileOwnerComponentManagerImpl
) : BaseOwnerComponent(devicePolicyManager, context), DeviceOwnerManager,
    DeviceRestrictionOwnerComponentManager by deviceRestrictionOwnerComponentManagerImpl,
    WorkProfileOwnerComponentManager by workProfileOwnerComponentManagerImpl {
    private val _isProfileOwner = MutableStateFlow(checkIsAppProfileOwner())

    override val isProfileOwner: Flow<Boolean> = _isProfileOwner
        .asAndroidDeviceOwnerFlow()

    override suspend fun updateDeviceOwnerState() {
        if (!checkIsAppProfileOwner()) return
        _isProfileOwner.value = checkIsAppProfileOwner()
        updateRestrictionState()
    }

    private fun <T> MutableStateFlow<T>.asAndroidDeviceOwnerFlow(): Flow<T> =
        asStateFlow().onSubscription { updateDeviceOwnerState() }
}