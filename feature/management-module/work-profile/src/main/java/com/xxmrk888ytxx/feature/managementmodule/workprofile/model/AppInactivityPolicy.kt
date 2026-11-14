package com.xxmrk888ytxx.feature.managementmodule.workprofile.model

enum class LockInactivityAction {
    NONE,
    FREEZE_ONLY,
    FREEZE_AND_WIPE
}

data class AppInactivityPolicy(
    val packageName: String,
    val lockAction: LockInactivityAction = LockInactivityAction.NONE,
    val lockFreezeHours: Int = 12,
    val lockWipeHours: Int = 12,
    val wipeIfUnusedEnabled: Boolean = false,
    val wipeIfUnusedDays: Int = 30,
    val lastOpenedTimestamp: Long = 0L,
    val lockFreezeMinutes: Int = 0,
    val lockWipeMinutes: Int = 0,
    val lastLockFreezeTimestamp: Long = 0L,
    val lastLockWipeTimestamp: Long = 0L,
    val lastUnusedWipeTimestamp: Long = 0L
) {
    val isConfigured: Boolean
        get() = lockAction != LockInactivityAction.NONE || wipeIfUnusedEnabled

    val lockFreezeDurationMs: Long
        get() = if (lockFreezeMinutes > 0) lockFreezeMinutes * 60_000L else lockFreezeHours * 3600_000L

    val lockWipeDurationMs: Long
        get() = if (lockWipeMinutes > 0) lockWipeMinutes * 60_000L else lockWipeHours * 3600_000L

    val unusedWipeDurationMs: Long
        get() = wipeIfUnusedDays * 86400_000L
}
