package com.xxmrk888ytxx.apexowner.domain

import kotlinx.coroutines.flow.Flow

interface AppLockRepository {
    val hasPassword: Flow<Boolean>
    val biometricEnabled: Flow<Boolean>

    suspend fun setPassword(password: String)
    suspend fun clearPassword()
    suspend fun verifyPassword(password: String): Boolean
    suspend fun setBiometricEnabled(enabled: Boolean)
}
