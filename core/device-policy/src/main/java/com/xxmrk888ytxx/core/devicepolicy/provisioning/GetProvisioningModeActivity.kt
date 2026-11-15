package com.xxmrk888ytxx.core.devicepolicy.provisioning

import android.app.Activity
import android.app.admin.DevicePolicyManager
import android.content.Intent
import android.os.Build
import android.os.Bundle

/**
 * Activity for Android 12+ (API 31+) provisioning flow.
 * When the system initiates provisioning for a Work Profile, it queries this activity
 * to retrieve the intended provisioning mode.
 */
class GetProvisioningModeActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val resultIntent = Intent().apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                putExtra(
                    DevicePolicyManager.EXTRA_PROVISIONING_MODE,
                    DevicePolicyManager.PROVISIONING_MODE_MANAGED_PROFILE
                )
            }
        }
        setResult(RESULT_OK, resultIntent)
        finish()
    }
}
