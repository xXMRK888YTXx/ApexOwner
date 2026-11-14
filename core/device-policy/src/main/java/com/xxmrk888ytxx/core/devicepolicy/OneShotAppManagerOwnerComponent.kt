package com.xxmrk888ytxx.core.devicepolicy

import kotlinx.coroutines.flow.Flow

interface OneShotAppManagerOwnerComponent {
    val oneShotAppPackages: Flow<Set<String>>
    val autoFreezeAppPackages: Flow<Set<String>>

    fun isOneShotApp(packageName: String): Boolean
    fun isAutoFreezeApp(packageName: String): Boolean

    suspend fun setOneShotApp(packageName: String, isOneShot: Boolean): Result<Unit>
    suspend fun setAutoFreezeApp(packageName: String, isAutoFreeze: Boolean): Result<Unit>

    suspend fun launchOneShotApp(packageName: String): Result<Unit>
    suspend fun resetOneShotApp(packageName: String): Result<Unit>
    suspend fun resetOneShotApps(): Result<Unit>

    suspend fun freezeAutoFreezeApp(packageName: String): Result<Unit>
    suspend fun freezeAutoFreezeApps(): Result<Unit>

    suspend fun handleLifecycleTriggers(): Result<Unit>
}
