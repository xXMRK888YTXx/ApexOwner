package com.xxmrk888ytxx.core.devicepolicy

import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.os.UserManager
import com.xxmrk888ytxx.core.devicepolicy.exception.AppNotDeviceOwnerException
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onSubscription
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class AndroidDeviceOwnerManager @Inject constructor(
    private val devicePolicyManager: DevicePolicyManager,
    @param:ApplicationContext private val context: Context
) : DeviceOwnerManager {

    private val deviceOwnerReceiver: ComponentName by lazy {
        ComponentName(context, ApexDeviceOwnerReceiver::class.java)
    }

    private val _isDeviceOwner = MutableStateFlow(isAppDeviceOwner())
    private val _isCameraDisabled = MutableStateFlow(isCameraDisabled())
    private val _isMicrophoneDisabled = MutableStateFlow(false)

    override val isCameraDisabled: Flow<Boolean> = _isCameraDisabled.asAndroidDeviceOwnerFlow()
    override val isMicrophoneDisabled: Flow<Boolean> =
        _isMicrophoneDisabled.asAndroidDeviceOwnerFlow()

    override val isDeviceOwner: Flow<Boolean> = _isDeviceOwner
        .asAndroidDeviceOwnerFlow()

    override suspend fun updateDeviceOwnerState() {
        if (!isAppDeviceOwner()) return
        _isDeviceOwner.value = isAppDeviceOwner()
        _isCameraDisabled.value = isCameraDisabled()
        val userRestriction = devicePolicyManager.getUserRestrictions(deviceOwnerReceiver)
        _isMicrophoneDisabled.value = userRestriction.getBoolean(UserManager.DISALLOW_UNMUTE_MICROPHONE, false)
    }

    override suspend fun setCameraDisabled(isDisabled: Boolean) = changeDeviceOwnerPolicy {
        setCameraDisabled(deviceOwnerReceiver, isDisabled)
    }

    override suspend fun setMicrophoneDisabled(isDisabled: Boolean) = changeDeviceOwnerPolicy {
        toggleUserRestriction(UserManager.DISALLOW_UNMUTE_MICROPHONE, isDisabled)
    }

    private suspend fun toggleUserRestriction(restrictionKey: String, isEnabled: Boolean) {
        if (isEnabled) {
            devicePolicyManager.addUserRestriction(deviceOwnerReceiver, restrictionKey)
        } else {
            devicePolicyManager.clearUserRestriction(deviceOwnerReceiver, restrictionKey)
        }
    }

    private suspend fun changeDeviceOwnerPolicy(block: suspend DevicePolicyManager.() -> Unit) {
        if (!isAppDeviceOwner()) throw AppNotDeviceOwnerException()
        devicePolicyManager.block()
        updateDeviceOwnerState()
    }

    private fun isAppDeviceOwner() = devicePolicyManager.isDeviceOwnerApp(context.packageName)
    private fun isCameraDisabled() = devicePolicyManager.getCameraDisabled(deviceOwnerReceiver)
    private fun <T> MutableStateFlow<T>.asAndroidDeviceOwnerFlow(): Flow<T> =
        asStateFlow().onSubscription { updateDeviceOwnerState() }
}