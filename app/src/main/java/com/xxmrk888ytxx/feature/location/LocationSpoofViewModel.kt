package com.xxmrk888ytxx.feature.location

import androidx.lifecycle.ViewModel
import com.xxmrk888ytxx.apexowner.location.LocationSpoofManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LocationSpoofViewModel @Inject constructor(
    private val manager: LocationSpoofManager
) : ViewModel() {
    val state = manager.state

    fun setCoordinates(latitude: String, longitude: String) = manager.setCoordinates(latitude, longitude)
    fun applyCoordinates(latitude: String, longitude: String) = manager.applyCoordinates(latitude, longitude)
    fun setEnabled(enabled: Boolean) = manager.toggle(enabled)
    fun refresh() = manager.refresh()
    fun mockLocationSettingsIntent() = manager.openMockLocationSettings()
}
