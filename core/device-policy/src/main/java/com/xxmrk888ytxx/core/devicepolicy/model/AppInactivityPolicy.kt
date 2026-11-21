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
    val freezeIfUnusedEnabled: Boolean = false,
    val freezeIfUnusedDays: Int = 30,
    val freezeIfUnusedHours: Int = 0,
    val freezeIfUnusedMinutes: Int = 0,
    val wipeIfUnusedEnabled: Boolean = false,
    val wipeIfUnusedDays: Int = 30,
    val wipeIfUnusedHours: Int = 0,
    val wipeIfUnusedMinutes: Int = 0,
    val lastOpenedTimestamp: Long = 0L,
    val lockFreezeMinutes: Int = 0,
    val lockWipeMinutes: Int = 0,
    val lastLockFreezeTimestamp: Long = 0L,
    val lastLockWipeTimestamp: Long = 0L,
    val lastUnusedWipeTimestamp: Long = 0L,
    val lastUnusedFreezeTimestamp: Long = 0L
) {
    val isConfigured: Boolean
        get() = lockAction != LockInactivityAction.NONE || freezeIfUnusedEnabled || wipeIfUnusedEnabled

    val lockFreezeDurationMs: Long
        get() = if (lockFreezeMinutes > 0) lockFreezeMinutes * 60_000L else lockFreezeHours * 3600_000L

    val lockWipeDurationMs: Long
        get() = if (lockWipeMinutes > 0) lockWipeMinutes * 60_000L else lockWipeHours * 3600_000L

    val unusedWipeDurationMs: Long
        get() = when {
            wipeIfUnusedMinutes > 0 -> wipeIfUnusedMinutes * 60_000L
            wipeIfUnusedHours > 0 -> wipeIfUnusedHours * 3600_000L
            else -> wipeIfUnusedDays * 86400_000L
        }

    val unusedFreezeDurationMs: Long
        get() = when {
            freezeIfUnusedMinutes > 0 -> freezeIfUnusedMinutes * 60_000L
            freezeIfUnusedHours > 0 -> freezeIfUnusedHours * 3600_000L
            else -> freezeIfUnusedDays * 86400_000L
        }

    fun toJson(): String {
        val obj = JSONObject()
        obj.put("packageName", packageName)
        obj.put("lockAction", lockAction.name)
        obj.put("lockFreezeHours", lockFreezeHours)
        obj.put("lockWipeHours", lockWipeHours)
        obj.put("freezeIfUnusedEnabled", freezeIfUnusedEnabled)
        obj.put("freezeIfUnusedDays", freezeIfUnusedDays)
        obj.put("freezeIfUnusedHours", freezeIfUnusedHours)
        obj.put("freezeIfUnusedMinutes", freezeIfUnusedMinutes)
        obj.put("wipeIfUnusedEnabled", wipeIfUnusedEnabled)
        obj.put("wipeIfUnusedDays", wipeIfUnusedDays)
        obj.put("wipeIfUnusedHours", wipeIfUnusedHours)
        obj.put("wipeIfUnusedMinutes", wipeIfUnusedMinutes)
        obj.put("lastOpenedTimestamp", lastOpenedTimestamp)
        obj.put("lockFreezeMinutes", lockFreezeMinutes)
        obj.put("lockWipeMinutes", lockWipeMinutes)
        obj.put("lastLockFreezeTimestamp", lastLockFreezeTimestamp)
        obj.put("lastLockWipeTimestamp", lastLockWipeTimestamp)
        obj.put("lastUnusedWipeTimestamp", lastUnusedWipeTimestamp)
        obj.put("lastUnusedFreezeTimestamp", lastUnusedFreezeTimestamp)
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
                    freezeIfUnusedEnabled = obj.optBoolean("freezeIfUnusedEnabled", false),
                    freezeIfUnusedDays = obj.optInt("freezeIfUnusedDays", 30),
                    freezeIfUnusedHours = obj.optInt("freezeIfUnusedHours", 0),
                    freezeIfUnusedMinutes = obj.optInt("freezeIfUnusedMinutes", 0),
                    wipeIfUnusedEnabled = obj.optBoolean("wipeIfUnusedEnabled", false),
                    wipeIfUnusedDays = obj.optInt("wipeIfUnusedDays", 30),
                    wipeIfUnusedHours = obj.optInt("wipeIfUnusedHours", 0),
                    wipeIfUnusedMinutes = obj.optInt("wipeIfUnusedMinutes", 0),
                    lastOpenedTimestamp = obj.optLong("lastOpenedTimestamp", 0L),
                    lockFreezeMinutes = obj.optInt("lockFreezeMinutes", 0),
                    lockWipeMinutes = obj.optInt("lockWipeMinutes", 0),
                    lastLockFreezeTimestamp = obj.optLong("lastLockFreezeTimestamp", 0L),
                    lastLockWipeTimestamp = obj.optLong("lastLockWipeTimestamp", 0L),
                    lastUnusedWipeTimestamp = obj.optLong("lastUnusedWipeTimestamp", 0L),
                    lastUnusedFreezeTimestamp = obj.optLong("lastUnusedFreezeTimestamp", 0L)
                )
            } catch (e: Exception) {
                null
            }
        }
    }
}
