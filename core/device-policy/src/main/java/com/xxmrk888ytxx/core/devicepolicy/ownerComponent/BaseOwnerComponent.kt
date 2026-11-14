package com.xxmrk888ytxx.core.devicepolicy.ownerComponent

import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import com.xxmrk888ytxx.core.devicepolicy.ApexDeviceOwnerReceiver

internal abstract class BaseOwnerComponent(
    protected val devicePolicyManager: DevicePolicyManager,
    protected val context: Context
) {
    protected fun checkIsAppDeviceOwner(): Boolean =
        devicePolicyManager.isDeviceOwnerApp(context.packageName)

    protected fun checkIsAppProfileOwner(): Boolean =
        devicePolicyManager.isProfileOwnerApp(context.packageName)

    protected fun checkCanManageOwnerPolicies(): Boolean =
        checkIsAppDeviceOwner() || checkIsAppProfileOwner()

    protected val deviceOwnerReceiver: ComponentName by lazy {
        ComponentName(context, ApexDeviceOwnerReceiver::class.java)
    }
}
