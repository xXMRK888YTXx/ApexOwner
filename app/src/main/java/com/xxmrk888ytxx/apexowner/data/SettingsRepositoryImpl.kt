package com.xxmrk888ytxx.apexowner.data

import androidx.datastore.preferences.core.booleanPreferencesKey
import com.xxmrk888ytxx.apexowner.domain.SettingsRepository
import com.xxmrk888ytxx.apexowner.domain.model.ApexOwnerSettings
import com.xxmrk888ytxx.preferencesstorage.PreferencesStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SettingsRepositoryImpl @Inject constructor(
    private val preferencesStorage: PreferencesStorage
) : SettingsRepository {

    private val isOnboardingPassedKey = booleanPreferencesKey("is_onboarding_passed")

    override val settings: Flow<ApexOwnerSettings> =
        preferencesStorage.getProperty(isOnboardingPassedKey, false).map {
            ApexOwnerSettings(it)
        }

    override suspend fun markOnboardingAsPassed() = withContext(Dispatchers.IO) {
        preferencesStorage.writeProperty(isOnboardingPassedKey, true)
    }
}