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
    val lastOpenedTimestamp: Long = 0L
) {
    val isConfigured: Boolean
        get() = lockAction != LockInactivityAction.NONE || wipeIfUnusedEnabled

    fun toJson(): String {
        val obj = JSONObject()
        obj.put("packageName", packageName)
        obj.put("lockAction", lockAction.name)
        obj.put("lockFreezeHours", lockFreezeHours)
        obj.put("lockWipeHours", lockWipeHours)
        obj.put("wipeIfUnusedEnabled", wipeIfUnusedEnabled)
        obj.put("wipeIfUnusedDays", wipeIfUnusedDays)
        obj.put("lastOpenedTimestamp", lastOpenedTimestamp)
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
                    lastOpenedTimestamp = obj.optLong("lastOpenedTimestamp", 0L)
                )
            } catch (e: Exception) {
                null
            }
        }
    }
}
