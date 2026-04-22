package com.xxmrk888ytxx.core.devicepolicy

import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.os.Build
import android.os.UserManager
import com.xxmrk888ytxx.core.devicepolicy.exception.AppNotDeviceOwnerException
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

    private val deviceOwnerReceiver: ComponentName by lazy {
        ComponentName(context, ApexDeviceOwnerReceiver::class.java)
    }

    private val _isDeviceOwner = MutableStateFlow(checkIsAppDeviceOwner())
    private val _isCameraDisabled = MutableStateFlow(checkIsCameraDisabled())
    private val _isMicrophoneDisabled = MutableStateFlow(false)
    private val _isUSBDataSignalDisabled = MutableStateFlow(checkIsUSBDataSignalDisabled())
    private val _isUSBFileTransferDisabled = MutableStateFlow(false)
    private val _isInstallAppsDisabled = MutableStateFlow(false)
    private val _isInstallAppsFromUnknownSourcesDisabled = MutableStateFlow(false)



    override val isCameraDisabled: Flow<Boolean> = _isCameraDisabled.asAndroidDeviceOwnerFlow()
    override val isMicrophoneDisabled: Flow<Boolean> = _isMicrophoneDisabled.asAndroidDeviceOwnerFlow()
    override val isUSBDataSignalDisabled: Flow<Boolean> = _isUSBDataSignalDisabled.asAndroidDeviceOwnerFlow()
    override val isUSBFileTransferDisabled: Flow<Boolean> = _isUSBFileTransferDisabled.asAndroidDeviceOwnerFlow()
    override val isInstallAppsDisabled: Flow<Boolean> = _isInstallAppsDisabled.asAndroidDeviceOwnerFlow()
    override val isInstallAppsFromUnknownSourcesDisabled: Flow<Boolean> = _isInstallAppsFromUnknownSourcesDisabled.asAndroidDeviceOwnerFlow()


    override val isCanDisableUSBDataSignal: Boolean
        get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            devicePolicyManager.canUsbDataSignalingBeDisabled()
        } else {
            false
        }

    override val isDeviceOwner: Flow<Boolean> = _isDeviceOwner
        .asAndroidDeviceOwnerFlow()

    override suspend fun updateDeviceOwnerState() {
        if (!checkIsAppDeviceOwner()) return
        _isDeviceOwner.value = checkIsAppDeviceOwner()
        _isCameraDisabled.value = checkIsCameraDisabled()
        _isUSBDataSignalDisabled.value = checkIsUSBDataSignalDisabled()
        val userRestriction = devicePolicyManager.getUserRestrictions(deviceOwnerReceiver)
        _isMicrophoneDisabled.value =
            userRestriction.getBoolean(UserManager.DISALLOW_UNMUTE_MICROPHONE, false)
        _isUSBFileTransferDisabled.value = userRestriction.getBoolean(UserManager.DISALLOW_USB_FILE_TRANSFER, false)
        _isInstallAppsDisabled.value = userRestriction.getBoolean(UserManager.DISALLOW_INSTALL_APPS, false)
        _isInstallAppsFromUnknownSourcesDisabled.value = userRestriction.getBoolean(UserManager.DISALLOW_INSTALL_UNKNOWN_SOURCES, false)
    }

    override suspend fun setCameraDisabled(isDisabled: Boolean) = changeDeviceOwnerPolicy {
        setCameraDisabled(deviceOwnerReceiver, isDisabled)
    }

    override suspend fun setMicrophoneDisabled(isDisabled: Boolean) = changeDeviceOwnerPolicy {
        toggleUserRestriction(UserManager.DISALLOW_UNMUTE_MICROPHONE, isDisabled)
    }

    override suspend fun setUSBDataSignalDisabled(isDisabled: Boolean) = changeDeviceOwnerPolicy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            devicePolicyManager.isUsbDataSignalingEnabled = !isDisabled
        }
    }

    override suspend fun setUSBFileTransferDisabled(isDisabled: Boolean) = changeDeviceOwnerPolicy {
        toggleUserRestriction(UserManager.DISALLOW_USB_FILE_TRANSFER, isDisabled)
    }

    override suspend fun setInstallAppsDisabled(isDisabled: Boolean) = changeDeviceOwnerPolicy {
        toggleUserRestriction(UserManager.DISALLOW_INSTALL_APPS, isDisabled)
    }

    override suspend fun setInstallAppsFromUnknownSourcesDisabled(isDisabled: Boolean) = changeDeviceOwnerPolicy {
        toggleUserRestriction(UserManager.DISALLOW_INSTALL_UNKNOWN_SOURCES, isDisabled)
    }

    private suspend fun toggleUserRestriction(restrictionKey: String, isEnabled: Boolean) {
        if (isEnabled) {
            devicePolicyManager.addUserRestriction(deviceOwnerReceiver, restrictionKey)
        } else {
            devicePolicyManager.clearUserRestriction(deviceOwnerReceiver, restrictionKey)
        }
    }

    private suspend fun changeDeviceOwnerPolicy(block: suspend DevicePolicyManager.() -> Unit) {
        if (!checkIsAppDeviceOwner()) throw AppNotDeviceOwnerException()
        devicePolicyManager.block()
        updateDeviceOwnerState()
    }

    private fun checkIsAppDeviceOwner() = devicePolicyManager.isDeviceOwnerApp(context.packageName)
    private fun checkIsCameraDisabled() = try {
        devicePolicyManager.getCameraDisabled(deviceOwnerReceiver)
    } catch (_: SecurityException) {
        false
    }
    private fun checkIsUSBDataSignalDisabled() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        !devicePolicyManager.isUsbDataSignalingEnabled
    } else {
        false
    }

    private fun <T> MutableStateFlow<T>.asAndroidDeviceOwnerFlow(): Flow<T> =
        asStateFlow().onSubscription { updateDeviceOwnerState() }
}