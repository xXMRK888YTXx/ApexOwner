package com.xxmrk888ytxx.core.devicepolicy.ownerComponent

import android.app.admin.DevicePolicyManager
import android.content.Context
import android.content.Intent
import com.xxmrk888ytxx.core.devicepolicy.WorkProfileOwnerComponentManager
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class WorkProfileOwnerComponentManagerImpl @Inject constructor(
    devicePolicyManager: DevicePolicyManager,
    @ApplicationContext context: Context
) : BaseOwnerComponent(devicePolicyManager, context), WorkProfileOwnerComponentManager {
    override val workProfileCreateIntent: Intent
        get() = Intent(DevicePolicyManager.ACTION_PROVISION_MANAGED_PROFILE).apply {
            putExtra(
                DevicePolicyManager.EXTRA_PROVISIONING_DEVICE_ADMIN_COMPONENT_NAME,
                deviceOwnerReceiver
            )
            putExtra(DevicePolicyManager.EXTRA_PROVISIONING_LEAVE_ALL_SYSTEM_APPS_ENABLED, true)
        }
}