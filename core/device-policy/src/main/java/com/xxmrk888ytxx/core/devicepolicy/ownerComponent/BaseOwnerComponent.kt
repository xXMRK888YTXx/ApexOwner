package com.xxmrk888ytxx.core.devicepolicy.ownerComponent

import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import com.xxmrk888ytxx.core.devicepolicy.ApexDeviceOwnerReceiver

internal abstract class BaseOwnerComponent(
    protected val devicePolicyManager: DevicePolicyManager,
    protected val context: Context
) {
    protected fun checkIsAppProfileOwner() = devicePolicyManager.isProfileOwnerApp(context.packageName)

    protected val deviceOwnerReceiver: ComponentName by lazy {
        ComponentName(context, ApexDeviceOwnerReceiver::class.java)
    }
}