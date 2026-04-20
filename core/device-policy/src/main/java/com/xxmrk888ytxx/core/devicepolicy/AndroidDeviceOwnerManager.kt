package com.xxmrk888ytxx.core.devicepolicy

import android.app.admin.DevicePolicyManager
import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onSubscription
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class AndroidDeviceOwnerManager @Inject constructor(
    private val devicePolicyManager: DevicePolicyManager,
    @param:ApplicationContext private val context: Context
) : DeviceOwnerManager {

    private val _isDeviceOwner = MutableStateFlow(false)

    override val isDeviceOwner: Flow<Boolean> = _isDeviceOwner
        .asStateFlow()
        .onSubscription { updateDeviceOwnerState() }
    override suspend fun updateDeviceOwnerState() {
        _isDeviceOwner.value = devicePolicyManager.isDeviceOwnerApp(context.packageName)
    }
}