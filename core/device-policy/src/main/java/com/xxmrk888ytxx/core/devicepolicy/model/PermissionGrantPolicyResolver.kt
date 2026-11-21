package com.xxmrk888ytxx.core.devicepolicy.model

import android.app.admin.DevicePolicyManager

/** Resolves an app policy to the DPM grant state, including global-deny behavior. */
object PermissionGrantPolicyResolver {
    fun resolve(
        policy: AppPermissionPolicy,
        isGloballyDisabled: Boolean,
        applyDefaultPolicy: Boolean = true,
    ): Int? = when (policy) {
        AppPermissionPolicy.FORCE_GRANTED -> DevicePolicyManager.PERMISSION_GRANT_STATE_GRANTED
        AppPermissionPolicy.FORCE_DENIED -> DevicePolicyManager.PERMISSION_GRANT_STATE_DENIED
        AppPermissionPolicy.USER_CONTROLLED -> DevicePolicyManager.PERMISSION_GRANT_STATE_DEFAULT
        AppPermissionPolicy.DEFAULT -> when {
            isGloballyDisabled -> DevicePolicyManager.PERMISSION_GRANT_STATE_DENIED
            applyDefaultPolicy -> DevicePolicyManager.PERMISSION_GRANT_STATE_DEFAULT
            else -> null
        }
    }
}
