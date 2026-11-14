package com.xxmrk888ytxx.apexowner.service

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import androidx.core.content.ContextCompat
import com.xxmrk888ytxx.apexowner.R
import com.xxmrk888ytxx.core.base.android.extension.buildForegroundServiceNotificationChannel
import com.xxmrk888ytxx.core.base.android.extension.buildNotification
import com.xxmrk888ytxx.core.base.android.logs.Logger
import com.xxmrk888ytxx.core.devicepolicy.PermissionManagerOwnerComponent
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ApexPolicyService : Service() {

    @Inject
    lateinit var permissionManagerOwnerComponent: PermissionManagerOwnerComponent

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private var isReceiverRegistered = false

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

    override fun onCreate() {
        super.onCreate()
        Logger.writeDebugLog("ApexPolicyService: onCreate")
        startForegroundInternal()
        registerPackageReceiver()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Logger.writeDebugLog("ApexPolicyService: onStartCommand")
        startForegroundInternal()
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
        serviceScope.cancel()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    companion object {
        private const val CHANNEL_ID = "apex_policy_service_channel"
        private const val NOTIFICATION_ID = 1001

        fun start(context: Context) {
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
    }
}
