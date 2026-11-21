package com.xxmrk888ytxx.core.devicepolicy.ownerComponent

import android.app.ActivityManager
import android.app.admin.DevicePolicyManager
import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import com.xxmrk888ytxx.core.base.android.logs.Logger
import com.xxmrk888ytxx.core.devicepolicy.OneShotAppManagerOwnerComponent
import com.xxmrk888ytxx.core.devicepolicy.exception.AppNotProfileOwnerException
import com.xxmrk888ytxx.core.devicepolicy.model.AppInactivityPolicy
import com.xxmrk888ytxx.core.devicepolicy.model.LockInactivityAction
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import org.json.JSONObject
import java.util.concurrent.ConcurrentHashMap
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

    private val _appInactivityPolicies = MutableStateFlow<Map<String, AppInactivityPolicy>>(emptyMap())
    override val appInactivityPolicies: Flow<Map<String, AppInactivityPolicy>> = _appInactivityPolicies.asStateFlow()

    private val wipedPackagesInCurrentLockSession = ConcurrentHashMap.newKeySet<String>()

    init {
        _oneShotAppPackages.value = getStoredOneShotPackages()
        _autoFreezeAppPackages.value = getStoredAutoFreezePackages()
        _appInactivityPolicies.value = getStoredInactivityPolicies()
    }

    private fun getStoredOneShotPackages(): Set<String> {
        return prefs.getStringSet(KEY_ONE_SHOT_PACKAGES, emptySet())?.toSet() ?: emptySet()
    }

    private fun getStoredAutoFreezePackages(): Set<String> {
        return prefs.getStringSet(KEY_AUTO_FREEZE_PACKAGES, emptySet())?.toSet() ?: emptySet()
    }

    private fun getStoredInactivityPolicies(): Map<String, AppInactivityPolicy> {
        val jsonStr = prefs.getString(KEY_INACTIVITY_POLICIES, null) ?: return emptyMap()
        val result = mutableMapOf<String, AppInactivityPolicy>()
        return try {
            val root = JSONObject(jsonStr)
            val keys = root.keys()
            while (keys.hasNext()) {
                val pkg = keys.next()
                val policyJson = root.optString(pkg, "")
                if (policyJson.isNotEmpty()) {
                    val policy = AppInactivityPolicy.fromJson(policyJson)
                    if (policy != null) {
                        result[pkg] = policy
                    }
                }
            }
            result
        } catch (e: Exception) {
            Logger.writeErrorLog("Failed to parse stored inactivity policies: ${e.message}")
            emptyMap()
        }
    }

    private fun saveInactivityPolicies(map: Map<String, AppInactivityPolicy>) {
        try {
            val root = JSONObject()
            for ((pkg, policy) in map) {
                root.put(pkg, policy.toJson())
            }
            prefs.edit().putString(KEY_INACTIVITY_POLICIES, root.toString()).apply()
            _appInactivityPolicies.value = map
        } catch (e: Exception) {
            Logger.writeErrorLog("Failed to save inactivity policies: ${e.message}")
        }
    }

    override fun isOneShotApp(packageName: String): Boolean {
        return _oneShotAppPackages.value.contains(packageName)
    }

    override fun isAutoFreezeApp(packageName: String): Boolean {
        return _autoFreezeAppPackages.value.contains(packageName)
    }

    override fun isPackageInForeground(packageName: String): Boolean {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager
            ?: return false
        return try {
            val runningTasks = activityManager.getRunningTasks(10)
            val taskPackages = runningTasks.flatMap { task ->
                listOfNotNull(
                    task.topActivity?.packageName,
                    task.baseActivity?.packageName
                )
            }
            val foregroundProcess = activityManager.runningAppProcesses.orEmpty().any { process ->
                process.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND &&
                    packageName in process.pkgList
            }
            val usageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as? UsageStatsManager
            val usageEvent = UsageEvents.Event()
            var latestUsageEventType = -1
            var latestUsageEventTime = 0L
            usageStatsManager
                ?.queryEvents(System.currentTimeMillis() - 60 * 60 * 1000L, System.currentTimeMillis())
                ?.let { events ->
                    while (events.hasNextEvent()) {
                        events.getNextEvent(usageEvent)
                        if (usageEvent.packageName == packageName && usageEvent.timeStamp >= latestUsageEventTime &&
                            (usageEvent.eventType == UsageEvents.Event.MOVE_TO_FOREGROUND ||
                                usageEvent.eventType == UsageEvents.Event.MOVE_TO_BACKGROUND)
                        ) {
                            latestUsageEventTime = usageEvent.timeStamp
                            latestUsageEventType = usageEvent.eventType
                        }
                    }
                }
            val usageForeground = latestUsageEventType == UsageEvents.Event.MOVE_TO_FOREGROUND
            packageName in taskPackages || foregroundProcess || usageForeground
        } catch (e: SecurityException) {
            Logger.writeDebugLog("isPackageInForeground: access denied for $packageName: ${e.message}")
            false
        } catch (e: Exception) {
            Logger.writeErrorLog("isPackageInForeground failed for $packageName: ${e.message}")
            false
        }
    }

    override fun getInactivityPolicy(packageName: String): AppInactivityPolicy? {
        return _appInactivityPolicies.value[packageName]
    }

    override suspend fun setInactivityPolicy(policy: AppInactivityPolicy): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val currentMap = _appInactivityPolicies.value.toMutableMap()
            val previousPolicy = currentMap[policy.packageName]
            val hasUnusedPolicy = policy.freezeIfUnusedEnabled || policy.wipeIfUnusedEnabled
            val unusedPolicyChanged = previousPolicy != null && (
                previousPolicy.freezeIfUnusedEnabled != policy.freezeIfUnusedEnabled ||
                    previousPolicy.wipeIfUnusedEnabled != policy.wipeIfUnusedEnabled ||
                    (policy.freezeIfUnusedEnabled && (
                        previousPolicy.freezeIfUnusedMinutes != policy.freezeIfUnusedMinutes ||
                            previousPolicy.freezeIfUnusedHours != policy.freezeIfUnusedHours ||
                            previousPolicy.freezeIfUnusedDays != policy.freezeIfUnusedDays
                        )) ||
                    (policy.wipeIfUnusedEnabled &&
                        (previousPolicy.wipeIfUnusedDays != policy.wipeIfUnusedDays ||
                            previousPolicy.wipeIfUnusedHours != policy.wipeIfUnusedHours ||
                            previousPolicy.wipeIfUnusedMinutes != policy.wipeIfUnusedMinutes))
                )
            val startsNewUnusedCycle = hasUnusedPolicy && (
                previousPolicy == null || policy.lastOpenedTimestamp == 0L || unusedPolicyChanged
                )
            val effectivePolicy = if (startsNewUnusedCycle) {
                policy.copy(
                    lastOpenedTimestamp = System.currentTimeMillis(),
                    lastUnusedFreezeTimestamp = 0L,
                    lastUnusedWipeTimestamp = 0L
                )
            } else if (policy.freezeIfUnusedEnabled) {
                policy.copy(
                    lastUnusedFreezeTimestamp = 0L,
                    lastUnusedWipeTimestamp = 0L
                )
            } else {
                policy
            }

            if (effectivePolicy.isConfigured) {
                currentMap[policy.packageName] = effectivePolicy
            } else {
                currentMap.remove(policy.packageName)
            }
            saveInactivityPolicies(currentMap)
            Logger.writeDebugLog(
                "setInactivityPolicy for ${policy.packageName}: " +
                    "lockAction=${effectivePolicy.lockAction}, " +
                    "lockFreezeHours=${effectivePolicy.lockFreezeHours}, " +
                    "lockWipeHours=${effectivePolicy.lockWipeHours}, " +
                    "freezeUnused=${effectivePolicy.freezeIfUnusedEnabled}, " +
                    "freezeMinutes=${effectivePolicy.freezeIfUnusedMinutes}, " +
                    "freezeHours=${effectivePolicy.freezeIfUnusedHours}, " +
                    "wipeUnused=${effectivePolicy.wipeIfUnusedEnabled}, " +
                    "wipeDays=${effectivePolicy.wipeIfUnusedDays}, " +
                    "wipeHours=${effectivePolicy.wipeIfUnusedHours}, " +
                    "wipeMinutes=${effectivePolicy.wipeIfUnusedMinutes}, " +
                    "lastOpened=${effectivePolicy.lastOpenedTimestamp}"
            )
            Result.success(Unit)
        } catch (e: Exception) {
            Logger.writeErrorLog("setInactivityPolicy failed for ${policy.packageName}: ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun recordAppOpened(packageName: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val currentMap = _appInactivityPolicies.value.toMutableMap()
            val existing = currentMap[packageName]
            if (existing != null) {
                val updated = existing.copy(
                    lastOpenedTimestamp = System.currentTimeMillis(),
                    lastUnusedFreezeTimestamp = 0L,
                    lastUnusedWipeTimestamp = 0L
                )
                currentMap[packageName] = updated
                saveInactivityPolicies(currentMap)
                Logger.writeDebugLog("recordAppOpened: updated lastOpenedTimestamp for $packageName")
            } else {
                Logger.writeDebugLog("recordAppOpened: no configured inactivity policy for $packageName")
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Logger.writeErrorLog("recordAppOpened failed for $packageName: ${e.message}")
            Result.failure(e)
        }
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

                Logger.writeDebugLog("setOneShotApp: disabled for $packageName, unfreezing app")
                unhideApp(packageName)
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

                Logger.writeDebugLog("setAutoFreezeApp: disabled for $packageName, unfreezing app")
                unhideApp(packageName)
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
                recordAppOpened(packageName)
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
                return@withContext clearResult
            }
            val hideResult = hideApp(packageName)
            if (hideResult.isFailure) {
                Logger.writeErrorLog("resetOneShotApp: failed to hide $packageName: ${hideResult.exceptionOrNull()?.message}")
                return@withContext hideResult
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

    override suspend fun checkAndApplyInactivityPolicies(
        deviceLockedDurationMs: Long,
        isDeviceLocked: Boolean
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            if (!isDeviceLocked) {
                wipedPackagesInCurrentLockSession.clear()
            }

            val policies = _appInactivityPolicies.value
            val now = System.currentTimeMillis()

            for ((pkg, policy) in policies) {
                var currentPolicy = policy
                var policyModified = false

                if ((currentPolicy.freezeIfUnusedEnabled || currentPolicy.wipeIfUnusedEnabled) &&
                    isPackageInForeground(pkg)
                ) {
                    Logger.writeDebugLog("checkAndApplyInactivityPolicies: $pkg is in foreground, resetting unused timer")
                    currentPolicy = currentPolicy.copy(
                        lastOpenedTimestamp = now,
                        lastUnusedFreezeTimestamp = 0L,
                        lastUnusedWipeTimestamp = 0L
                    )
                    policyModified = true
                }

                if (policyModified) {
                    val updatedMap = _appInactivityPolicies.value.toMutableMap()
                    updatedMap[pkg] = currentPolicy
                    saveInactivityPolicies(updatedMap)
                    continue
                }

                // 1. Device locked inactivity checks
                if (isDeviceLocked && deviceLockedDurationMs > 0L) {
                    when (currentPolicy.lockAction) {
                        LockInactivityAction.FREEZE_ONLY -> {
                            val freezeThresholdMs = currentPolicy.lockFreezeDurationMs
                            if (deviceLockedDurationMs >= freezeThresholdMs) {
                                Logger.writeDebugLog("checkAndApplyInactivityPolicies: Freezing $pkg (locked for ${deviceLockedDurationMs / 1000}s >= ${freezeThresholdMs / 1000}s)")
                                val hideResult = hideApp(pkg)
                                if (hideResult.isSuccess && (currentPolicy.lastLockFreezeTimestamp == 0L || (now - currentPolicy.lastLockFreezeTimestamp) > 60_000L)) {
                                    currentPolicy = currentPolicy.copy(lastLockFreezeTimestamp = now)
                                    policyModified = true
                                } else if (hideResult.isFailure) {
                                    Logger.writeErrorLog("checkAndApplyInactivityPolicies: failed to freeze $pkg: ${hideResult.exceptionOrNull()?.message}")
                                }
                            }
                        }
                        LockInactivityAction.FREEZE_AND_WIPE -> {
                            val freezeThresholdMs = currentPolicy.lockFreezeDurationMs
                            val wipeThresholdMs = currentPolicy.lockWipeDurationMs

                            if (deviceLockedDurationMs >= freezeThresholdMs) {
                                Logger.writeDebugLog("checkAndApplyInactivityPolicies: Freezing $pkg (locked for ${deviceLockedDurationMs / 1000}s >= ${freezeThresholdMs / 1000}s)")
                                val hideResult = hideApp(pkg)
                                if (hideResult.isSuccess && (currentPolicy.lastLockFreezeTimestamp == 0L || (now - currentPolicy.lastLockFreezeTimestamp) > 60_000L)) {
                                    currentPolicy = currentPolicy.copy(lastLockFreezeTimestamp = now)
                                    policyModified = true
                                } else if (hideResult.isFailure) {
                                    Logger.writeErrorLog("checkAndApplyInactivityPolicies: failed to freeze $pkg: ${hideResult.exceptionOrNull()?.message}")
                                }
                            }

                            if (deviceLockedDurationMs >= wipeThresholdMs) {
                                if (!wipedPackagesInCurrentLockSession.contains(pkg)) {
                                    Logger.writeDebugLog("checkAndApplyInactivityPolicies: Wiping $pkg (locked for ${deviceLockedDurationMs / 1000}s >= ${wipeThresholdMs / 1000}s)")
                                    val clearResult = clearAppData(pkg)
                                    val hideResult = if (clearResult.isSuccess) hideApp(pkg) else clearResult
                                    if (clearResult.isSuccess && hideResult.isSuccess) {
                                        wipedPackagesInCurrentLockSession.add(pkg)
                                        currentPolicy = currentPolicy.copy(lastLockWipeTimestamp = now)
                                        policyModified = true
                                    } else {
                                        Logger.writeErrorLog("checkAndApplyInactivityPolicies: failed to wipe $pkg: ${clearResult.exceptionOrNull()?.message ?: hideResult.exceptionOrNull()?.message}")
                                    }
                                }
                            }
                        }
                        LockInactivityAction.NONE -> { /* No lock inactivity action */ }
                    }
                }

                // 2. Unused app checks (e.g. 30 days)
                if (currentPolicy.freezeIfUnusedEnabled && currentPolicy.lastOpenedTimestamp > 0L) {
                    val unusedFreezeThresholdMs = currentPolicy.unusedFreezeDurationMs
                    val unusedDurationMs = now - currentPolicy.lastOpenedTimestamp
                    if (unusedDurationMs >= unusedFreezeThresholdMs &&
                        currentPolicy.lastUnusedFreezeTimestamp == 0L
                    ) {
                        Logger.writeDebugLog("checkAndApplyInactivityPolicies: Freezing $pkg because unused for ${unusedDurationMs / 1000}s >= ${unusedFreezeThresholdMs / 1000}s")
                        val hideResult = hideApp(pkg)
                        if (hideResult.isSuccess) {
                            currentPolicy = currentPolicy.copy(lastUnusedFreezeTimestamp = now)
                            policyModified = true
                        } else {
                            Logger.writeErrorLog("checkAndApplyInactivityPolicies: failed to freeze unused app $pkg: ${hideResult.exceptionOrNull()?.message}")
                        }
                    }
                }

                if (currentPolicy.wipeIfUnusedEnabled && currentPolicy.lastOpenedTimestamp > 0L) {
                    val unusedThresholdMs = currentPolicy.unusedWipeDurationMs
                    val unusedDurationMs = now - currentPolicy.lastOpenedTimestamp
                    if (unusedDurationMs >= unusedThresholdMs) {
                        Logger.writeDebugLog("checkAndApplyInactivityPolicies: Wiping $pkg because unused for ${unusedDurationMs / 1000}s >= ${unusedThresholdMs / 1000}s")
                        val clearResult = clearAppData(pkg)
                        if (clearResult.isSuccess) {
                            currentPolicy = currentPolicy.copy(
                                lastOpenedTimestamp = now,
                                lastUnusedWipeTimestamp = now
                            )
                            policyModified = true
                        } else {
                            Logger.writeErrorLog("checkAndApplyInactivityPolicies: failed to wipe unused app $pkg: ${clearResult.exceptionOrNull()?.message}")
                        }
                    }
                }

                if (policyModified) {
                    val updatedMap = _appInactivityPolicies.value.toMutableMap()
                    updatedMap[pkg] = currentPolicy
                    saveInactivityPolicies(updatedMap)
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Logger.writeErrorLog("checkAndApplyInactivityPolicies failed: ${e.message}")
            Result.failure(e)
        }
    }

    companion object {
        private const val PREFS_NAME = "one_shot_apps_prefs"
        private const val KEY_ONE_SHOT_PACKAGES = "one_shot_packages"
        private const val KEY_AUTO_FREEZE_PACKAGES = "auto_freeze_packages"
        private const val KEY_INACTIVITY_POLICIES = "inactivity_policies"
    }
}
