package com.xxmrk888ytxx.apexowner

import android.app.Application
import com.xxmrk888ytxx.apexowner.domain.NavigationManager
import com.xxmrk888ytxx.core.android.logs.Logger
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class ApexOwnerApp : Application() {

    @Inject
    lateinit var navigationManager: NavigationManager

    override fun onCreate() {
        super.onCreate()
        Logger.init(MAIN_DEBUG_TAG)
    }

    private companion object {
        const val MAIN_DEBUG_TAG = "log"
    }
}