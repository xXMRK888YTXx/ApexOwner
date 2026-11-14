package com.xxmrk888ytxx.apexowner.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.xxmrk888ytxx.core.base.android.logs.Logger

class InactivityAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        Logger.writeDebugLog("InactivityAlarmReceiver: received action=$action")
        if (action == ACTION_CHECK_INACTIVITY) {
            ApexPolicyService.triggerInactivityCheck(context)
        }
    }

    companion object {
        const val ACTION_CHECK_INACTIVITY = "com.xxmrk888ytxx.apexowner.action.CHECK_INACTIVITY"
    }
}
