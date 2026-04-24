package com.xxmrk888ytxx.feature.managementmodule.apprestriction.contract

import kotlinx.coroutines.flow.Flow

interface RestrictionManageContract {
    val isCameraDisabled: Flow<Boolean>
    val isMicrophoneDisabled: Flow<Boolean>
    val isUSBDataSignalDisabled: Flow<Boolean>
    val isUSBFileTransferDisabled: Flow<Boolean>
    val isInstallAppsDisabled: Flow<Boolean>
    val isInstallAppsFromUnknownSourcesDisabled: Flow<Boolean>
    val isUninstallAppsDisabled: Flow<Boolean>
    val isAppControlDisabled: Flow<Boolean>
    val isScreenContentCaptureForAIDisabled: Flow<Boolean>
    val isContentSuggestionDisabled: Flow<Boolean>
    val isScreenshotsDisabled: Flow<Boolean>
    val isDebugFeaturesDisabled: Flow<Boolean>
    val isFactoryResetDisabled: Flow<Boolean>
    val isSafeBootDisabled: Flow<Boolean>
    val isCanDisableUSBDataSignal: Boolean
    val isCanDisableScreenContentCaptureForAI: Boolean
    val isCanDisableContentSuggestion: Boolean
    suspend fun setCameraDisabled(isDisabled: Boolean) : Result<Unit>
    suspend fun setMicrophoneDisabled(isDisabled: Boolean): Result<Unit>
    suspend fun setUSBDataSignalDisabled(isDisabled: Boolean): Result<Unit>
    suspend fun setUSBFileTransferDisabled(isDisabled: Boolean): Result<Unit>
    suspend fun setInstallAppsDisabled(isDisabled: Boolean): Result<Unit>
    suspend fun setInstallAppsFromUnknownSourcesDisabled(isDisabled: Boolean): Result<Unit>
    suspend fun setUninstallAppsDisabled(isDisabled: Boolean): Result<Unit>
    suspend fun setAppControlDisabled(isDisabled: Boolean): Result<Unit>
    suspend fun setScreenContentCaptureForAIDisabled(isDisabled: Boolean): Result<Unit>
    suspend fun setContentSuggestionDisabled(isDisabled: Boolean): Result<Unit>
    suspend fun setScreenshotsDisabled(isDisabled: Boolean): Result<Unit>
    suspend fun setDebugFeaturesDisabled(isDisabled: Boolean): Result<Unit>
    suspend fun setFactoryResetDisabled(isDisabled: Boolean): Result<Unit>
    suspend fun setSafeBootDisabled(isDisabled: Boolean): Result<Unit>
}