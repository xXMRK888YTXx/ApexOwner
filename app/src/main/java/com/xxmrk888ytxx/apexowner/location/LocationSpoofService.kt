package com.xxmrk888ytxx.apexowner.location

import android.app.AppOpsManager
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
import android.location.Criteria
import com.xxmrk888ytxx.apexowner.R
import com.xxmrk888ytxx.core.base.android.extension.buildForegroundServiceNotificationChannel
import com.xxmrk888ytxx.core.base.android.extension.buildNotification
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import android.os.Process


class LocationSpoofService : Service() {

    private data class ProviderSpec(
        val name: String,
        val requiresNetwork: Boolean,
        val requiresSatellite: Boolean,
        val requiresCell: Boolean,
        val supportsAltitude: Boolean,
        val supportsSpeed: Boolean,
        val supportsBearing: Boolean,
        val powerRequirement: Int,
        val accuracy: Int,
        val accuracyMeters: Float
    )

    private val serviceJob = SupervisorJob()
    private val scope = CoroutineScope(serviceJob + Dispatchers.IO)

    private val providerLock = Any()
    private val initializedProviders = mutableSetOf<String>()

    @Volatile
    private var stopping = false

    private lateinit var locationManager: LocationManager

    private var periodicJob: Job? = null

    private val providerSpecs: List<ProviderSpec>
        get() {
            val specs = mutableListOf(
                ProviderSpec(
                    name = LocationManager.GPS_PROVIDER,
                    requiresNetwork = false,
                    requiresSatellite = true,
                    requiresCell = false,
                    supportsAltitude = true,
                    supportsSpeed = true,
                    supportsBearing = true,
                    powerRequirement = Criteria.POWER_HIGH,
                    accuracy = Criteria.ACCURACY_FINE,
                    accuracyMeters = 3f
                ),
                ProviderSpec(
                    name = LocationManager.NETWORK_PROVIDER,
                    requiresNetwork = true,
                    requiresSatellite = false,
                    requiresCell = true,
                    supportsAltitude = false,
                    supportsSpeed = false,
                    supportsBearing = false,
                    powerRequirement = Criteria.POWER_LOW,
                    accuracy = Criteria.ACCURACY_COARSE,
                    accuracyMeters = 25f
                )
            )

            // Android 12+
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                specs += ProviderSpec(
                    name = LocationManager.FUSED_PROVIDER,
                    requiresNetwork = false,
                    requiresSatellite = false,
                    requiresCell = false,
                    supportsAltitude = true,
                    supportsSpeed = true,
                    supportsBearing = true,
                    powerRequirement = Criteria.POWER_MEDIUM,
                    accuracy = Criteria.ACCURACY_FINE,
                    accuracyMeters = 5f
                )
            }

