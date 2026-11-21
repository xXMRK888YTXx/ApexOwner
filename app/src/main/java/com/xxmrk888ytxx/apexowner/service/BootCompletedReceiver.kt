package com.xxmrk888ytxx.apexowner.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.xxmrk888ytxx.core.base.android.logs.Logger
import com.xxmrk888ytxx.apexowner.location.LocationSpoofService

class BootCompletedReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        Logger.writeDebugLog("BootCompletedReceiver: received action=$action")
        if (
            action == Intent.ACTION_BOOT_COMPLETED ||
            action == Intent.ACTION_LOCKED_BOOT_COMPLETED ||
            action == Intent.ACTION_USER_UNLOCKED
        ) {
            ApexPolicyService.start(context)
            if (context.getSharedPreferences("location_spoof", Context.MODE_PRIVATE).getBoolean("enabled", false)) {
                LocationSpoofService.start(context)
            }
        }
    }
}
