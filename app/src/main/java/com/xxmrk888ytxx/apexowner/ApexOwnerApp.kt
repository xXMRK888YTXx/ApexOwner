package com.xxmrk888ytxx.apexowner

import android.app.Application
import com.xxmrk888ytxx.android.logs.Logger
import com.xxmrk888ytxx.apexowner.domain.NavigationManager
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class ApexOwnerApp : Application() {

    @Inject
    lateinit var navigationManager: NavigationManager

    override fun onCreate() {
        super.onCreate()
        Logger.init(MAIN_DEBUG_TAG)
        Logger.writeDebugLog("onCreate")
    }

    private companion object {
        const val MAIN_DEBUG_TAG = "log"
    }
}