            return specs
        }

    override fun onCreate() {
        super.onCreate()

        locationManager = getSystemService(LocationManager::class.java)

        startForegroundNotification()

        periodicJob = scope.launch {
            while (isActive) {
                publishLocation()
                delay(PUBLISH_INTERVAL)
            }
        }
    }

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int
    ): Int {

        when (intent?.action) {

            ACTION_UPDATE -> {
                scope.launch {
                    publishLocation()
                }
            }

            ACTION_STOP -> {
                scope.launch {
                    synchronized(providerLock) {
                        stopping = true
                        cleanupProvidersLocked()
                    }

                    stopSelf(startId)
                }

                return START_NOT_STICKY
            }
        }

        return START_STICKY
    }

    /**
     * All provider operations are serialized using providerLock.
     */
    private fun publishLocation() {
        synchronized(providerLock) {

            if (stopping) return

            val coordinates = readCoordinates() ?: return

            val (latitude, longitude) = coordinates

            var successCount = 0

            providerSpecs.forEach { spec ->

                try {
                    ensureProviderInitialized(spec)

                    locationManager.setTestProviderEnabled(
                        spec.name,
                        true
                    )

                    val location = createLocation(
                        spec,
                        latitude,
                        longitude
                    )

                    locationManager.setTestProviderLocation(
                        spec.name,
                        location
                    )

                    successCount++

                    Log.d(
                        TAG,
                        "Published ${spec.name}: $latitude, $longitude"
                    )

                } catch (error: SecurityException) {

                    Log.e(
                        TAG,
                        "Mock location permission denied for ${spec.name}. " +
                                "Check mock-location app selection and AppOps.",
                        error
                    )

                } catch (error: Exception) {

                    initializedProviders.remove(spec.name)

                    Log.e(
                        TAG,
                        "Failed to publish ${spec.name}",
                        error
                    )
                }
            }

            if (successCount == 0) {
                Log.e(
                    TAG,
                    "No mock location provider accepted the configured point"
                )
            }
        }
    }

    private fun ensureProviderInitialized(spec: ProviderSpec) {

        if (spec.name in initializedProviders) return

        /*
         * addTestProvider replaces an existing provider with
         * the same name. Do not remove it first.
         */
        locationManager.addTestProvider(
            spec.name,
            spec.requiresNetwork,
            spec.requiresSatellite,
            spec.requiresCell,
            false,
            spec.supportsAltitude,
            spec.supportsSpeed,
            spec.supportsBearing,
            spec.powerRequirement,
            spec.accuracy
        )

        initializedProviders += spec.name

        Log.i(TAG, "Initialized provider: ${spec.name}")
    }

    private fun createLocation(
        spec: ProviderSpec,
        latitude: Double,
        longitude: Double
    ): Location {

        return Location(spec.name).apply {

            this.latitude = latitude
            this.longitude = longitude

            accuracy = spec.accuracyMeters

            time = System.currentTimeMillis()

            elapsedRealtimeNanos =
                SystemClock.elapsedRealtimeNanos()

            if (spec.supportsAltitude) {
                altitude = 0.0
            }

            if (spec.supportsSpeed) {
                speed = 0f
            }

            if (spec.supportsBearing) {
                bearing = 0f
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                if (spec.supportsAltitude) {
                    verticalAccuracyMeters = 3f
                }

                if (spec.supportsSpeed) {
                    speedAccuracyMetersPerSecond = 0.5f
                }

                if (spec.supportsBearing) {
                    bearingAccuracyDegrees = 1f
                }
            }
        }
    }

    private fun readCoordinates(): Pair<Double, Double>? {

        val preferences = getSharedPreferences(
            PREFERENCES_NAME,
            MODE_PRIVATE
        )

        val latitude = preferences
            .getString("latitude", null)
            ?.replace(',', '.')
            ?.toDoubleOrNull()
            ?: return null

        val longitude = preferences
            .getString("longitude", null)
            ?.replace(',', '.')
            ?.toDoubleOrNull()
            ?: return null

        if (!latitude.isFinite() || !longitude.isFinite()) {
            Log.e(TAG, "Coordinates contain NaN or Infinity")
            return null
        }

        if (latitude !in -90.0..90.0) {
            Log.e(TAG, "Invalid latitude: $latitude")
            return null
        }

        if (longitude !in -180.0..180.0) {
            Log.e(TAG, "Invalid longitude: $longitude")
            return null
        }

        return latitude to longitude
    }

    /**
     * Diagnostic only. Does not grant or modify AppOps.
     */
    private fun logMockLocationPermission() {

        runCatching {

            val appOps = getSystemService(AppOpsManager::class.java)

            val mode = appOps.checkOpNoThrow(
                AppOpsManager.OPSTR_MOCK_LOCATION,
                Process.myUid(),
                packageName
            )

            if (mode != AppOpsManager.MODE_ALLOWED) {
                Log.e(
                    TAG,
                    "MOCK_LOCATION AppOp is not allowed. Mode=$mode"
                )
            } else {
                Log.i(TAG, "MOCK_LOCATION AppOp is allowed")
            }

        }.onFailure {
            Log.e(TAG, "Unable to inspect mock location AppOp", it)
        }
    }

    private fun startForegroundNotification() {

        buildForegroundServiceNotificationChannel(
            CHANNEL_ID,
            getString(R.string.location_spoof_channel_name)
        )

        val notification = buildNotification(CHANNEL_ID) {
            setContentTitle(
                getString(R.string.location_spoof_notification_title)
            )

            setContentText(
                getString(R.string.location_spoof_notification_text)
            )

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
    }

    private fun cleanupProvidersLocked() {

        initializedProviders.toList().forEach { provider ->

            runCatching {
                locationManager.removeTestProvider(provider)
            }.onFailure {
                Log.e(
                    TAG,
                    "Failed to remove provider: $provider",
                    it
                )
            }
        }

        initializedProviders.clear()
    }

    override fun onDestroy() {

        stopping = true

        periodicJob?.cancel()
        serviceJob.cancel()

        synchronized(providerLock) {
            cleanupProvidersLocked()
        }

        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    companion object {

        private const val TAG = "LocationSpoofService"

        private const val CHANNEL_ID = "location_spoof"
        private const val NOTIFICATION_ID = 1002

        private const val PREFERENCES_NAME = "location_spoof"

        private const val PUBLISH_INTERVAL = 5_000L

        private const val ACTION_UPDATE =
            "com.xxmrk888ytxx.apexowner.action.UPDATE_MOCK_LOCATION"

        private const val ACTION_STOP =
            "com.xxmrk888ytxx.apexowner.action.STOP_MOCK_LOCATION"

        fun start(context: Context) {

            val intent = Intent(
                context,
                LocationSpoofService::class.java
            )

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        fun stop(context: Context) {

            context.startService(
                Intent(
                    context,
                    LocationSpoofService::class.java
                ).setAction(ACTION_STOP)
            )
        }

        fun update(context: Context) {

            val intent = Intent(
                context,
                LocationSpoofService::class.java
            ).setAction(ACTION_UPDATE)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }
    }
}

