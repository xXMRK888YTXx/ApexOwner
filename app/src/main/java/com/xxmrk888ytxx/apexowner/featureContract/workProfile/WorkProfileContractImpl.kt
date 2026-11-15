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
import com.xxmrk888ytxx.apexowner.service.ApexPolicyService
import com.xxmrk888ytxx.apexowner.view.mainActivity.MainActivity
import com.xxmrk888ytxx.core.base.android.logs.Logger
import com.xxmrk888ytxx.core.devicepolicy.OneShotAppManagerOwnerComponent
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
    private val oneShotAppManagerOwnerComponent: OneShotAppManagerOwnerComponent,
    private val navigationManager: NavigationManager
) : WorkProfileContract {

    private val packageManager: PackageManager = context.packageManager
    private val launcherApps: LauncherApps? =
        context.getSystemService(Context.LAUNCHER_APPS_SERVICE) as? LauncherApps
    private val crossProfileApps: CrossProfileApps? =
        context.getSystemService(Context.CROSS_PROFILE_APPS_SERVICE) as? CrossProfileApps

    private val _apps = MutableStateFlow<List<WorkProfileApp>>(emptyList())
    private val crossProfileOverrides = mutableMapOf<String, Boolean>()
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
                    if (!shouldFreeze) {
                        oneShotAppManagerOwnerComponent.recordAppOpened(packageName)
                        ApexPolicyService.triggerInactivityCheck(context)
                    }
                    refresh()
                }
                result
            } else if (profileOwnerManager.isAppDeviceOwner) {
                val result = profileOwnerManager.setApplicationHidden(packageName, shouldFreeze)
                if (result.isSuccess) {
                    if (!shouldFreeze) {
                        oneShotAppManagerOwnerComponent.recordAppOpened(packageName)
                        ApexPolicyService.triggerInactivityCheck(context)
                    }
                    refresh()
                }
                result
            } else {
                Result.failure(SecurityException("Для заморозки приложений требуются права Device Owner"))
            }
        }

    override suspend fun toggleCrossProfile(packageName: String, isAllowed: Boolean): Result<Unit> =
        withContext(Dispatchers.IO) {
            if (profileOwnerManager.isInsideWorkProfile || profileOwnerManager.isAppDeviceOwner) {
                val result = profileOwnerManager.setCrossProfilePackage(packageName, isAllowed)
                if (result.isSuccess) {
                    crossProfileOverrides.remove(packageName)
                    refresh()
                }
                result
            } else {
                try {
                    val intent = Intent(WorkProfileDispatcherActivity.ACTION_TOGGLE_CROSS_PROFILE).apply {
                        addCategory(Intent.CATEGORY_DEFAULT)
                        putExtra(WorkProfileDispatcherActivity.EXTRA_PACKAGE_NAME, packageName)
                        putExtra(WorkProfileDispatcherActivity.EXTRA_IS_CROSS_PROFILE_ALLOWED, isAllowed)
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    context.startActivity(intent)
                    crossProfileOverrides[packageName] = isAllowed
                    delay(300)
                    refresh()
                    Result.success(Unit)
                } catch (e: Exception) {
                    Logger.writeErrorLog("Failed to dispatch cross-profile toggle: ${e.message}")
                    Result.failure(e)
                }
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
            if (oneShotAppManagerOwnerComponent.isOneShotApp(packageName) || oneShotAppManagerOwnerComponent.isAutoFreezeApp(packageName)) {
                val res = oneShotAppManagerOwnerComponent.launchOneShotApp(packageName)
                if (res.isSuccess) {
                    refresh()
                }
                return@withContext res
            }
            if ((profileOwnerManager.isInsideWorkProfile || profileOwnerManager.isAppDeviceOwner) &&
                profileOwnerManager.isApplicationHidden(packageName)
            ) {
                profileOwnerManager.setApplicationHidden(packageName, false)
            }
            oneShotAppManagerOwnerComponent.recordAppOpened(packageName)
            ApexPolicyService.triggerInactivityCheck(context)
            val launchIntent = packageManager.getLaunchIntentForPackage(packageName)
            if (launchIntent != null) {
                launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(launchIntent)
                refresh()
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

                val policyCore = oneShotAppManagerOwnerComponent.getInactivityPolicy(pkg)
                val policyUi = policyCore?.let {
                    com.xxmrk888ytxx.feature.managementmodule.workprofile.model.AppInactivityPolicy(
                        packageName = it.packageName,
                        lockAction = com.xxmrk888ytxx.feature.managementmodule.workprofile.model.LockInactivityAction.valueOf(it.lockAction.name),
                        lockFreezeHours = it.lockFreezeHours,
                        lockWipeHours = it.lockWipeHours,
                        freezeIfUnusedEnabled = it.freezeIfUnusedEnabled,
                        freezeIfUnusedDays = it.freezeIfUnusedDays,
                        freezeIfUnusedHours = it.freezeIfUnusedHours,
                        freezeIfUnusedMinutes = it.freezeIfUnusedMinutes,
                        wipeIfUnusedEnabled = it.wipeIfUnusedEnabled,
                        wipeIfUnusedDays = it.wipeIfUnusedDays,
                        lastOpenedTimestamp = it.lastOpenedTimestamp,
                        lockFreezeMinutes = it.lockFreezeMinutes,
                        lockWipeMinutes = it.lockWipeMinutes,
                        lastLockFreezeTimestamp = it.lastLockFreezeTimestamp,
                        lastLockWipeTimestamp = it.lastLockWipeTimestamp,
                        lastUnusedWipeTimestamp = it.lastUnusedWipeTimestamp,
                        lastUnusedFreezeTimestamp = it.lastUnusedFreezeTimestamp
                    )
                }

                WorkProfileApp(
                    packageName = pkg,
                    name = appInfo.loadLabel(packageManager).toString(),
                    isSystem = isSystem,
                    isFrozen = isFrozen,
                    isInstalledInWorkProfile = true,
                    hasLauncher = hasLauncher,
                    isInstalledInOtherProfile = false,
                    isCrossProfileAllowed = crossProfileOverrides[pkg]
                        ?: profileOwnerManager.isCrossProfilePackage(pkg),
                    isOneShot = oneShotAppManagerOwnerComponent.isOneShotApp(pkg),
                    isAutoFreeze = oneShotAppManagerOwnerComponent.isAutoFreezeApp(pkg),
                    inactivityPolicy = policyUi
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

        // Include hidden/disabled packages so frozen personal-profile apps remain
        // visible and can be unfrozen from the management list.
        val installed = packageManager.getInstalledApplications(
            PackageManager.MATCH_UNINSTALLED_PACKAGES or PackageManager.MATCH_DISABLED_COMPONENTS
        )
        return installed
            .filter { it.packageName != myPackage }
            .map { appInfo ->
                val pkg = appInfo.packageName
                val isSystem = (appInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0
                val hasLauncher = launcherPackages.contains(pkg)
                val isFrozen = try {
                    profileOwnerManager.isApplicationHidden(pkg) || profileOwnerManager.isPackageSuspended(pkg)
                } catch (_: Exception) {
                    false
                }
                val isInstalledInWork = workUser != null && launcherApps != null && try {
                    launcherApps.isPackageEnabled(pkg, workUser) ||
                            launcherApps.getApplicationInfo(pkg, 0, workUser) != null
                } catch (_: Exception) {
                    false
                }

                val policyCore = oneShotAppManagerOwnerComponent.getInactivityPolicy(pkg)
                val policyUi = policyCore?.let {
                    com.xxmrk888ytxx.feature.managementmodule.workprofile.model.AppInactivityPolicy(
                        packageName = it.packageName,
                        lockAction = com.xxmrk888ytxx.feature.managementmodule.workprofile.model.LockInactivityAction.valueOf(it.lockAction.name),
                        lockFreezeHours = it.lockFreezeHours,
                        lockWipeHours = it.lockWipeHours,
                        freezeIfUnusedEnabled = it.freezeIfUnusedEnabled,
                        freezeIfUnusedDays = it.freezeIfUnusedDays,
                        freezeIfUnusedHours = it.freezeIfUnusedHours,
                        freezeIfUnusedMinutes = it.freezeIfUnusedMinutes,
                        wipeIfUnusedEnabled = it.wipeIfUnusedEnabled,
                        wipeIfUnusedDays = it.wipeIfUnusedDays,
                        lastOpenedTimestamp = it.lastOpenedTimestamp,
                        lockFreezeMinutes = it.lockFreezeMinutes,
                        lockWipeMinutes = it.lockWipeMinutes,
                        lastLockFreezeTimestamp = it.lastLockFreezeTimestamp,
                        lastLockWipeTimestamp = it.lastLockWipeTimestamp,
                        lastUnusedWipeTimestamp = it.lastUnusedWipeTimestamp,
                        lastUnusedFreezeTimestamp = it.lastUnusedFreezeTimestamp
                    )
                }

                WorkProfileApp(
                    packageName = pkg,
                    name = appInfo.loadLabel(packageManager).toString(),
                    isSystem = isSystem,
                    isFrozen = isFrozen,
                    isInstalledInWorkProfile = isInstalledInWork,
                    hasLauncher = hasLauncher,
                    isInstalledInOtherProfile = isInstalledInWork,
                    isCrossProfileAllowed = crossProfileOverrides[pkg]
                        ?: profileOwnerManager.isCrossProfilePackage(pkg),
                    isOneShot = oneShotAppManagerOwnerComponent.isOneShotApp(pkg),
                    isAutoFreeze = oneShotAppManagerOwnerComponent.isAutoFreezeApp(pkg),
                    inactivityPolicy = policyUi
                )
            }
            .sortedBy { it.name.lowercase() }
    }

    override suspend fun openAppPermissions(packageName: String, appName: String) {
        navigationManager.navigate(Screen.AppPermissionsModuleScreen(packageName, appName))
    }

    override suspend fun toggleOneShot(packageName: String, isOneShot: Boolean): Result<Unit> =
        withContext(Dispatchers.IO) {
            if (profileOwnerManager.isInsideWorkProfile || profileOwnerManager.isAppDeviceOwner) {
                val result = oneShotAppManagerOwnerComponent.setOneShotApp(packageName, isOneShot)
                if (result.isSuccess) {
                    refresh()
                }
                result
            } else {
                try {
                    val intent = Intent(WorkProfileDispatcherActivity.ACTION_TOGGLE_ONE_SHOT).apply {
                        addCategory(Intent.CATEGORY_DEFAULT)
                        putExtra(WorkProfileDispatcherActivity.EXTRA_PACKAGE_NAME, packageName)
                        putExtra(WorkProfileDispatcherActivity.EXTRA_IS_ONE_SHOT, isOneShot)
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    context.startActivity(intent)
                    delay(500)
                    refresh()
                    Result.success(Unit)
                } catch (e: Exception) {
                    Logger.writeErrorLog("Failed to dispatch toggle one-shot intent: ${e.message}")
                    Result.failure(e)
                }
            }
        }

    override suspend fun resetOneShot(packageName: String): Result<Unit> =
        withContext(Dispatchers.IO) {
            if (profileOwnerManager.isInsideWorkProfile || profileOwnerManager.isAppDeviceOwner) {
                val result = oneShotAppManagerOwnerComponent.resetOneShotApp(packageName)
                if (result.isSuccess) {
                    refresh()
                }
                result
            } else {
                try {
                    val intent = Intent(WorkProfileDispatcherActivity.ACTION_RESET_ONE_SHOT).apply {
                        addCategory(Intent.CATEGORY_DEFAULT)
                        putExtra(WorkProfileDispatcherActivity.EXTRA_PACKAGE_NAME, packageName)
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    context.startActivity(intent)
                    delay(500)
                    refresh()
                    Result.success(Unit)
                } catch (e: Exception) {
                    Logger.writeErrorLog("Failed to dispatch reset one-shot intent: ${e.message}")
                    Result.failure(e)
                }
            }
        }

    override suspend fun toggleAutoFreeze(packageName: String, isAutoFreeze: Boolean): Result<Unit> =
        withContext(Dispatchers.IO) {
            if (profileOwnerManager.isInsideWorkProfile || profileOwnerManager.isAppDeviceOwner) {
                val result = oneShotAppManagerOwnerComponent.setAutoFreezeApp(packageName, isAutoFreeze)
                if (result.isSuccess) {
                    refresh()
                }
                result
            } else {
                try {
                    val intent = Intent(WorkProfileDispatcherActivity.ACTION_TOGGLE_AUTO_FREEZE).apply {
                        addCategory(Intent.CATEGORY_DEFAULT)
                        putExtra(WorkProfileDispatcherActivity.EXTRA_PACKAGE_NAME, packageName)
                        putExtra(WorkProfileDispatcherActivity.EXTRA_IS_AUTO_FREEZE, isAutoFreeze)
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    context.startActivity(intent)
                    delay(500)
                    refresh()
                    Result.success(Unit)
                } catch (e: Exception) {
                    Logger.writeErrorLog("Failed to dispatch toggle auto-freeze intent: ${e.message}")
                    Result.failure(e)
                }
            }
        }

    override suspend fun setInactivityPolicy(
        policy: com.xxmrk888ytxx.feature.managementmodule.workprofile.model.AppInactivityPolicy
    ): Result<Unit> = withContext(Dispatchers.IO) {
        val corePolicy = com.xxmrk888ytxx.core.devicepolicy.model.AppInactivityPolicy(
            packageName = policy.packageName,
            lockAction = com.xxmrk888ytxx.core.devicepolicy.model.LockInactivityAction.valueOf(policy.lockAction.name),
            lockFreezeHours = policy.lockFreezeHours,
            lockWipeHours = policy.lockWipeHours,
            freezeIfUnusedEnabled = policy.freezeIfUnusedEnabled,
            freezeIfUnusedDays = policy.freezeIfUnusedDays,
            freezeIfUnusedHours = policy.freezeIfUnusedHours,
            freezeIfUnusedMinutes = policy.freezeIfUnusedMinutes,
            wipeIfUnusedEnabled = policy.wipeIfUnusedEnabled,
            wipeIfUnusedDays = policy.wipeIfUnusedDays,
            lastOpenedTimestamp = policy.lastOpenedTimestamp,
            lockFreezeMinutes = policy.lockFreezeMinutes,
            lockWipeMinutes = policy.lockWipeMinutes,
            lastLockFreezeTimestamp = policy.lastLockFreezeTimestamp,
            lastLockWipeTimestamp = policy.lastLockWipeTimestamp,
            lastUnusedWipeTimestamp = policy.lastUnusedWipeTimestamp,
            lastUnusedFreezeTimestamp = policy.lastUnusedFreezeTimestamp
        )

        if (profileOwnerManager.isInsideWorkProfile || profileOwnerManager.isAppDeviceOwner) {
            val result = oneShotAppManagerOwnerComponent.setInactivityPolicy(corePolicy)
            if (result.isSuccess) {
                ApexPolicyService.triggerInactivityCheck(context)
                refresh()
            }
            result
        } else {
            try {
                val intent = Intent(WorkProfileDispatcherActivity.ACTION_SET_INACTIVITY_POLICY).apply {
                    addCategory(Intent.CATEGORY_DEFAULT)
                    putExtra(WorkProfileDispatcherActivity.EXTRA_PACKAGE_NAME, policy.packageName)
                    putExtra(WorkProfileDispatcherActivity.EXTRA_POLICY_JSON, corePolicy.toJson())
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(intent)
                delay(500)
                refresh()
                Result.success(Unit)
            } catch (e: Exception) {
                Logger.writeErrorLog("Failed to dispatch set inactivity policy intent: ${e.message}")
                Result.failure(e)
            }
        }
    }
}
