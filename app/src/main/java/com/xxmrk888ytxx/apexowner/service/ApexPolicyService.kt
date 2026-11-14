package com.xxmrk888ytxx.apexowner.service

import android.app.AlarmManager
import android.app.KeyguardManager
import android.app.PendingIntent
import android.app.Service
import android.app.admin.DevicePolicyManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import android.os.UserManager
import androidx.core.content.ContextCompat
import com.xxmrk888ytxx.apexowner.R
import com.xxmrk888ytxx.core.base.android.extension.buildForegroundServiceNotificationChannel
import com.xxmrk888ytxx.core.base.android.extension.buildNotification
import com.xxmrk888ytxx.core.base.android.logs.Logger
import com.xxmrk888ytxx.core.devicepolicy.OneShotAppManagerOwnerComponent
import com.xxmrk888ytxx.core.devicepolicy.PermissionManagerOwnerComponent
import com.xxmrk888ytxx.core.devicepolicy.model.LockInactivityAction
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@AndroidEntryPoint
class ApexPolicyService : Service() {

    @Inject
    lateinit var permissionManagerOwnerComponent: PermissionManagerOwnerComponent

    @Inject
    lateinit var oneShotAppManagerOwnerComponent: OneShotAppManagerOwnerComponent

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private var isReceiverRegistered = false
    private var isShutdownReceiverRegistered = false
    private var isLockReceiverRegistered = false

