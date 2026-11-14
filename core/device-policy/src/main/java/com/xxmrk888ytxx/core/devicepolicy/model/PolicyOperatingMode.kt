package com.xxmrk888ytxx.core.devicepolicy.model

/**
 * Operating mode of the application in the system.
 */
enum class PolicyOperatingMode {
    /**
     * Running in the personal profile without admin privileges.
     * Can create a work profile or guide the user.
     */
    PERSONAL_HOST,

    /**
     * Running inside the managed work profile as Profile Owner (Shelter mode).
     * Can freeze, unfreeze, clone apps and manage the profile.
     */
    PROFILE_OWNER,

    /**
     * Running with full Device Owner privileges across the entire device.
     */
    DEVICE_OWNER
}
