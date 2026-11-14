package com.xxmrk888ytxx.apexowner.bridge

import android.content.ContentProvider
import android.content.ContentValues
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import com.xxmrk888ytxx.core.base.android.logs.Logger
import com.xxmrk888ytxx.core.devicepolicy.OneShotAppManagerOwnerComponent
import com.xxmrk888ytxx.core.devicepolicy.ProfileOwnerManager
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class ApexBridgeProvider : ContentProvider() {

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface BridgeEntryPoint {
        fun profileOwnerManager(): ProfileOwnerManager
        fun oneShotAppManagerOwnerComponent(): OneShotAppManagerOwnerComponent
    }

    override fun onCreate(): Boolean = true

    override fun call(method: String, arg: String?, extras: Bundle?): Bundle? {
        val appContext = context?.applicationContext ?: return null
        val entryPoint = EntryPointAccessors.fromApplication(appContext, BridgeEntryPoint::class.java)
        val profileOwnerManager = entryPoint.profileOwnerManager()
        val pm = appContext.packageManager

        val response = Bundle()
        when (method) {
            METHOD_PING -> {
                response.putBoolean(EXTRA_SUCCESS, true)
            }
            METHOD_CLONE_APP -> {
                val packageName = arg ?: return null
                val apkPath = extras?.getString(EXTRA_APK_PATH)
                val splitPaths = extras?.getStringArray(EXTRA_SPLIT_PATHS)
                Logger.writeDebugLog("ApexBridgeProvider: received clone request for $packageName")
                val result = runBlocking {
                    profileOwnerManager.installExistingPackage(packageName, apkPath, splitPaths)
                }
                response.putBoolean(EXTRA_SUCCESS, result.isSuccess)
                if (result.isFailure) {
                    val errorMsg = result.exceptionOrNull()?.message ?: "Unknown error"
                    Logger.writeErrorLog("ApexBridgeProvider: failed to clone $packageName: $errorMsg")
                    response.putString(EXTRA_ERROR, errorMsg)
                } else {
                    Logger.writeDebugLog("ApexBridgeProvider: successfully cloned $packageName")
                }
            }
            METHOD_FREEZE_APP -> {
                val packageName = arg ?: return null
                Logger.writeDebugLog("ApexBridgeProvider: received freeze request for $packageName")
                val result = runBlocking {
                    profileOwnerManager.setApplicationHidden(packageName, true)
                }
                response.putBoolean(EXTRA_SUCCESS, result.isSuccess)
                if (result.isFailure) {
                    response.putString(EXTRA_ERROR, result.exceptionOrNull()?.message)
                }
            }
            METHOD_UNFREEZE_APP -> {
                val packageName = arg ?: return null
                Logger.writeDebugLog("ApexBridgeProvider: received unfreeze request for $packageName")
                val result = runBlocking {
                    profileOwnerManager.setApplicationHidden(packageName, false)
                }
                response.putBoolean(EXTRA_SUCCESS, result.isSuccess)
                if (result.isFailure) {
                    response.putString(EXTRA_ERROR, result.exceptionOrNull()?.message)
                }
            }
            METHOD_GET_WORK_APPS -> {
                try {
                    val launcherIntent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)
                    val launcherPackages = pm.queryIntentActivities(launcherIntent, 0)
                        .mapNotNull { it.activityInfo?.packageName }
                        .toSet()
                    val myPackage = appContext.packageName

                    val installed = pm.getInstalledApplications(0)
                    val appLines = installed
                        .filter { it.packageName != myPackage }
                        .filter { (it.flags and ApplicationInfo.FLAG_INSTALLED) != 0 }
                        .map { appInfo ->
                            val pkg = appInfo.packageName
                            val isHidden = profileOwnerManager.isApplicationHidden(pkg) ||
                                profileOwnerManager.isPackageSuspended(pkg)
                            val isSystem = (appInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0
                            val hasLauncher = launcherPackages.contains(pkg)
                            "$pkg\t${appInfo.loadLabel(pm)}\t$isSystem\t$isHidden\ttrue\t$hasLauncher"
                        }
                    response.putStringArrayList(EXTRA_APPS, ArrayList(appLines))
                    response.putBoolean(EXTRA_SUCCESS, true)
                } catch (e: Exception) {
                    Logger.writeErrorLog("ApexBridgeProvider: failed to get work apps: ${e.message}")
                    response.putBoolean(EXTRA_SUCCESS, false)
                    response.putString(EXTRA_ERROR, e.message)
                }
            }
            METHOD_GET_PERSONAL_APPS -> {
                try {
                    val launcherIntent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)
                    val launcherPackages = pm.queryIntentActivities(launcherIntent, 0)
                        .mapNotNull { it.activityInfo?.packageName }
                        .toSet()
                    val myPackage = appContext.packageName

                    val installed = pm.getInstalledApplications(0)
                    val appLines = installed
                        .filter { it.packageName != myPackage }
                        .map { appInfo ->
                            val pkg = appInfo.packageName
                            val isSystem = (appInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0
                            val hasLauncher = launcherPackages.contains(pkg)
                            "$pkg\t${appInfo.loadLabel(pm)}\t$isSystem\tfalse\tfalse\t$hasLauncher"
                        }
                    response.putStringArrayList(EXTRA_APPS, ArrayList(appLines))
                    response.putBoolean(EXTRA_SUCCESS, true)
                } catch (e: Exception) {
                    Logger.writeErrorLog("ApexBridgeProvider: failed to get personal apps: ${e.message}")
                    response.putBoolean(EXTRA_SUCCESS, false)
                    response.putString(EXTRA_ERROR, e.message)
                }
            }
            METHOD_REMOVE_WORK_PROFILE -> {
                val result = runBlocking {
                    profileOwnerManager.removeWorkProfile()
                }
                response.putBoolean(EXTRA_SUCCESS, result.isSuccess)
            }
            METHOD_GET_ONE_SHOT_APPS -> {
                try {
                    val oneShotManager = entryPoint.oneShotAppManagerOwnerComponent()
                    val oneShotPackages = runBlocking { oneShotManager.oneShotAppPackages.first() }
                    val autoFreezePackages = runBlocking { oneShotManager.autoFreezeAppPackages.first() }
                    response.putStringArrayList(EXTRA_ONE_SHOT_PACKAGES, ArrayList(oneShotPackages))
                    response.putStringArrayList(EXTRA_AUTO_FREEZE_PACKAGES, ArrayList(autoFreezePackages))
                    response.putBoolean(EXTRA_SUCCESS, true)
                } catch (e: Exception) {
                    Logger.writeErrorLog("ApexBridgeProvider: failed to get one-shot apps: ${e.message}")
                    response.putBoolean(EXTRA_SUCCESS, false)
                    response.putString(EXTRA_ERROR, e.message)
                }
            }
            METHOD_LAUNCH_ONE_SHOT_APP -> {
                val packageName = arg ?: return null
                try {
                    val oneShotManager = entryPoint.oneShotAppManagerOwnerComponent()
                    val result = runBlocking { oneShotManager.launchOneShotApp(packageName) }
                    response.putBoolean(EXTRA_SUCCESS, result.isSuccess)
                    if (result.isFailure) {
                        response.putString(EXTRA_ERROR, result.exceptionOrNull()?.message)
                    }
                } catch (e: Exception) {
                    Logger.writeErrorLog("ApexBridgeProvider: failed to launch one-shot app $packageName: ${e.message}")
                    response.putBoolean(EXTRA_SUCCESS, false)
                    response.putString(EXTRA_ERROR, e.message)
                }
            }
        }
        return response
    }

    override fun query(uri: Uri, p1: Array<out String>?, p2: String?, p3: Array<out String>?, p4: String?): Cursor? = null
    override fun getType(uri: Uri): String? = null
    override fun insert(uri: Uri, values: ContentValues?): Uri? = null
    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int = 0
    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<out String>?): Int = 0

    companion object {
        const val AUTHORITY = "com.xxmrk888ytxx.apexowner.bridge"
        const val METHOD_CLONE_APP = "cloneApp"
        const val METHOD_FREEZE_APP = "freezeApp"
        const val METHOD_UNFREEZE_APP = "unfreezeApp"
        const val METHOD_GET_WORK_APPS = "getWorkApps"
        const val METHOD_GET_PERSONAL_APPS = "getPersonalApps"
        const val METHOD_REMOVE_WORK_PROFILE = "removeWorkProfile"
        const val METHOD_GET_ONE_SHOT_APPS = "getOneShotApps"
        const val METHOD_LAUNCH_ONE_SHOT_APP = "launchOneShotApp"
        const val METHOD_PING = "ping"
        const val EXTRA_SUCCESS = "extra_success"
        const val EXTRA_ERROR = "extra_error"
        const val EXTRA_APPS = "extra_apps"
        const val EXTRA_ONE_SHOT_PACKAGES = "EXTRA_ONE_SHOT_PACKAGES"
        const val EXTRA_AUTO_FREEZE_PACKAGES = "EXTRA_AUTO_FREEZE_PACKAGES"
        const val EXTRA_APK_PATH = "EXTRA_APK_PATH"
        const val EXTRA_SPLIT_PATHS = "EXTRA_SPLIT_PATHS"
    }
}