    private val shutdownReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val action = intent?.action ?: return
            Logger.writeDebugLog("ApexPolicyService received shutdown action: $action")
            if (action == Intent.ACTION_SHUTDOWN) {
                runBlocking {
                    try {
                        Logger.writeDebugLog("ApexPolicyService handling triggers on ACTION_SHUTDOWN")
                        oneShotAppManagerOwnerComponent.handleLifecycleTriggers()
                    } catch (e: Exception) {
                        Logger.writeErrorLog("Failed to handle triggers on ACTION_SHUTDOWN: ${e.message}")
                    }
                }
            }
        }
    }

    private val packageReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val action = intent?.action ?: return
            val packageName = intent.data?.schemeSpecificPart ?: return
            Logger.writeDebugLog("ApexPolicyService received: $action for $packageName")

            if (action == Intent.ACTION_PACKAGE_ADDED || action == Intent.ACTION_PACKAGE_REPLACED) {
                serviceScope.launch {
                    try {
                        Logger.writeDebugLog("ApexPolicyService applying permissions to package: $packageName")
                        permissionManagerOwnerComponent.applyPermissionsToPackage(packageName)
                    } catch (e: Exception) {
                        Logger.writeErrorLog("Failed to apply permissions to $packageName in ApexPolicyService: ${e.message}")
                    }
                }
            }
        }
    }

    private val lockReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val action = intent?.action ?: return
            val km = getSystemService(KEYGUARD_SERVICE) as? KeyguardManager
            Logger.writeDebugLog("ApexPolicyService lockReceiver received: $action")

            when (action) {
                Intent.ACTION_SCREEN_OFF -> {
                    val prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
                    var lockedSince = prefs.getLong(KEY_DEVICE_LOCKED_SINCE, 0L)
                    if (lockedSince == 0L) {
                        lockedSince = System.currentTimeMillis()
                        prefs.edit().putLong(KEY_DEVICE_LOCKED_SINCE, lockedSince).apply()
                        Logger.writeDebugLog("ApexPolicyService: device locked at $lockedSince")
                    }
                    evaluateInactivityPolicies()
                    scheduleNextInactivityAlarm()
                }

                Intent.ACTION_USER_PRESENT -> {
                    Logger.writeDebugLog("ApexPolicyService: user unlocked device, resetting lock timer")
                    val prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
                    prefs.edit().putLong(KEY_DEVICE_LOCKED_SINCE, 0L).apply()
                    evaluateInactivityPolicies()
                    scheduleNextInactivityAlarm()
                }

                Intent.ACTION_SCREEN_ON -> {
                    // Do NOT eagerly reset lockedSince on SCREEN_ON if device has a keyguard/lock screen.
                    // ACTION_USER_PRESENT will be called once the user actually unlocks.
                    // Only reset on SCREEN_ON if keyguard is absent or disabled.
                    val hasKeyguard = km?.isKeyguardSecure == true || km?.isKeyguardLocked == true || km?.isDeviceLocked == true
                    if (!hasKeyguard) {
                        Logger.writeDebugLog("ApexPolicyService: screen on and no keyguard, resetting lock timer")
                        val prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
                        prefs.edit().putLong(KEY_DEVICE_LOCKED_SINCE, 0L).apply()
                        evaluateInactivityPolicies()
                        scheduleNextInactivityAlarm()
                    }
                }
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        Logger.writeDebugLog("ApexPolicyService: onCreate")
        if (!shouldServiceRun(this)) {
            Logger.writeDebugLog("ApexPolicyService: shouldServiceRun is false in this profile, stopping self")
            startForegroundInternal()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                stopForeground(STOP_FOREGROUND_REMOVE)
            } else {
                @Suppress("DEPRECATION")
                stopForeground(true)
            }
            stopSelf()
            return
        }
        startForegroundInternal()
        registerPackageReceiver()
        registerShutdownReceiver()
        registerLockReceiver()

        val km = getSystemService(KEYGUARD_SERVICE) as? KeyguardManager
        val isLocked = km?.isKeyguardLocked == true || km?.isDeviceLocked == true
        val prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        if (isLocked) {
            val lockedSince = prefs.getLong(KEY_DEVICE_LOCKED_SINCE, 0L)
            if (lockedSince == 0L) {
                prefs.edit().putLong(KEY_DEVICE_LOCKED_SINCE, System.currentTimeMillis()).apply()
            }
        } else {
            prefs.edit().putLong(KEY_DEVICE_LOCKED_SINCE, 0L).apply()
        }

        serviceScope.launch {
            try {
                Logger.writeDebugLog("ApexPolicyService: handling triggers on service start")
                oneShotAppManagerOwnerComponent.handleLifecycleTriggers()
                evaluateInactivityPolicies()
                scheduleNextInactivityAlarm()
            } catch (e: Exception) {
                Logger.writeErrorLog("Failed to handle triggers on service start: ${e.message}")
            }
        }

        // Periodic ticker every 15 minutes
        serviceScope.launch {
            while (isActive) {
                delay(15 * 60 * 1000L)
                try {
                    evaluateInactivityPolicies()
                    scheduleNextInactivityAlarm()
                } catch (e: Exception) {
                    Logger.writeErrorLog("ApexPolicyService ticker error: ${e.message}")
                }
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Logger.writeDebugLog("ApexPolicyService: onStartCommand action=${intent?.action}")
        if (!shouldServiceRun(this)) {
            Logger.writeDebugLog("ApexPolicyService: onStartCommand with shouldServiceRun=false, stopping")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                stopForeground(STOP_FOREGROUND_REMOVE)
            } else {
                @Suppress("DEPRECATION")
                stopForeground(true)
            }
            stopSelf()
            return START_NOT_STICKY
        }
        startForegroundInternal()

        if (intent?.action == ACTION_CHECK_POLICIES) {
            evaluateInactivityPolicies()
            scheduleNextInactivityAlarm()
        }

        return START_STICKY
    }

    private fun startForegroundInternal() {
        try {
            buildForegroundServiceNotificationChannel(
                CHANNEL_ID,
                getString(R.string.policy_service_channel_name)
            )

            val notification = buildNotification(CHANNEL_ID) {
                setContentTitle(getString(R.string.policy_service_notification_title))
                setContentText(getString(R.string.policy_service_notification_desc))
                setSmallIcon(R.mipmap.ic_launcher)
                setOngoing(true)
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                startForeground(
                    NOTIFICATION_ID,
                    notification,
                    ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE
                )
            } else {
                startForeground(NOTIFICATION_ID, notification)
            }
        } catch (e: Exception) {
            Logger.writeErrorLog("Failed to start foreground in ApexPolicyService: ${e.message}")
        }
    }

    private fun registerPackageReceiver() {
        if (isReceiverRegistered) return
        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_PACKAGE_ADDED)
            addAction(Intent.ACTION_PACKAGE_REPLACED)
            addDataScheme("package")
        }
        try {
            ContextCompat.registerReceiver(
                this,
                packageReceiver,
                filter,
                ContextCompat.RECEIVER_EXPORTED
            )
            isReceiverRegistered = true
            Logger.writeDebugLog("ApexPolicyService: packageReceiver successfully registered")
        } catch (e: Exception) {
            Logger.writeErrorLog("Failed to register packageReceiver in ApexPolicyService: ${e.message}")
        }
    }

    private fun registerShutdownReceiver() {
        if (isShutdownReceiverRegistered) return
        val filter = IntentFilter(Intent.ACTION_SHUTDOWN)
        try {
            registerReceiver(shutdownReceiver, filter)
            isShutdownReceiverRegistered = true
            Logger.writeDebugLog("ApexPolicyService: shutdownReceiver successfully registered")
        } catch (e: Exception) {
            Logger.writeErrorLog("Failed to register shutdownReceiver in ApexPolicyService: ${e.message}")
        }
    }

    private fun registerLockReceiver() {
        if (isLockReceiverRegistered) return
        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_SCREEN_OFF)
            addAction(Intent.ACTION_USER_PRESENT)
            addAction(Intent.ACTION_SCREEN_ON)
        }
        try {
            registerReceiver(lockReceiver, filter)
            isLockReceiverRegistered = true
            Logger.writeDebugLog("ApexPolicyService: lockReceiver successfully registered")
        } catch (e: Exception) {
            Logger.writeErrorLog("Failed to register lockReceiver in ApexPolicyService: ${e.message}")
        }
    }

    private fun evaluateInactivityPolicies() {
        serviceScope.launch {
            try {
                val prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
                val lockedSince = prefs.getLong(KEY_DEVICE_LOCKED_SINCE, 0L)
                val km = getSystemService(KEYGUARD_SERVICE) as? KeyguardManager
                val isKmLocked = km?.isKeyguardLocked == true || km?.isDeviceLocked == true
                val isLocked = lockedSince > 0L || isKmLocked
                val now = System.currentTimeMillis()
                val lockedDuration = if (lockedSince > 0L) {
                    (now - lockedSince).coerceAtLeast(0L)
                } else {
                    0L
                }
                Logger.writeDebugLog("ApexPolicyService evaluating inactivity: isLocked=$isLocked, lockedSince=$lockedSince, durationMs=$lockedDuration")
                oneShotAppManagerOwnerComponent.checkAndApplyInactivityPolicies(lockedDuration, isLocked)
            } catch (e: Exception) {
                Logger.writeErrorLog("Failed to evaluate inactivity policies in ApexPolicyService: ${e.message}")
            }
        }
    }

    private fun scheduleNextInactivityAlarm() {
        serviceScope.launch {
            try {
                val alarmManager = getSystemService(ALARM_SERVICE) as? AlarmManager ?: return@launch
                val prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
                val lockedSince = prefs.getLong(KEY_DEVICE_LOCKED_SINCE, 0L)
                val km = getSystemService(KEYGUARD_SERVICE) as? KeyguardManager
                val isKmLocked = km?.isKeyguardLocked == true || km?.isDeviceLocked == true
                val isLocked = lockedSince > 0L || isKmLocked
                val now = System.currentTimeMillis()

                val policies = oneShotAppManagerOwnerComponent.appInactivityPolicies.first()
                var earliestDeadline = Long.MAX_VALUE

                for ((_, policy) in policies) {
                    if (isLocked && lockedSince > 0L) {
                        when (policy.lockAction) {
                            LockInactivityAction.FREEZE_ONLY -> {
                                val deadline = lockedSince + policy.lockFreezeDurationMs
                                if (deadline > now && deadline < earliestDeadline) {
                                    earliestDeadline = deadline
                                }
                            }
                            LockInactivityAction.FREEZE_AND_WIPE -> {
                                val freezeDeadline = lockedSince + policy.lockFreezeDurationMs
                                val wipeDeadline = lockedSince + policy.lockWipeDurationMs
                                if (freezeDeadline > now && freezeDeadline < earliestDeadline) {
                                    earliestDeadline = freezeDeadline
                                }
                                if (wipeDeadline > now && wipeDeadline < earliestDeadline) {
                                    earliestDeadline = wipeDeadline
                                }
                            }
                            LockInactivityAction.NONE -> {}
                        }
                    }
                    if (policy.wipeIfUnusedEnabled && policy.lastOpenedTimestamp > 0L) {
                        val unusedDeadline = policy.lastOpenedTimestamp + policy.unusedWipeDurationMs
                        if (unusedDeadline > now && unusedDeadline < earliestDeadline) {
                            earliestDeadline = unusedDeadline
                        }
                    }
                }

                if (earliestDeadline != Long.MAX_VALUE) {
                    Logger.writeDebugLog("ApexPolicyService: scheduling next inactivity alarm in ${(earliestDeadline - now) / 1000}s")
                    val intent = Intent(this@ApexPolicyService, InactivityAlarmReceiver::class.java).apply {
                        action = InactivityAlarmReceiver.ACTION_CHECK_INACTIVITY
                    }
                    val pendingIntent = PendingIntent.getBroadcast(
                        this@ApexPolicyService,
                        ALARM_REQUEST_CODE,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    )
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        try {
                            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, earliestDeadline, pendingIntent)
                        } catch (_: SecurityException) {
                            alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, earliestDeadline, pendingIntent)
                        }
                    } else {
                        alarmManager.set(AlarmManager.RTC_WAKEUP, earliestDeadline, pendingIntent)
                    }
                } else {
                    cancelInactivityAlarm()
                }
            } catch (e: Exception) {
                Logger.writeErrorLog("Failed to schedule inactivity alarm: ${e.message}")
            }
        }
    }

    private fun cancelInactivityAlarm() {
        try {
            val alarmManager = getSystemService(ALARM_SERVICE) as? AlarmManager ?: return
            val intent = Intent(this, InactivityAlarmReceiver::class.java).apply {
                action = InactivityAlarmReceiver.ACTION_CHECK_INACTIVITY
            }
            val pendingIntent = PendingIntent.getBroadcast(
                this,
                ALARM_REQUEST_CODE,
                intent,
                PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
            )
            if (pendingIntent != null) {
                alarmManager.cancel(pendingIntent)
                pendingIntent.cancel()
                Logger.writeDebugLog("ApexPolicyService: cancelled inactivity alarm")
            }
        } catch (e: Exception) {
            Logger.writeErrorLog("Failed to cancel inactivity alarm: ${e.message}")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Logger.writeDebugLog("ApexPolicyService: onDestroy")
        if (isReceiverRegistered) {
            try {
                unregisterReceiver(packageReceiver)
                isReceiverRegistered = false
            } catch (e: Exception) {
                Logger.writeErrorLog("Failed to unregister packageReceiver: ${e.message}")
            }
        }
        if (isShutdownReceiverRegistered) {
            try {
                unregisterReceiver(shutdownReceiver)
                isShutdownReceiverRegistered = false
            } catch (e: Exception) {
                Logger.writeErrorLog("Failed to unregister shutdownReceiver: ${e.message}")
            }
        }
        if (isLockReceiverRegistered) {
            try {
                unregisterReceiver(lockReceiver)
                isLockReceiverRegistered = false
            } catch (e: Exception) {
                Logger.writeErrorLog("Failed to unregister lockReceiver: ${e.message}")
            }
        }
        cancelInactivityAlarm()
        serviceScope.cancel()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    companion object {
        private const val CHANNEL_ID = "apex_policy_service_channel"
        private const val NOTIFICATION_ID = 1001
        private const val ALARM_REQUEST_CODE = 2002
        private const val PREFS_NAME = "apex_policy_service_prefs"
        private const val KEY_DEVICE_LOCKED_SINCE = "device_locked_since"
        const val ACTION_CHECK_POLICIES = "com.xxmrk888ytxx.apexowner.action.CHECK_POLICIES"

        fun shouldServiceRun(context: Context): Boolean {
            val dpm = context.getSystemService(DEVICE_POLICY_SERVICE) as? DevicePolicyManager
            val userManager = context.getSystemService(USER_SERVICE) as? UserManager
            val packageName = context.packageName

            val isDeviceOwner = dpm?.isDeviceOwnerApp(packageName) == true
            val isProfileOwner = dpm?.isProfileOwnerApp(packageName) == true
            val isManagedProfile = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                userManager?.isManagedProfile == true
            } else {
                isProfileOwner && !isDeviceOwner
            }

            // In work profile (managed profile / profile owner), the service is needed.
            // In main profile (personal profile), the service is needed ONLY if the app is Device Owner.
            return isManagedProfile || isProfileOwner || isDeviceOwner
        }

        fun triggerInactivityCheck(context: Context) {
            if (!shouldServiceRun(context)) return
            try {
                val intent = Intent(context, ApexPolicyService::class.java).apply {
                    action = ACTION_CHECK_POLICIES
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(intent)
                } else {
                    context.startService(intent)
                }
            } catch (e: Exception) {
                Logger.writeErrorLog("Failed to trigger inactivity check: ${e.message}")
            }
        }

        fun start(context: Context) {
            if (!shouldServiceRun(context)) {
                Logger.writeDebugLog("ApexPolicyService.start: service should not run in current profile, stopping any running instance")
                stop(context)
                return
            }
            try {
                val intent = Intent(context, ApexPolicyService::class.java)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(intent)
                } else {
                    context.startService(intent)
                }
            } catch (e: Exception) {
                Logger.writeErrorLog("Failed to start ApexPolicyService: ${e.message}")
            }
        }

        fun stop(context: Context) {
            try {
                val intent = Intent(context, ApexPolicyService::class.java)
                context.stopService(intent)
            } catch (e: Exception) {
                Logger.writeErrorLog("Failed to stop ApexPolicyService: ${e.message}")
            }
        }
    }
}
