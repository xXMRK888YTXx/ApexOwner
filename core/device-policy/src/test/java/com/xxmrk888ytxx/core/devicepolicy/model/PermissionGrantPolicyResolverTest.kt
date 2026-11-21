package com.xxmrk888ytxx.core.devicepolicy.model

import android.app.admin.DevicePolicyManager
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class PermissionGrantPolicyResolverTest {
    @Test
    fun defaultFollowsGlobalPolicy() {
        assertEquals(
            DevicePolicyManager.PERMISSION_GRANT_STATE_DENIED,
            PermissionGrantPolicyResolver.resolve(AppPermissionPolicy.DEFAULT, isGloballyDisabled = true),
        )
        assertEquals(
            DevicePolicyManager.PERMISSION_GRANT_STATE_DEFAULT,
            PermissionGrantPolicyResolver.resolve(AppPermissionPolicy.DEFAULT, isGloballyDisabled = false),
        )
        assertNull(
            PermissionGrantPolicyResolver.resolve(
                AppPermissionPolicy.DEFAULT,
                isGloballyDisabled = false,
                applyDefaultPolicy = false,
            )
        )
    }

    @Test
    fun userControlledIgnoresGlobalDeny() {
        assertEquals(
            DevicePolicyManager.PERMISSION_GRANT_STATE_DEFAULT,
            PermissionGrantPolicyResolver.resolve(AppPermissionPolicy.USER_CONTROLLED, isGloballyDisabled = true),
        )
    }

    @Test
    fun explicitPoliciesAlwaysWin() {
        assertEquals(
            DevicePolicyManager.PERMISSION_GRANT_STATE_DENIED,
            PermissionGrantPolicyResolver.resolve(AppPermissionPolicy.FORCE_DENIED, isGloballyDisabled = false),
        )
        assertEquals(
            DevicePolicyManager.PERMISSION_GRANT_STATE_GRANTED,
            PermissionGrantPolicyResolver.resolve(AppPermissionPolicy.FORCE_GRANTED, isGloballyDisabled = true),
        )
    }
}
