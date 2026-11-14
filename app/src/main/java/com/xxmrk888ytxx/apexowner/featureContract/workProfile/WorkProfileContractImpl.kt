package com.xxmrk888ytxx.apexowner.featureContract.workProfile

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.CrossProfileApps
import android.content.pm.LauncherApps
import android.content.pm.PackageManager
import com.xxmrk888ytxx.apexowner.bridge.WorkProfileDispatcherActivity
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
    private val profileOwnerManager: ProfileOwnerManager
) : WorkProfileContract {

    private val packageManager: PackageManager = context.packageManager
    private val launcherApps: LauncherApps? =
        context.getSystemService(Context.LAUNCHER_APPS_SERVICE) as? LauncherApps
    private val crossProfileApps: CrossProfileApps? =
        context.getSystemService(Context.CROSS_PROFILE_APPS_SERVICE) as? CrossProfileApps

    private val _workProfileApps = MutableStateFlow<List<WorkProfileApp>>(emptyList())
    override val workProfileApps: Flow<List<WorkProfileApp>> = _workProfileApps.asStateFlow()

    private val _personalApps = MutableStateFlow<List<WorkProfileApp>>(emptyList())
    override val personalApps: Flow<List<WorkProfileApp>> = _personalApps.asStateFlow()

    override val isInsideWorkProfile: Boolean
        get() = profileOwnerManager.isInsideWorkProfile

    override val hasWorkProfile: Flow<Boolean> = profileOwnerManager.hasWorkProfile

    override suspend fun refresh() = withContext(Dispatchers.IO) {
        profileOwnerManager.updateProfileOwnerState()
        profileOwnerManager.setupCrossProfileFilters()
        val workApps = queryWorkProfileApps()
        _workProfileApps.value = workApps

        val workPackages = workApps.map { it.packageName }.toSet()
        _personalApps.value = queryPersonalApps(workPackages)
    }

    override suspend fun toggleFreeze(packageName: String, shouldFreeze: Boolean): Result<Unit> =
        withContext(Dispatchers.IO) {
            if (profileOwnerManager.isInsideWorkProfile) {
                val result = profileOwnerManager.setApplicationHidden(packageName, shouldFreeze)
                if (result.isSuccess) {
                    refresh()
                }
                result
            } else {
                val action = if (shouldFreeze) {
                    WorkProfileDispatcherActivity.ACTION_FREEZE_APP
                } else {
                    WorkProfileDispatcherActivity.ACTION_UNFREEZE_APP
                }
                try {
                    val intent = Intent(action).apply {
                        addCategory(Intent.CATEGORY_DEFAULT)
                        putExtra(WorkProfileDispatcherActivity.EXTRA_PACKAGE_NAME, packageName)
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    context.startActivity(intent)
                    delay(500)
                    refresh()
                    Result.success(Unit)
                } catch (e: Exception) {
                    Logger.writeErrorLog("Failed to dispatch freeze intent: ${e.message}")
                    Result.failure(e)
                }
            }
        }

    override suspend fun cloneApp(packageName: String): Result<Unit> = withContext(Dispatchers.IO) {
        if (profileOwnerManager.isInsideWorkProfile) {
            val result = profileOwnerManager.installExistingPackage(packageName)
            if (result.isSuccess) {
                refresh()
            }
            result
        } else {
            val workUser = profileOwnerManager.getWorkProfileUserHandle()
            if (workUser != null) {
                try {
                    val appInfo = try {
                        packageManager.getApplicationInfo(packageName, 0)
                    } catch (_: Exception) {
                        null
                    }

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

    override suspend fun launchApp(packageName: String): Result<Unit> = withContext(Dispatchers.IO) {
        val workUser = profileOwnerManager.getWorkProfileUserHandle()
        if (!profileOwnerManager.isInsideWorkProfile && workUser != null && launcherApps != null) {
            try {
                val activities = launcherApps.getActivityList(packageName, workUser)
                val firstActivity = activities.firstOrNull()
                if (firstActivity != null) {
                    launcherApps.startMainActivity(firstActivity.componentName, workUser, null, null)
                    Result.success(Unit)
                } else {
                    val launchIntent = packageManager.getLaunchIntentForPackage(packageName)
                    if (launchIntent != null) {
                        launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        context.startActivity(launchIntent)
                        Result.success(Unit)
                    } else {
                        Result.failure(IllegalStateException("No launch intent found for $packageName in work profile"))
                    }
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        } else {
            try {
                if (profileOwnerManager.isApplicationHidden(packageName)) {
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
        return if (profileOwnerManager.isInsideWorkProfile) {
            val installed = packageManager.getInstalledApplications(0)
            installed
                .filter { it.packageName != myPackage }
                .filter { (it.flags and ApplicationInfo.FLAG_INSTALLED) != 0 }
                .map { appInfo ->
                    val isHidden = profileOwnerManager.isApplicationHidden(appInfo.packageName) ||
                        profileOwnerManager.isPackageSuspended(appInfo.packageName)
                    val isSystem = (appInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0
                    WorkProfileApp(
                        packageName = appInfo.packageName,
                        name = appInfo.loadLabel(packageManager).toString(),
                        isSystem = isSystem,
                        isFrozen = isHidden,
                        isInstalledInWorkProfile = true
                    )
                }
                .sortedBy { it.name.lowercase() }
        } else {
            val workUser = profileOwnerManager.getWorkProfileUserHandle() ?: return emptyList()
            val activities = launcherApps?.getActivityList(null, workUser) ?: emptyList()
            activities
                .map { it.applicationInfo }
                .distinctBy { it.packageName }
                .filter { it.packageName != myPackage }
                .map { appInfo ->
                    val isSystem = (appInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0
                    WorkProfileApp(
                        packageName = appInfo.packageName,
                        name = appInfo.loadLabel(packageManager).toString(),
                        isSystem = isSystem,
                        isFrozen = false,
                        isInstalledInWorkProfile = true
                    )
                }
                .sortedBy { it.name.lowercase() }
        }
    }

    private fun queryPersonalApps(installedInWorkProfile: Set<String>): List<WorkProfileApp> {
        val myPackage = context.packageName
        return if (profileOwnerManager.isInsideWorkProfile) {
            val allApps = packageManager.getInstalledApplications(PackageManager.MATCH_UNINSTALLED_PACKAGES)
            allApps
                .filter { it.packageName != myPackage }
                .map { appInfo ->
                    val isInstalledInThisProfile = (appInfo.flags and ApplicationInfo.FLAG_INSTALLED) != 0
                    val isSystem = (appInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0
                    WorkProfileApp(
                        packageName = appInfo.packageName,
                        name = appInfo.loadLabel(packageManager).toString(),
                        isSystem = isSystem,
                        isFrozen = false,
                        isInstalledInWorkProfile = isInstalledInThisProfile
                    )
                }
                .sortedBy { it.name.lowercase() }
        } else {
            val installed = packageManager.getInstalledApplications(0)
            installed
                .filter { it.packageName != myPackage }
                .map { appInfo ->
                    val isSystem = (appInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0
                    WorkProfileApp(
                        packageName = appInfo.packageName,
                        name = appInfo.loadLabel(packageManager).toString(),
                        isSystem = isSystem,
                        isFrozen = false,
                        isInstalledInWorkProfile = installedInWorkProfile.contains(appInfo.packageName)
                    )
                }
                .sortedBy { it.name.lowercase() }
        }
    }
}
