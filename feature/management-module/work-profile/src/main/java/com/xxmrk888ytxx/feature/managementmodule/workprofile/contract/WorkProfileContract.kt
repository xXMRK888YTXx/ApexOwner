package com.xxmrk888ytxx.feature.managementmodule.workprofile.contract

import com.xxmrk888ytxx.feature.managementmodule.workprofile.model.WorkProfileApp
import kotlinx.coroutines.flow.Flow

interface WorkProfileContract {
    val workProfileApps: Flow<List<WorkProfileApp>>
    val personalApps: Flow<List<WorkProfileApp>>
    val isInsideWorkProfile: Boolean
    val hasWorkProfile: Flow<Boolean>

    suspend fun refresh()
    suspend fun toggleFreeze(packageName: String, shouldFreeze: Boolean): Result<Unit>
    suspend fun cloneApp(packageName: String): Result<Unit>
    suspend fun launchApp(packageName: String): Result<Unit>
    suspend fun openWorkProfileApp(): Result<Unit>
    suspend fun removeWorkProfile(): Result<Unit>
}
