package com.xxmrk888ytxx.core.devicepolicy.model

data class PolicyState(
    val operatingMode: PolicyOperatingMode = PolicyOperatingMode.PERSONAL_HOST,
    val isDeviceOwner: Boolean = false,
    val isProfileOwner: Boolean = false,
    val isInsideWorkProfile: Boolean = false,
    val hasWorkProfile: Boolean = false,
    val isWorkProfileQuietMode: Boolean = false
)
