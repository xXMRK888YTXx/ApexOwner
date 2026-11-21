package com.xxmrk888ytxx.apexowner.data

import android.util.Base64
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.xxmrk888ytxx.apexowner.domain.AppLockRepository
import com.xxmrk888ytxx.common.preferencesstorage.PreferencesStorage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec
import javax.inject.Inject

class AppLockRepositoryImpl @Inject constructor(
    private val preferencesStorage: PreferencesStorage
) : AppLockRepository {
    override val hasPassword: Flow<Boolean> =
        preferencesStorage.isPropertyExist(passwordHashKey)

    override val biometricEnabled: Flow<Boolean> =
        preferencesStorage.getProperty(biometricEnabledKey, false)

    override suspend fun setPassword(password: String) {
        val salt = ByteArray(SALT_SIZE).also(SecureRandom()::nextBytes)
        preferencesStorage.writeProperty(passwordSaltKey, encode(salt))
        preferencesStorage.writeProperty(passwordHashKey, encode(deriveKey(password, salt)))
    }

    override suspend fun clearPassword() {
        preferencesStorage.removeProperty(passwordSaltKey)
        preferencesStorage.removeProperty(passwordHashKey)
        preferencesStorage.writeProperty(biometricEnabledKey, false)
    }

    override suspend fun verifyPassword(password: String): Boolean {
        val salt = preferencesStorage.getPropertyOrNull(passwordSaltKey)
            .map { it?.let(::decode) }
        val hash = preferencesStorage.getPropertyOrNull(passwordHashKey)
            .map { it?.let(::decode) }
        var result = false
        val (storedSalt, storedHash) = kotlinx.coroutines.flow.combine(salt, hash) { storedSalt, storedHash ->
            storedSalt to storedHash
        }.first()
        if (storedSalt != null && storedHash != null) {
            result = MessageDigest.isEqual(storedHash, deriveKey(password, storedSalt))
        }
        return result
    }

    override suspend fun setBiometricEnabled(enabled: Boolean) {
        preferencesStorage.writeProperty(biometricEnabledKey, enabled)
    }

    private fun deriveKey(password: String, salt: ByteArray): ByteArray {
        val spec = PBEKeySpec(password.toCharArray(), salt, ITERATIONS, KEY_LENGTH_BITS)
        return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
            .generateSecret(spec)
            .encoded
    }

    private fun encode(value: ByteArray): String = Base64.encodeToString(value, Base64.NO_WRAP)

    private fun decode(value: String): ByteArray = Base64.decode(value, Base64.NO_WRAP)

    private companion object {
        const val SALT_SIZE = 16
        const val ITERATIONS = 150_000
        const val KEY_LENGTH_BITS = 256
        val passwordSaltKey = stringPreferencesKey("app_lock_password_salt")
        val passwordHashKey = stringPreferencesKey("app_lock_password_hash")
        val biometricEnabledKey = booleanPreferencesKey("app_lock_biometric_enabled")
    }
}
