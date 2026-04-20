package com.xxmrk888ytxx.core.devicepolicy

import android.app.admin.DeviceAdminReceiver
import android.content.Context
import android.content.Intent
import com.xxmrk888ytxx.core.base.android.logs.Logger
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
internal class ApexDeviceOwnerReceiver : DeviceAdminReceiver() {

    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    @Inject
    lateinit var deviceOwnerManager: DeviceOwnerManager

    override fun onEnabled(context: Context, intent: Intent) {
        super.onEnabled(context, intent)
        Logger.writeDebugLog("onEnabled")
        scope.launch { deviceOwnerManager.updateDeviceOwnerState() }
    }

    override fun onDisabled(context: Context, intent: Intent) {
        super.onDisabled(context, intent)
        Logger.writeDebugLog("onDisabled")
        scope.launch { deviceOwnerManager.updateDeviceOwnerState() }
    }
}