package com.xxmrk888ytxx.core.devicepolicy.ownerComponent

import android.app.admin.DevicePolicyManager
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import com.xxmrk888ytxx.core.base.android.logs.Logger
import com.xxmrk888ytxx.core.devicepolicy.OneShotAppManagerOwnerComponent
import com.xxmrk888ytxx.core.devicepolicy.exception.AppNotProfileOwnerException
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import java.util.concurrent.TimeoutException
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

@Singleton
internal class OneShotAppManagerOwnerComponentImpl @Inject constructor(
    devicePolicyManager: DevicePolicyManager,
    @ApplicationContext context: Context
) : BaseOwnerComponent(devicePolicyManager, context), OneShotAppManagerOwnerComponent {

    private val prefs by lazy {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    private val _oneShotAppPackages = MutableStateFlow<Set<String>>(emptySet())
    override val oneShotAppPackages: Flow<Set<String>> = _oneShotAppPackages.asStateFlow()

    private val _autoFreezeAppPackages = MutableStateFlow<Set<String>>(emptySet())
    override val autoFreezeAppPackages: Flow<Set<String>> = _autoFreezeAppPackages.asStateFlow()

    init {
        _oneShotAppPackages.value = getStoredOneShotPackages()
        _autoFreezeAppPackages.value = getStoredAutoFreezePackages()
    }

    private fun getStoredOneShotPackages(): Set<String> {
        return prefs.getStringSet(KEY_ONE_SHOT_PACKAGES, emptySet())?.toSet() ?: emptySet()
    }

    private fun getStoredAutoFreezePackages(): Set<String> {
        return prefs.getStringSet(KEY_AUTO_FREEZE_PACKAGES, emptySet())?.toSet() ?: emptySet()
    }

    override fun isOneShotApp(packageName: String): Boolean {
        return _oneShotAppPackages.value.contains(packageName)
    }

    override fun isAutoFreezeApp(packageName: String): Boolean {
        return _autoFreezeAppPackages.value.contains(packageName)
    }

    override suspend fun setOneShotApp(packageName: String, isOneShot: Boolean): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val oneShotSet = getStoredOneShotPackages().toMutableSet()
            val autoFreezeSet = getStoredAutoFreezePackages().toMutableSet()

            if (isOneShot) {
                oneShotSet.add(packageName)
                autoFreezeSet.remove(packageName) // Mutual exclusivity

                prefs.edit()
                    .putStringSet(KEY_ONE_SHOT_PACKAGES, oneShotSet)
                    .putStringSet(KEY_AUTO_FREEZE_PACKAGES, autoFreezeSet)
                    .apply()

                _oneShotAppPackages.value = oneShotSet.toSet()
                _autoFreezeAppPackages.value = autoFreezeSet.toSet()

                Logger.writeDebugLog("setOneShotApp: enabled for $packageName, freezing app")
                hideApp(packageName)
            } else {
                oneShotSet.remove(packageName)
                prefs.edit().putStringSet(KEY_ONE_SHOT_PACKAGES, oneShotSet).apply()
                _oneShotAppPackages.value = oneShotSet.toSet()

                Logger.writeDebugLog("setOneShotApp: disabled for $packageName")
                Result.success(Unit)
            }
        } catch (e: Exception) {
            Logger.writeErrorLog("setOneShotApp failed for $packageName: ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun setAutoFreezeApp(packageName: String, isAutoFreeze: Boolean): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val oneShotSet = getStoredOneShotPackages().toMutableSet()
            val autoFreezeSet = getStoredAutoFreezePackages().toMutableSet()

            if (isAutoFreeze) {
                autoFreezeSet.add(packageName)
                oneShotSet.remove(packageName) // Mutual exclusivity

                prefs.edit()
                    .putStringSet(KEY_AUTO_FREEZE_PACKAGES, autoFreezeSet)
                    .putStringSet(KEY_ONE_SHOT_PACKAGES, oneShotSet)
                    .apply()

                _autoFreezeAppPackages.value = autoFreezeSet.toSet()
                _oneShotAppPackages.value = oneShotSet.toSet()

                Logger.writeDebugLog("setAutoFreezeApp: enabled for $packageName, freezing app")
                hideApp(packageName)
            } else {
                autoFreezeSet.remove(packageName)
                prefs.edit().putStringSet(KEY_AUTO_FREEZE_PACKAGES, autoFreezeSet).apply()
                _autoFreezeAppPackages.value = autoFreezeSet.toSet()

                Logger.writeDebugLog("setAutoFreezeApp: disabled for $packageName")
                Result.success(Unit)
            }
        } catch (e: Exception) {
            Logger.writeErrorLog("setAutoFreezeApp failed for $packageName: ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun launchOneShotApp(packageName: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            Logger.writeDebugLog("launchOneShotApp: unfreezing $packageName")
            unhideApp(packageName)

            var launchIntent = context.packageManager.getLaunchIntentForPackage(packageName)
            var attempts = 0
            while (launchIntent == null && attempts < 5) {
                delay(100)
                launchIntent = context.packageManager.getLaunchIntentForPackage(packageName)
                attempts++
            }

            if (launchIntent != null) {
                launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(launchIntent)
                Logger.writeDebugLog("launchOneShotApp: successfully launched $packageName")
                Result.success(Unit)
            } else {
                Logger.writeErrorLog("launchOneShotApp: launch intent not found for $packageName")
                Result.failure(IllegalStateException("No launch intent found for $packageName"))
            }
        } catch (e: Exception) {
            Logger.writeErrorLog("launchOneShotApp: failed for $packageName: ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun resetOneShotApp(packageName: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            Logger.writeDebugLog("resetOneShotApp: clearing data and freezing $packageName")
            val clearResult = clearAppData(packageName)
            if (clearResult.isFailure) {
                Logger.writeErrorLog("resetOneShotApp: failed to clear data for $packageName: ${clearResult.exceptionOrNull()?.message}")
            }
            val hideResult = hideApp(packageName)
            if (hideResult.isFailure) {
                Logger.writeErrorLog("resetOneShotApp: failed to hide $packageName: ${hideResult.exceptionOrNull()?.message}")
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Logger.writeErrorLog("resetOneShotApp: exception for $packageName: ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun resetOneShotApps(): Result<Unit> = withContext(Dispatchers.IO) {
        val packages = getStoredOneShotPackages()
        Logger.writeDebugLog("resetOneShotApps: resetting ${packages.size} apps: $packages")
        for (pkg in packages) {
            resetOneShotApp(pkg)
        }
        Result.success(Unit)
    }

    override suspend fun freezeAutoFreezeApp(packageName: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            Logger.writeDebugLog("freezeAutoFreezeApp: freezing $packageName without clearing data")
            hideApp(packageName)
        } catch (e: Exception) {
            Logger.writeErrorLog("freezeAutoFreezeApp failed for $packageName: ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun freezeAutoFreezeApps(): Result<Unit> = withContext(Dispatchers.IO) {
        val packages = getStoredAutoFreezePackages()
        Logger.writeDebugLog("freezeAutoFreezeApps: freezing ${packages.size} apps without clearing data: $packages")
        for (pkg in packages) {
            freezeAutoFreezeApp(pkg)
        }
        Result.success(Unit)
    }

    override suspend fun handleLifecycleTriggers(): Result<Unit> = withContext(Dispatchers.IO) {
        Logger.writeDebugLog("handleLifecycleTriggers: executing reset for OneShot apps and freezing for AutoFreeze apps")
        resetOneShotApps()
        freezeAutoFreezeApps()
        Result.success(Unit)
    }

    private suspend fun hideApp(packageName: String): Result<Unit> {
        if (!checkCanManageOwnerPolicies()) return Result.failure(AppNotProfileOwnerException())
        return try {
            val ok = devicePolicyManager.setApplicationHidden(deviceOwnerReceiver, packageName, true)
            if (ok) Result.success(Unit) else Result.failure(IllegalStateException("Failed to hide $packageName"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun unhideApp(packageName: String): Result<Unit> {
        if (!checkCanManageOwnerPolicies()) return Result.failure(AppNotProfileOwnerException())
        return try {
            val ok = devicePolicyManager.setApplicationHidden(deviceOwnerReceiver, packageName, false)
            if (ok) Result.success(Unit) else Result.failure(IllegalStateException("Failed to unhide $packageName"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun clearAppData(packageName: String): Result<Unit> {
        if (!checkCanManageOwnerPolicies()) return Result.failure(AppNotProfileOwnerException())
        return withTimeoutOrNull(5000L) {
            suspendCancellableCoroutine<Result<Unit>> { continuation ->
                try {
                    devicePolicyManager.clearApplicationUserData(
                        deviceOwnerReceiver,
                        packageName,
                        ContextCompat.getMainExecutor(context)
                    ) { pkg, succeeded ->
                        if (continuation.isActive) {
                            if (succeeded) {
                                Logger.writeDebugLog("clearApplicationUserData succeeded for $pkg")
                                continuation.resume(Result.success(Unit))
                            } else {
                                Logger.writeErrorLog("clearApplicationUserData failed for $pkg")
                                continuation.resume(Result.failure(IllegalStateException("Failed to clear data for $pkg")))
                            }
                        }
                    }
                } catch (e: Exception) {
                    if (continuation.isActive) {
                        continuation.resume(Result.failure(e))
                    }
                }
            }
        } ?: Result.failure(TimeoutException("clearApplicationUserData timed out for $packageName"))
    }

    companion object {
        private const val PREFS_NAME = "one_shot_apps_prefs"
        private const val KEY_ONE_SHOT_PACKAGES = "one_shot_packages"
        private const val KEY_AUTO_FREEZE_PACKAGES = "auto_freeze_packages"
    }
}
