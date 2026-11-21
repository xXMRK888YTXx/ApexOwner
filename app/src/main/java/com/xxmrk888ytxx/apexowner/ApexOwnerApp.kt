package com.xxmrk888ytxx.apexowner

import android.app.Application
import com.xxmrk888ytxx.apexowner.domain.NavigationManager
import com.xxmrk888ytxx.apexowner.service.ApexPolicyService
import com.xxmrk888ytxx.apexowner.location.LocationSpoofManager
import com.xxmrk888ytxx.apexowner.location.LocationSpoofService
import com.xxmrk888ytxx.core.base.android.logs.Logger
import com.xxmrk888ytxx.core.devicepolicy.ProfileOwnerManager
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class ApexOwnerApp : Application() {

    @Inject
    lateinit var navigationManager: NavigationManager

    @Inject
    lateinit var profileOwnerManager: ProfileOwnerManager

    @Inject
    lateinit var locationSpoofManager: LocationSpoofManager

    override fun onCreate() {
        super.onCreate()
        Logger.init(MAIN_DEBUG_TAG)
        profileOwnerManager.setupCrossProfileFilters()
        ApexPolicyService.start(this)
        val locationState = locationSpoofManager.state.value
        if (locationState.enabled && locationState.isMockLocationApp && locationState.hasLocationPermission) {
            LocationSpoofService.start(this)
        }
    }

    private companion object {
        const val MAIN_DEBUG_TAG = "log"
    }
}
