package com.xxmrk888ytxx.apexowner.featureContract.workProfile

import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.CrossProfileApps
import android.content.pm.LauncherApps
import android.content.pm.PackageInstaller
import android.content.pm.PackageManager
import android.net.Uri
import com.xxmrk888ytxx.apexowner.bridge.PackageInstallerReceiver
import com.xxmrk888ytxx.apexowner.bridge.WorkProfileDispatcherActivity
import com.xxmrk888ytxx.apexowner.core.navigation.Screen
import com.xxmrk888ytxx.apexowner.domain.NavigationManager
import com.xxmrk888ytxx.apexowner.view.mainActivity.MainActivity
import com.xxmrk888ytxx.core.base.android.logs.Logger
import com.xxmrk888ytxx.core.devicepolicy.ProfileOwnerManager
import com.xxmrk888ytxx.feature.managementmodule.workprofile.contract.WorkProfileContract
import com.xxmrk888ytxx.feature.managementmodule.workprofile.model.WorkProfileApp
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class WorkProfileContractImpl @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val profileOwnerManager: ProfileOwnerManager,
    private val navigationManager: NavigationManager
) : WorkProfileContract {

    private val packageManager: PackageManager = context.packageManager
    private val launcherApps: LauncherApps? =
        context.getSystemService(Context.LAUNCHER_APPS_SERVICE) as? LauncherApps
    private val crossProfileApps: CrossProfileApps? =
        context.getSystemService(Context.CROSS_PROFILE_APPS_SERVICE) as? CrossProfileApps

    private val _apps = MutableStateFlow<List<WorkProfileApp>>(emptyList())
    override val apps: Flow<List<WorkProfileApp>> = _apps.asStateFlow()

    override val workProfileApps: Flow<List<WorkProfileApp>> = apps
    override val personalApps: Flow<List<WorkProfileApp>> = apps

    override val isInsideWorkProfile: Boolean
        get() = profileOwnerManager.isInsideWorkProfile

    override val hasWorkProfile: Flow<Boolean> = profileOwnerManager.hasWorkProfile

    override val isDeviceOwner: Boolean
        get() = profileOwnerManager.isAppDeviceOwner

    override val canFreezeInCurrentProfile: Boolean
        get() = profileOwnerManager.isInsideWorkProfile || profileOwnerManager.isAppDeviceOwner

    override suspend fun refresh() = withContext(Dispatchers.IO) {
        profileOwnerManager.updateProfileOwnerState()
        profileOwnerManager.setupCrossProfileFilters()
        _apps.value = if (profileOwnerManager.isInsideWorkProfile) {
            queryWorkProfileApps()
        } else {
            queryPersonalProfileApps()
        }
    }

    override suspend fun toggleFreeze(packageName: String, shouldFreeze: Boolean): Result<Unit> =
        withContext(Dispatchers.IO) {
            if (profileOwnerManager.isInsideWorkProfile) {
                val result = profileOwnerManager.setApplicationHidden(packageName, shouldFreeze)
                if (result.isSuccess) {
                    refresh()
                }
                result
            } else if (profileOwnerManager.isAppDeviceOwner) {
                val result = profileOwnerManager.setApplicationHidden(packageName, shouldFreeze)
                if (result.isSuccess) {
                    refresh()
                }
                result
            } else {
                Result.failure(SecurityException("Для заморозки приложений требуются права Device Owner"))
            }
        }

    override suspend fun cloneApp(packageName: String): Result<Unit> = withContext(Dispatchers.IO) {
        val appInfo = try {
            packageManager.getApplicationInfo(packageName, 0)
        } catch (_: Exception) {
            null
        }

        if (profileOwnerManager.isInsideWorkProfile) {
            // Clone from Work Profile -> into Personal Profile
            try {
                val intent = Intent(WorkProfileDispatcherActivity.ACTION_CLONE_TO_PERSONAL).apply {
                    addCategory(Intent.CATEGORY_DEFAULT)
                    putExtra(WorkProfileDispatcherActivity.EXTRA_PACKAGE_NAME, packageName)
                    appInfo?.sourceDir?.let {
                        putExtra(WorkProfileDispatcherActivity.EXTRA_APK_PATH, it)
                    }
                    appInfo?.splitSourceDirs?.let {
                        putExtra(WorkProfileDispatcherActivity.EXTRA_SPLIT_PATHS, it)
                    }
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(intent)
                delay(1000)
                refresh()
                Result.success(Unit)
            } catch (e: Exception) {
                Logger.writeErrorLog("Failed to dispatch clone to personal intent: ${e.message}")
                Result.failure(e)
            }
        } else {
            // Clone from Personal Profile -> into Work Profile
            val workUser = profileOwnerManager.getWorkProfileUserHandle()
            if (workUser != null) {
                try {
                    val intent = Intent(WorkProfileDispatcherActivity.ACTION_CLONE_APP).apply {
                        addCategory(Intent.CATEGORY_DEFAULT)
                        putExtra(WorkProfileDispatcherActivity.EXTRA_PACKAGE_NAME, packageName)
                        appInfo?.sourceDir?.let {
                            putExtra(WorkProfileDispatcherActivity.EXTRA_APK_PATH, it)
                        }
                        appInfo?.splitSourceDirs?.let {
                            putExtra(WorkProfileDispatcherActivity.EXTRA_SPLIT_PATHS, it)
                        }
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    context.startActivity(intent)
                    delay(1000)
                    refresh()
                    Result.success(Unit)
                } catch (e: Exception) {
                    Logger.writeErrorLog("Failed to dispatch clone intent: ${e.message}")
                    Result.failure(e)
                }
            } else {
                profileOwnerManager.installExistingPackage(packageName)
            }
        }
    }

    override suspend fun uninstallApp(packageName: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val canUninstallSilently = profileOwnerManager.isInsideWorkProfile || profileOwnerManager.isAppDeviceOwner
            if (canUninstallSilently) {
                try {
                    val packageInstaller = packageManager.packageInstaller
                    val callbackIntent = Intent(context, PackageInstallerReceiver::class.java).apply {
                        action = PackageInstallerReceiver.ACTION_UNINSTALL_CALLBACK
                        putExtra(PackageInstallerReceiver.EXTRA_IS_UNINSTALL, true)
                        putExtra(PackageInstaller.EXTRA_PACKAGE_NAME, packageName)
                    }
                    val pendingIntent = PendingIntent.getBroadcast(
                        context,
                        packageName.hashCode(),
                        callbackIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
                    )
                    packageInstaller.uninstall(packageName, pendingIntent.intentSender)
                    delay(500)
                    refresh()
                    Result.success(Unit)
                } catch (e: Exception) {
                    Logger.writeErrorLog("Silent uninstall failed, falling back to system dialog: ${e.message}")
                    launchSystemUninstallDialog(packageName)
                }
            } else {
                launchSystemUninstallDialog(packageName)
            }
        } catch (e: Exception) {
            Logger.writeErrorLog("Failed to uninstall app $packageName: ${e.message}")
            Result.failure(e)
        }
    }

    private fun launchSystemUninstallDialog(packageName: String): Result<Unit> {
        return try {
            val intent = Intent(Intent.ACTION_DELETE).apply {
                data = Uri.fromParts("package", packageName, null)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            Result.success(Unit)
        } catch (e: Exception) {
            Logger.writeErrorLog("Failed to launch system uninstall dialog for $packageName: ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun launchApp(packageName: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            if (profileOwnerManager.isInsideWorkProfile && profileOwnerManager.isApplicationHidden(packageName)) {
                profileOwnerManager.setApplicationHidden(packageName, false)
            }
            val launchIntent = packageManager.getLaunchIntentForPackage(packageName)
            if (launchIntent != null) {
                launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(launchIntent)
                Result.success(Unit)
            } else {
                Result.failure(IllegalStateException("No launch intent found for $packageName"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun openWorkProfileApp(): Result<Unit> = withContext(Dispatchers.IO) {
        val workUser = profileOwnerManager.getWorkProfileUserHandle()
        if (workUser != null && crossProfileApps != null) {
            try {
                val component = ComponentName(context, MainActivity::class.java)
                crossProfileApps.startMainActivity(component, workUser)
                Result.success(Unit)
            } catch (e: Exception) {
                Logger.writeErrorLog("Failed to open work profile app: ${e.message}")
                Result.failure(e)
            }
        } else {
            Result.failure(IllegalStateException("Work profile not found"))
        }
    }

    override suspend fun removeWorkProfile(): Result<Unit> = withContext(Dispatchers.IO) {
        if (profileOwnerManager.isInsideWorkProfile) {
            profileOwnerManager.removeWorkProfile()
        } else {
            try {
                val intent = Intent(WorkProfileDispatcherActivity.ACTION_REMOVE_WORK_PROFILE).apply {
                    addCategory(Intent.CATEGORY_DEFAULT)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(intent)
                delay(1000)
                profileOwnerManager.updateProfileOwnerState()
                refresh()
                Result.success(Unit)
            } catch (e: Exception) {
                Logger.writeErrorLog("Failed to dispatch remove work profile intent: ${e.message}")
                Result.failure(e)
            }
        }
    }

    private fun queryWorkProfileApps(): List<WorkProfileApp> {
        val myPackage = context.packageName
        val launcherIntent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)
        val launcherPackages = packageManager.queryIntentActivities(launcherIntent, 0)
            .mapNotNull { it.activityInfo?.packageName }
            .toSet()

        val allApps = packageManager.getInstalledApplications(
            PackageManager.MATCH_UNINSTALLED_PACKAGES or PackageManager.MATCH_DISABLED_COMPONENTS
        )

        return allApps
            .filter { it.packageName != myPackage }
            .mapNotNull { appInfo ->
                val pkg = appInfo.packageName
                val isHidden = profileOwnerManager.isApplicationHidden(pkg)
                val isSuspended = profileOwnerManager.isPackageSuspended(pkg)
                val isInstalled = (appInfo.flags and ApplicationInfo.FLAG_INSTALLED) != 0

                if (!isInstalled && !isHidden) {
                    return@mapNotNull null
                }

                val isSystem = (appInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0
                val isFrozen = isHidden || isSuspended

                val hasLauncher = launcherPackages.contains(pkg) || run {
                    val intent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER).setPackage(pkg)
                    packageManager.queryIntentActivities(
                        intent,
                        PackageManager.MATCH_UNINSTALLED_PACKAGES or PackageManager.MATCH_DISABLED_COMPONENTS
                    ).isNotEmpty()
                }

                WorkProfileApp(
                    packageName = pkg,
                    name = appInfo.loadLabel(packageManager).toString(),
                    isSystem = isSystem,
                    isFrozen = isFrozen,
                    isInstalledInWorkProfile = true,
                    hasLauncher = hasLauncher,
                    isInstalledInOtherProfile = false
                )
            }
            .sortedBy { it.name.lowercase() }
    }

    private fun queryPersonalProfileApps(): List<WorkProfileApp> {
        val myPackage = context.packageName
        val workUser = profileOwnerManager.getWorkProfileUserHandle()

        val launcherIntent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)
        val launcherPackages = packageManager.queryIntentActivities(launcherIntent, 0)
            .mapNotNull { it.activityInfo?.packageName }
            .toSet()

        val installed = packageManager.getInstalledApplications(0)
        return installed
            .filter { it.packageName != myPackage }
            .map { appInfo ->
                val pkg = appInfo.packageName
                val isSystem = (appInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0
                val hasLauncher = launcherPackages.contains(pkg)
                val isInstalledInWork = workUser != null && launcherApps != null && try {
                    launcherApps.isPackageEnabled(pkg, workUser) ||
                            launcherApps.getApplicationInfo(pkg, 0, workUser) != null
                } catch (_: Exception) {
                    false
                }

                WorkProfileApp(
                    packageName = pkg,
                    name = appInfo.loadLabel(packageManager).toString(),
                    isSystem = isSystem,
                    isFrozen = false,
                    isInstalledInWorkProfile = isInstalledInWork,
                    hasLauncher = hasLauncher,
                    isInstalledInOtherProfile = isInstalledInWork
                )
            }
            .sortedBy { it.name.lowercase() }
    }

    override suspend fun openAppPermissions(packageName: String, appName: String) {
        navigationManager.navigate(Screen.AppPermissionsModuleScreen(packageName, appName))
    }
}
