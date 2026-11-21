package com.xxmrk888ytxx.apexowner.location

import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.provider.Settings
import androidx.core.content.ContextCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class LocationSpoofState(
    val latitude: String = "",
    val longitude: String = "",
    val enabled: Boolean = false,
    val isMockLocationApp: Boolean = false,
    val hasLocationPermission: Boolean = false,
    val error: LocationSpoofError? = null
)

enum class LocationSpoofError {
    INVALID_COORDINATES,
    MOCK_LOCATION_APP_REQUIRED,
    LOCATION_PERMISSION_REQUIRED
}

@Singleton
class LocationSpoofManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val preferences = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE)
    private val _state = MutableStateFlow(readState())
    val state: StateFlow<LocationSpoofState> = _state

    fun setCoordinates(latitude: String, longitude: String) {
        val lat = latitude.trim().replace(',', '.').toDoubleOrNull()
        val lon = longitude.trim().replace(',', '.').toDoubleOrNull()
        if (lat == null || lon == null || lat !in -90.0..90.0 || lon !in -180.0..180.0) {
            _state.value = _state.value.copy(latitude = latitude, longitude = longitude, error = LocationSpoofError.INVALID_COORDINATES)
            return
        }
        preferences.edit().putString(KEY_LATITUDE, latitude.trim()).putString(KEY_LONGITUDE, longitude.trim()).apply()
        _state.value = readState()
    }

    fun applyCoordinates(latitude: String, longitude: String) {
        setCoordinates(latitude, longitude)
        val updated = _state.value
        if (updated.error == null && updated.enabled) {
            LocationSpoofService.stop(context)
            LocationSpoofService.start(context)
        }
    }

    fun clearError() {
        _state.value = _state.value.copy(error = null)
    }

    fun toggle(enabled: Boolean) {
        val current = readState()
        if (enabled) {
            val lat = current.latitude.replace(',', '.').toDoubleOrNull()
            val lon = current.longitude.replace(',', '.').toDoubleOrNull()
            if (lat == null || lon == null || lat !in -90.0..90.0 || lon !in -180.0..180.0) {
                _state.value = current.copy(error = LocationSpoofError.INVALID_COORDINATES)
                return
            }
            if (!current.hasLocationPermission) {
                _state.value = current.copy(error = LocationSpoofError.LOCATION_PERMISSION_REQUIRED)
                return
            }
            if (!current.isMockLocationApp) {
                _state.value = current.copy(error = LocationSpoofError.MOCK_LOCATION_APP_REQUIRED)
                return
            }
            preferences.edit().putBoolean(KEY_ENABLED, true).apply()
            LocationSpoofService.start(context)
        } else {
            preferences.edit().putBoolean(KEY_ENABLED, false).apply()
            LocationSpoofService.stop(context)
        }
        _state.value = readState()
    }

    fun refresh() {
        _state.value = readState()
    }

    fun openMockLocationSettings(): Intent = Intent("android.settings.MOCK_LOCATION_SETTINGS")

    private fun readState(): LocationSpoofState {
        val hasPermission = ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val appOps = context.getSystemService(AppOpsManager::class.java)
        val mockAllowed = if (android.os.Build.VERSION.SDK_INT >= 29) {
            appOps?.unsafeCheckOpNoThrow(AppOpsManager.OPSTR_MOCK_LOCATION, android.os.Process.myUid(), context.packageName) == AppOpsManager.MODE_ALLOWED
        } else {
            Settings.Secure.getInt(context.contentResolver, "mock_location", 0) == 1
        }
        return LocationSpoofState(
            latitude = preferences.getString(KEY_LATITUDE, "") ?: "",
            longitude = preferences.getString(KEY_LONGITUDE, "") ?: "",
            enabled = preferences.getBoolean(KEY_ENABLED, false),
            isMockLocationApp = mockAllowed,
            hasLocationPermission = hasPermission
        )
    }

    private companion object {
        const val PREFERENCES = "location_spoof"
        const val KEY_LATITUDE = "latitude"
        const val KEY_LONGITUDE = "longitude"
        const val KEY_ENABLED = "enabled"
    }
}
