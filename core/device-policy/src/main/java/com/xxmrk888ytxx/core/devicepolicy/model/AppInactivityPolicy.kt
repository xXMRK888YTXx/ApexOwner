package com.xxmrk888ytxx.core.devicepolicy.model

import org.json.JSONObject

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

    fun toJson(): String {
        val obj = JSONObject()
        obj.put("packageName", packageName)
        obj.put("lockAction", lockAction.name)
        obj.put("lockFreezeHours", lockFreezeHours)
        obj.put("lockWipeHours", lockWipeHours)
        obj.put("wipeIfUnusedEnabled", wipeIfUnusedEnabled)
        obj.put("wipeIfUnusedDays", wipeIfUnusedDays)
        obj.put("lastOpenedTimestamp", lastOpenedTimestamp)
        obj.put("lockFreezeMinutes", lockFreezeMinutes)
        obj.put("lockWipeMinutes", lockWipeMinutes)
        obj.put("lastLockFreezeTimestamp", lastLockFreezeTimestamp)
        obj.put("lastLockWipeTimestamp", lastLockWipeTimestamp)
        obj.put("lastUnusedWipeTimestamp", lastUnusedWipeTimestamp)
        return obj.toString()
    }

    companion object {
        fun fromJson(jsonStr: String): AppInactivityPolicy? {
            return try {
                val obj = JSONObject(jsonStr)
                AppInactivityPolicy(
                    packageName = obj.getString("packageName"),
                    lockAction = LockInactivityAction.valueOf(
                        obj.optString("lockAction", LockInactivityAction.NONE.name)
                    ),
                    lockFreezeHours = obj.optInt("lockFreezeHours", 12),
                    lockWipeHours = obj.optInt("lockWipeHours", 12),
                    wipeIfUnusedEnabled = obj.optBoolean("wipeIfUnusedEnabled", false),
                    wipeIfUnusedDays = obj.optInt("wipeIfUnusedDays", 30),
                    lastOpenedTimestamp = obj.optLong("lastOpenedTimestamp", 0L),
                    lockFreezeMinutes = obj.optInt("lockFreezeMinutes", 0),
                    lockWipeMinutes = obj.optInt("lockWipeMinutes", 0),
                    lastLockFreezeTimestamp = obj.optLong("lastLockFreezeTimestamp", 0L),
                    lastLockWipeTimestamp = obj.optLong("lastLockWipeTimestamp", 0L),
                    lastUnusedWipeTimestamp = obj.optLong("lastUnusedWipeTimestamp", 0L)
                )
            } catch (e: Exception) {
                null
            }
        }
    }
}
