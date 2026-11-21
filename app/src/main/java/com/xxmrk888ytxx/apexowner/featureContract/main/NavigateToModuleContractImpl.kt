package com.xxmrk888ytxx.apexowner.featureContract.main

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.LauncherApps
import com.xxmrk888ytxx.apexowner.core.navigation.Screen
import com.xxmrk888ytxx.apexowner.domain.NavigationManager
import com.xxmrk888ytxx.apexowner.view.mainActivity.MainActivity
import com.xxmrk888ytxx.core.devicepolicy.ProfileOwnerManager
import com.xxmrk888ytxx.feature.main.contract.NavigateToModuleContract
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class NavigateToModuleContractImpl @Inject constructor(
    private val navigationManager: NavigationManager,
    private val profileOwnerManager: ProfileOwnerManager,
    @ApplicationContext private val context: Context
) : NavigateToModuleContract {
    override suspend fun toDeviceRestriction() {
        if (!profileOwnerManager.isInsideWorkProfile && !profileOwnerManager.isAppDeviceOwner) {
            val workUser = profileOwnerManager.getWorkProfileUserHandle()
            if (workUser != null) {
                val intent = Intent("com.xxmrk888ytxx.apexowner.ACTION_OPEN_RESTRICTIONS").apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                try {
                    context.startActivity(intent)
                    return
                } catch (_: Exception) {
                    val launcherApps = context.getSystemService(Context.LAUNCHER_APPS_SERVICE) as? LauncherApps
                    launcherApps?.startMainActivity(
                        ComponentName(context, MainActivity::class.java),
                        workUser,
                        null,
                        null
                    )
                    return
                }
            }
        }
        navigationManager.navigate(Screen.DeviceRestrictionModuleScreen)
    }

    override suspend fun toWorkProfileManagement() {
        navigationManager.navigate(Screen.WorkProfileModuleScreen)
    }

    override suspend fun toGlobalPermissions() {
        if (!profileOwnerManager.isInsideWorkProfile && !profileOwnerManager.isAppDeviceOwner) {
            val workUser = profileOwnerManager.getWorkProfileUserHandle()
            if (workUser != null) {
                val intent = Intent("com.xxmrk888ytxx.apexowner.ACTION_OPEN_PERMISSIONS").apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                try {
                    context.startActivity(intent)
                    return
                } catch (_: Exception) {
                    val launcherApps =
                        context.getSystemService(Context.LAUNCHER_APPS_SERVICE) as? LauncherApps
                    launcherApps?.startMainActivity(
                        ComponentName(context, MainActivity::class.java),
                        workUser,
                        null,
                        null
                    )
                    return
                }
            }
        }
        navigationManager.navigate(Screen.GlobalPermissionModuleScreen)
    }

    override suspend fun toLocationSpoof() {
        navigationManager.navigate(Screen.LocationSpoofScreen)
    }
}
