package com.xxmrk888ytxx.apexowner

import android.app.Application
import android.os.UserManager
import com.xxmrk888ytxx.apexowner.location.LocationSpoofManager
import com.xxmrk888ytxx.apexowner.location.LocationSpoofService
import com.xxmrk888ytxx.apexowner.domain.NavigationManager
import com.xxmrk888ytxx.apexowner.service.ApexPolicyService
import com.xxmrk888ytxx.core.base.android.logs.Logger
import com.xxmrk888ytxx.core.devicepolicy.ProfileOwnerManager
import dagger.hilt.android.HiltAndroidApp
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@HiltAndroidApp
class ApexOwnerApp : Application() {
    override fun onCreate() {
        super.onCreate()
        Logger.init(MAIN_DEBUG_TAG)
        if (!getSystemService(UserManager::class.java).isUserUnlocked) return

        val entryPoint = EntryPointAccessors.fromApplication(this, AppEntryPoint::class.java)
        entryPoint.profileOwnerManager().setupCrossProfileFilters()
        ApexPolicyService.start(this)
        val locationState = entryPoint.locationSpoofManager().state.value
        if (locationState.enabled && locationState.isMockLocationApp && locationState.hasLocationPermission) {
            LocationSpoofService.start(this)
        }
    }

    private companion object {
        const val MAIN_DEBUG_TAG = "log"
    }
}

@EntryPoint
@InstallIn(SingletonComponent::class)
interface AppEntryPoint {
    fun profileOwnerManager(): ProfileOwnerManager
    fun locationSpoofManager(): LocationSpoofManager
}
