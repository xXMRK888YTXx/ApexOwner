package com.xxmrk888ytxx.core.devicepolicy

import android.app.admin.DeviceAdminReceiver
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.UserHandle
import com.xxmrk888ytxx.core.base.android.logs.Logger
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
internal class ApexDeviceOwnerReceiver : DeviceAdminReceiver() {

    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    @Inject
    lateinit var deviceOwnerManager: DeviceOwnerManager

    @Inject
    lateinit var devicePolicyManager: DevicePolicyManager

    private fun provideDeviceOwnerReceiverComponentName(context: Context): ComponentName =
        ComponentName(context, ApexDeviceOwnerReceiver::class.java)

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == DevicePolicyManager.ACTION_MANAGED_PROFILE_PROVISIONED) {
            Logger.writeDebugLog("onReceive: ACTION_MANAGED_PROFILE_PROVISIONED")
            enableWorkProfile(context)
        } else if (intent.action == Intent.ACTION_MANAGED_PROFILE_UNAVAILABLE || intent.action == Intent.ACTION_SHUTDOWN) {
            Logger.writeDebugLog("onReceive: ${intent.action}, handling triggers")
            scope.launch {
                try {
                    deviceOwnerManager.handleLifecycleTriggers()
                } catch (e: Exception) {
                    Logger.writeErrorLog("Failed to handle triggers on ${intent.action}: ${e.message}")
                }
            }
        }
        super.onReceive(context, intent)
    }

    override fun onEnabled(context: Context, intent: Intent) {
        super.onEnabled(context, intent)
        Logger.writeDebugLog("onEnabled")
        scope.launch { deviceOwnerManager.updateDeviceOwnerState() }
    }

    override fun onDisabled(context: Context, intent: Intent) {
        super.onDisabled(context, intent)
        Logger.writeDebugLog("onDisabled")
        scope.launch { deviceOwnerManager.updateDeviceOwnerState() }
    }

    override fun onProfileProvisioningComplete(context: Context, intent: Intent) {
        super.onProfileProvisioningComplete(context, intent)
        Logger.writeDebugLog("onProfileProvisioningComplete")
        enableWorkProfile(context)
    }

    override fun onUserStopped(context: Context, intent: Intent, stoppedUser: UserHandle) {
        super.onUserStopped(context, intent, stoppedUser)
        Logger.writeDebugLog("onUserStopped: $stoppedUser")
        scope.launch {
            try {
                deviceOwnerManager.handleLifecycleTriggers()
            } catch (e: Exception) {
                Logger.writeErrorLog("Failed to handle triggers in onUserStopped: ${e.message}")
            }
        }
    }

    private fun enableWorkProfile(context: Context) {
        val componentName = provideDeviceOwnerReceiverComponentName(context)
        try {
            val profileName = context.getString(R.string.work_profile_name)
            devicePolicyManager.setProfileName(componentName, profileName)
            devicePolicyManager.setProfileEnabled(componentName)
            Logger.writeDebugLog("Work profile successfully enabled with name: $profileName")

            try {
                devicePolicyManager.clearUserRestriction(componentName, android.os.UserManager.DISALLOW_INSTALL_UNKNOWN_SOURCES)
                devicePolicyManager.clearUserRestriction(componentName, android.os.UserManager.DISALLOW_INSTALL_APPS)
                Logger.writeDebugLog("Cleared DISALLOW_INSTALL_UNKNOWN_SOURCES and DISALLOW_INSTALL_APPS in receiver")
            } catch (e: Exception) {
                Logger.writeErrorLog("Failed to clear install restrictions in receiver: ${e.message}")
            }

            try {
                context.packageManager.setComponentEnabledSetting(
                    ComponentName(context, "com.xxmrk888ytxx.apexowner.bridge.WorkProfileDispatcherActivity"),
                    android.content.pm.PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                    android.content.pm.PackageManager.DONT_KILL_APP
                )
            } catch (e: Exception) {
                Logger.writeErrorLog("Failed to enable dispatcher in receiver: ${e.message}")
            }

            devicePolicyManager.clearCrossProfileIntentFilters(componentName)
            listOf(
                "com.xxmrk888ytxx.apexowner.ACTION_CLONE_APP",
                "com.xxmrk888ytxx.apexowner.ACTION_FREEZE_APP",
                "com.xxmrk888ytxx.apexowner.ACTION_UNFREEZE_APP",
                "com.xxmrk888ytxx.apexowner.ACTION_REMOVE_WORK_PROFILE",
                "com.xxmrk888ytxx.apexowner.ACTION_OPEN_RESTRICTIONS",
                "com.xxmrk888ytxx.apexowner.ACTION_OPEN_PERMISSIONS",
                "com.xxmrk888ytxx.apexowner.ACTION_TOGGLE_ONE_SHOT",
                "com.xxmrk888ytxx.apexowner.ACTION_RESET_ONE_SHOT",
                "com.xxmrk888ytxx.apexowner.ACTION_LAUNCH_APP"
            ).forEach { actionStr ->
                val filter = android.content.IntentFilter(actionStr).apply {
                    addCategory(Intent.CATEGORY_DEFAULT)
                }
                devicePolicyManager.addCrossProfileIntentFilter(
                    componentName,
                    filter,
                    DevicePolicyManager.FLAG_PARENT_CAN_ACCESS_MANAGED or DevicePolicyManager.FLAG_MANAGED_CAN_ACCESS_PARENT
                )
            }
        } catch (e: Exception) {
            Logger.writeErrorLog("Failed to enable work profile: ${e.message}")
        }
        scope.launch { deviceOwnerManager.updatePolicyState() }
    }
}
