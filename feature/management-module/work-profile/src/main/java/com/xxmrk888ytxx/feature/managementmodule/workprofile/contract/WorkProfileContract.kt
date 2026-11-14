package com.xxmrk888ytxx.feature.managementmodule.workprofile.contract

import com.xxmrk888ytxx.feature.managementmodule.workprofile.model.WorkProfileApp
import kotlinx.coroutines.flow.Flow

interface WorkProfileContract {
    val apps: Flow<List<WorkProfileApp>>
    val workProfileApps: Flow<List<WorkProfileApp>>
    val personalApps: Flow<List<WorkProfileApp>>
    val isInsideWorkProfile: Boolean
    val hasWorkProfile: Flow<Boolean>
    val isDeviceOwner: Boolean
    val canFreezeInCurrentProfile: Boolean

    suspend fun refresh()
    suspend fun toggleFreeze(packageName: String, shouldFreeze: Boolean): Result<Unit>
    suspend fun cloneApp(packageName: String): Result<Unit>
    suspend fun launchApp(packageName: String): Result<Unit>
    suspend fun uninstallApp(packageName: String): Result<Unit>
    suspend fun openWorkProfileApp(): Result<Unit>
    suspend fun removeWorkProfile(): Result<Unit>
    suspend fun openAppPermissions(packageName: String, appName: String)
    suspend fun toggleOneShot(packageName: String, isOneShot: Boolean): Result<Unit>
    suspend fun resetOneShot(packageName: String): Result<Unit>
    suspend fun toggleAutoFreeze(packageName: String, isAutoFreeze: Boolean): Result<Unit>
    suspend fun setInactivityPolicy(policy: com.xxmrk888ytxx.feature.managementmodule.workprofile.model.AppInactivityPolicy): Result<Unit>
}
