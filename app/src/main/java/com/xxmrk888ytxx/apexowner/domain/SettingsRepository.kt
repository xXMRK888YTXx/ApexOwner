package com.xxmrk888ytxx.apexowner.domain

import com.xxmrk888ytxx.apexowner.domain.model.ApexOwnerSettings
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    val settings: Flow<ApexOwnerSettings>
    suspend fun markOnboardingAsPassed()
}