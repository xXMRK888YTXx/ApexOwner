package com.xxmrk888ytxx.core.devicepolicy

import android.content.Intent
import android.os.UserHandle
import kotlinx.coroutines.flow.Flow

interface ProfileOwnerManager {
    val isProfileOwner: Flow<Boolean>
    val hasWorkProfile: Flow<Boolean>
    val isInsideWorkProfile: Boolean
    val isAppDeviceOwner: Boolean
    val isWorkProfileQuietMode: Flow<Boolean>
    val workProfileCreateIntent: Intent

    suspend fun updateProfileOwnerState()
    fun setupCrossProfileFilters()

    fun getWorkProfileUserHandle(): UserHandle?

    suspend fun installExistingPackage(
        packageName: String,
        apkPath: String? = null,
        splitPaths: Array<String>? = null
    ): Result<Unit>
    suspend fun setApplicationHidden(packageName: String, isHidden: Boolean): Result<Unit>
    fun isApplicationHidden(packageName: String): Boolean
    suspend fun setPackageSuspended(packageName: String, isSuspended: Boolean): Result<Unit>
    fun isPackageSuspended(packageName: String): Boolean
    fun isCrossProfilePackage(packageName: String): Boolean
    suspend fun setCrossProfilePackage(packageName: String, isAllowed: Boolean): Result<Unit>
    suspend fun clearApplicationUserData(packageName: String): Result<Unit>

    suspend fun setWorkProfileQuietMode(isQuietMode: Boolean): Result<Unit>
    suspend fun removeWorkProfile(): Result<Unit>
    suspend fun clearDeviceOwner(): Result<Unit>
}
