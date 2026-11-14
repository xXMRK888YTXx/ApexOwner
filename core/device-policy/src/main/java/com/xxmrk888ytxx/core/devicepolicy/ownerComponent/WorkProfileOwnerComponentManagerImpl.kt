package com.xxmrk888ytxx.core.devicepolicy.ownerComponent

import android.app.PendingIntent
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.LauncherApps
import android.content.pm.PackageInstaller
import android.content.pm.PackageManager
import android.os.Build
import android.os.Process
import android.os.UserHandle
import android.os.UserManager
import com.xxmrk888ytxx.core.base.android.logs.Logger
import com.xxmrk888ytxx.core.devicepolicy.WorkProfileOwnerComponentManager
import com.xxmrk888ytxx.core.devicepolicy.exception.AppNotProfileOwnerException
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onSubscription
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class WorkProfileOwnerComponentManagerImpl @Inject constructor(
    devicePolicyManager: DevicePolicyManager,
    @ApplicationContext context: Context
) : BaseOwnerComponent(devicePolicyManager, context), WorkProfileOwnerComponentManager {

    private val userManager: UserManager? by lazy {
        context.getSystemService(Context.USER_SERVICE) as? UserManager
    }

    private val launcherApps: LauncherApps? by lazy {
        context.getSystemService(Context.LAUNCHER_APPS_SERVICE) as? LauncherApps
    }

    private val _isProfileOwner = MutableStateFlow(checkIsAppProfileOwner())
    private val _hasWorkProfile = MutableStateFlow(checkHasWorkProfile())
    private val _isWorkProfileQuietMode = MutableStateFlow(checkIsWorkProfileQuietMode())

    override val isProfileOwner: Flow<Boolean> = _isProfileOwner
        .asStateFlow()
        .onSubscription { updateProfileOwnerState() }

    override val hasWorkProfile: Flow<Boolean> = _hasWorkProfile
        .asStateFlow()
        .onSubscription { updateProfileOwnerState() }

    override val isWorkProfileQuietMode: Flow<Boolean> = _isWorkProfileQuietMode
        .asStateFlow()
        .onSubscription { updateProfileOwnerState() }

    override val isAppDeviceOwner: Boolean
        get() = checkIsAppDeviceOwner()

    override val isInsideWorkProfile: Boolean
        get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            userManager?.isManagedProfile ?: false
        } else {
            checkIsAppProfileOwner() && !checkIsAppDeviceOwner()
        }

    override val workProfileCreateIntent: Intent
        get() = Intent(DevicePolicyManager.ACTION_PROVISION_MANAGED_PROFILE).apply {
            putExtra(
                DevicePolicyManager.EXTRA_PROVISIONING_DEVICE_ADMIN_COMPONENT_NAME,
                deviceOwnerReceiver
            )
            putExtra(DevicePolicyManager.EXTRA_PROVISIONING_LEAVE_ALL_SYSTEM_APPS_ENABLED, true)
        }

    override suspend fun updateProfileOwnerState() {
        _isProfileOwner.value = checkIsAppProfileOwner()
        _hasWorkProfile.value = checkHasWorkProfile()
        _isWorkProfileQuietMode.value = checkIsWorkProfileQuietMode()
        setupCrossProfileFilters()
    }

    override fun setupCrossProfileFilters() {
        if (checkCanManageOwnerPolicies()) {
            try {
                try {
                    devicePolicyManager.clearUserRestriction(deviceOwnerReceiver, UserManager.DISALLOW_INSTALL_UNKNOWN_SOURCES)
                    devicePolicyManager.clearUserRestriction(deviceOwnerReceiver, UserManager.DISALLOW_INSTALL_APPS)
                    Logger.writeDebugLog("Cleared install restrictions in setupCrossProfileFilters")
                } catch (e: Exception) {
                    Logger.writeErrorLog("Failed to clear install restrictions: ${e.message}")
                }

                try {
                    context.packageManager.setComponentEnabledSetting(
                        ComponentName(context, "com.xxmrk888ytxx.apexowner.bridge.WorkProfileDispatcherActivity"),
                        PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                        PackageManager.DONT_KILL_APP
                    )
                } catch (e: Exception) {
                    Logger.writeErrorLog("Failed to enable dispatcher in work profile: ${e.message}")
                }

                devicePolicyManager.clearCrossProfileIntentFilters(deviceOwnerReceiver)
                listOf(
                    "com.xxmrk888ytxx.apexowner.ACTION_CLONE_APP",
                    "com.xxmrk888ytxx.apexowner.ACTION_CLONE_TO_PERSONAL",
                    "com.xxmrk888ytxx.apexowner.ACTION_FREEZE_APP",
                    "com.xxmrk888ytxx.apexowner.ACTION_UNFREEZE_APP",
                    "com.xxmrk888ytxx.apexowner.ACTION_REMOVE_WORK_PROFILE",
                    "com.xxmrk888ytxx.apexowner.ACTION_OPEN_RESTRICTIONS",
                    "com.xxmrk888ytxx.apexowner.ACTION_OPEN_PERMISSIONS"
                ).forEach { actionStr ->
                    val filter = IntentFilter(actionStr).apply {
                        addCategory(Intent.CATEGORY_DEFAULT)
                    }
                    devicePolicyManager.addCrossProfileIntentFilter(
                        deviceOwnerReceiver,
                        filter,
                        DevicePolicyManager.FLAG_PARENT_CAN_ACCESS_MANAGED or DevicePolicyManager.FLAG_MANAGED_CAN_ACCESS_PARENT
                    )
                }
                Logger.writeDebugLog("Cross profile intent filter registered successfully")
            } catch (e: Exception) {
                Logger.writeErrorLog("Failed to register cross profile intent filter: ${e.message}")
            }
        } else {
            try {
                context.packageManager.setComponentEnabledSetting(
                    ComponentName(context, "com.xxmrk888ytxx.apexowner.bridge.WorkProfileDispatcherActivity"),
                    PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                    PackageManager.DONT_KILL_APP
                )
            } catch (e: Exception) {
                Logger.writeErrorLog("Failed to disable dispatcher in host: ${e.message}")
            }
        }
    }

    override suspend fun removeWorkProfile(): Result<Unit> {
        if (!checkCanManageOwnerPolicies()) {
            return Result.failure(AppNotProfileOwnerException())
        }
        return try {
            devicePolicyManager.wipeData(0)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getWorkProfileUserHandle(): UserHandle? {
        val myUser = Process.myUserHandle()
        return launcherApps?.profiles?.firstOrNull { it != myUser }
    }

    override suspend fun installExistingPackage(
        packageName: String,
        apkPath: String?,
        splitPaths: Array<String>?
    ): Result<Unit> {
        if (!checkCanManageOwnerPolicies()) {
            return Result.failure(AppNotProfileOwnerException())
        }
        return try {
            try {
                devicePolicyManager.enableSystemApp(deviceOwnerReceiver, packageName)
                Logger.writeDebugLog("enableSystemApp succeeded for $packageName")
                return Result.success(Unit)
            } catch (e: Exception) {
                Logger.writeDebugLog("enableSystemApp not applicable or failed for $packageName: ${e.message}")
            }

            try {
                val installed = devicePolicyManager.installExistingPackage(deviceOwnerReceiver, packageName)
                if (installed) {
                    Logger.writeDebugLog("installExistingPackage succeeded for $packageName")
                    return Result.success(Unit)
                }
            } catch (e: Exception) {
                Logger.writeDebugLog("installExistingPackage failed for $packageName: ${e.message}")
            }

            installViaPackageInstaller(packageName, apkPath, splitPaths)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun installViaPackageInstaller(
        packageName: String,
        explicitApkPath: String?,
        explicitSplitPaths: Array<String>?
    ): Result<Unit> {
        return try {
            val pm = context.packageManager
            val sourceDir = explicitApkPath ?: try {
                pm.getApplicationInfo(packageName, PackageManager.MATCH_UNINSTALLED_PACKAGES).sourceDir
            } catch (_: Exception) {
                null
            }

            if (sourceDir == null) {
                return Result.failure(IllegalStateException("Cannot find source APK for $packageName"))
            }

            val packageInstaller = pm.packageInstaller
            val params = PackageInstaller.SessionParams(PackageInstaller.SessionParams.MODE_FULL_INSTALL)
            params.setAppPackageName(packageName)
            val sessionId = packageInstaller.createSession(params)
            val session = packageInstaller.openSession(sessionId)

            val baseFile = File(sourceDir)
            baseFile.inputStream().use { input ->
                session.openWrite("base.apk", 0, baseFile.length()).use { output ->
                    input.copyTo(output)
                }
            }

            val splits = explicitSplitPaths ?: try {
                pm.getApplicationInfo(packageName, PackageManager.MATCH_UNINSTALLED_PACKAGES).splitSourceDirs
            } catch (_: Exception) {
                null
            }

            splits?.forEachIndexed { index, splitPath ->
                val splitFile = File(splitPath)
                if (splitFile.exists()) {
                    splitFile.inputStream().use { input ->
                        session.openWrite("split_$index.apk", 0, splitFile.length()).use { output ->
                            input.copyTo(output)
                        }
                    }
                }
            }

            val callbackIntent = Intent().apply {
                component = ComponentName(context, "com.xxmrk888ytxx.apexowner.bridge.PackageInstallerReceiver")
                action = "com.xxmrk888ytxx.apexowner.ACTION_INSTALL_CALLBACK"
            }
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                sessionId,
                callbackIntent,
                PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )

            session.commit(pendingIntent.intentSender)
            session.close()
            Logger.writeDebugLog("PackageInstaller session committed for $packageName")
            Result.success(Unit)
        } catch (e: Exception) {
            Logger.writeErrorLog("PackageInstaller failed for $packageName: ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun setApplicationHidden(packageName: String, isHidden: Boolean): Result<Unit> {
        if (!checkCanManageOwnerPolicies()) {
            return Result.failure(AppNotProfileOwnerException())
        }
        return try {
            val success = devicePolicyManager.setApplicationHidden(deviceOwnerReceiver, packageName, isHidden)
            if (success) {
                Result.success(Unit)
            } else {
                Result.failure(IllegalStateException("Failed to set hidden=$isHidden for $packageName"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun isApplicationHidden(packageName: String): Boolean {
        return try {
            devicePolicyManager.isApplicationHidden(deviceOwnerReceiver, packageName)
        } catch (_: Exception) {
            false
        }
    }

    override suspend fun setPackageSuspended(packageName: String, isSuspended: Boolean): Result<Unit> {
        if (!checkCanManageOwnerPolicies()) {
            return Result.failure(AppNotProfileOwnerException())
        }
        return try {
            val failedPackages = devicePolicyManager.setPackagesSuspended(
                deviceOwnerReceiver,
                arrayOf(packageName),
                isSuspended
            )
            if (failedPackages.isEmpty()) {
                Result.success(Unit)
            } else {
                Result.failure(IllegalStateException("Failed to suspend package: $packageName"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun isPackageSuspended(packageName: String): Boolean {
        return try {
            devicePolicyManager.isPackageSuspended(deviceOwnerReceiver, packageName)
        } catch (_: Exception) {
            false
        }
    }

    override suspend fun setWorkProfileQuietMode(isQuietMode: Boolean): Result<Unit> {
        val handle = getWorkProfileUserHandle()
            ?: return Result.failure(IllegalStateException("No work profile found"))
        val mgr = userManager
            ?: return Result.failure(IllegalStateException("UserManager not available"))
        return try {
            mgr.requestQuietModeEnabled(isQuietMode, handle)
            _isWorkProfileQuietMode.value = isQuietMode
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun checkHasWorkProfile(): Boolean {
        val myUser = Process.myUserHandle()
        return launcherApps?.profiles?.any { it != myUser } ?: false
    }

    private fun checkIsWorkProfileQuietMode(): Boolean {
        val handle = getWorkProfileUserHandle() ?: return false
        return userManager?.isQuietModeEnabled(handle) ?: false
    }
}
