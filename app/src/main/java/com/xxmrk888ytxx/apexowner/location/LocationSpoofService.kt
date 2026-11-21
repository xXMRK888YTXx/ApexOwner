package com.xxmrk888ytxx.apexowner.location

import android.app.Service
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.IBinder
import android.os.SystemClock
import android.util.Log
import android.content.pm.ServiceInfo
import com.xxmrk888ytxx.apexowner.R
import com.xxmrk888ytxx.core.base.android.extension.buildForegroundServiceNotificationChannel
import com.xxmrk888ytxx.core.base.android.extension.buildNotification
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class LocationSpoofService : Service() {
    private val scope = CoroutineScope(Dispatchers.Default)
    private var job: Job? = null
    private lateinit var locationManager: LocationManager
    private val initializedProviders = mutableSetOf<String>()

    override fun onCreate() {
        super.onCreate()
        locationManager = getSystemService(LocationManager::class.java)
        startForegroundNotification()
        job = scope.launch {
            while (isActive) {
                publishLocation()
                delay(5_000)
            }
        }
    }

    private fun publishLocation() {
        val preferences = getSharedPreferences("location_spoof", MODE_PRIVATE)
        val latitude = preferences.getString("latitude", null)?.replace(',', '.')?.toDoubleOrNull() ?: return
        val longitude = preferences.getString("longitude", null)?.replace(',', '.')?.toDoubleOrNull() ?: return
        var published = false
        listOf(LocationManager.GPS_PROVIDER, LocationManager.NETWORK_PROVIDER).forEach { provider ->
            try {
                if (provider !in initializedProviders) {
                    runCatching { locationManager.removeTestProvider(provider) }
                    locationManager.addTestProvider(provider, false, false, false, false, true, true, true, 1, 1)
                    initializedProviders += provider
                }
                locationManager.setTestProviderEnabled(provider, true)
                locationManager.setTestProviderLocation(provider, Location(provider).apply {
                    this.latitude = latitude
                    this.longitude = longitude
                    accuracy = 1f
                    time = System.currentTimeMillis()
                    elapsedRealtimeNanos = SystemClock.elapsedRealtimeNanos()
                })
                published = true
            } catch (error: Exception) {
                initializedProviders -= provider
                Log.e(TAG, "Failed to publish $provider mock location", error)
            }
        }
        if (!published) {
            Log.e(TAG, "No mock location provider accepted the configured point")
        }
    }

    private fun startForegroundNotification() {
        buildForegroundServiceNotificationChannel(CHANNEL_ID, getString(R.string.location_spoof_channel_name))
        val notification = buildNotification(CHANNEL_ID) {
            setContentTitle(getString(R.string.location_spoof_notification_title))
            setContentText(getString(R.string.location_spoof_notification_text))
            setSmallIcon(R.mipmap.ic_launcher)
            setOngoing(true)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            startForeground(NOTIFICATION_ID, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE)
        } else {
            startForeground(NOTIFICATION_ID, notification)
        }
    }

    override fun onDestroy() {
        job?.cancel()
        cleanupProviders()
        super.onDestroy()
    }

    private fun cleanupProviders() {
        initializedProviders.toList().forEach { provider ->
            runCatching { locationManager.removeTestProvider(provider) }
        }
        initializedProviders.clear()
    }

    override fun onBind(intent: Intent?): IBinder? = null
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_UPDATE -> publishLocation()
            ACTION_STOP -> {
                cleanupProviders()
                stopSelf()
                return START_NOT_STICKY
            }
        }
        return START_STICKY
    }

    companion object {
        private const val TAG = "LocationSpoofService"
        private const val CHANNEL_ID = "location_spoof"
        private const val NOTIFICATION_ID = 1002

        fun start(context: Context) {
            context.startForegroundService(Intent(context, LocationSpoofService::class.java))
        }

        fun stop(context: Context) {
            context.startService(
                Intent(context, LocationSpoofService::class.java).setAction(ACTION_STOP)
            )
        }

        fun update(context: Context) {
            context.startService(
                Intent(context, LocationSpoofService::class.java).setAction(ACTION_UPDATE)
            )
        }

        private const val ACTION_UPDATE = "com.xxmrk888ytxx.apexowner.action.UPDATE_MOCK_LOCATION"
        private const val ACTION_STOP = "com.xxmrk888ytxx.apexowner.action.STOP_MOCK_LOCATION"
    }
}
