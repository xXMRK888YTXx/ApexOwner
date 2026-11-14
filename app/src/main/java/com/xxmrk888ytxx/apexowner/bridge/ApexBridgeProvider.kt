package com.xxmrk888ytxx.apexowner.bridge

import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import com.xxmrk888ytxx.core.base.android.logs.Logger
import com.xxmrk888ytxx.core.devicepolicy.ProfileOwnerManager
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.runBlocking

class ApexBridgeProvider : ContentProvider() {

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface BridgeEntryPoint {
        fun profileOwnerManager(): ProfileOwnerManager
    }

    override fun onCreate(): Boolean = true

    override fun call(method: String, arg: String?, extras: Bundle?): Bundle? {
        val appContext = context?.applicationContext ?: return null
        val entryPoint = EntryPointAccessors.fromApplication(appContext, BridgeEntryPoint::class.java)
        val profileOwnerManager = entryPoint.profileOwnerManager()

        val response = Bundle()
        when (method) {
            METHOD_CLONE_APP -> {
                val packageName = arg ?: return null
                Logger.writeDebugLog("ApexBridgeProvider: received clone request for $packageName")
                val result = runBlocking {
                    profileOwnerManager.installExistingPackage(packageName)
                }
                response.putBoolean(EXTRA_SUCCESS, result.isSuccess)
                if (result.isFailure) {
                    val errorMsg = result.exceptionOrNull()?.message ?: "Unknown error"
                    Logger.writeErrorLog("ApexBridgeProvider: failed to clone $packageName: $errorMsg")
                    response.putString(EXTRA_ERROR, errorMsg)
                } else {
                    Logger.writeDebugLog("ApexBridgeProvider: successfully cloned $packageName")
                }
            }
            METHOD_PING -> {
                response.putBoolean(EXTRA_SUCCESS, true)
            }
        }
        return response
    }

    override fun query(uri: Uri, p1: Array<out String>?, p2: String?, p3: Array<out String>?, p4: String?): Cursor? = null
    override fun getType(uri: Uri): String? = null
    override fun insert(uri: Uri, values: ContentValues?): Uri? = null
    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int = 0
    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<out String>?): Int = 0

    companion object {
        const val AUTHORITY = "com.xxmrk888ytxx.apexowner.bridge"
        const val METHOD_CLONE_APP = "cloneApp"
        const val METHOD_PING = "ping"
        const val EXTRA_SUCCESS = "extra_success"
        const val EXTRA_ERROR = "extra_error"
    }
}
