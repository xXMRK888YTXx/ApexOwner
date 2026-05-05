package com.xxmrk888ytxx.core.base.android.logs

import timber.log.Timber

object Logger {

    private var mainTag: String? = null

    fun init(mainTag: String? = null) {
        this.mainTag = mainTag
        // TODO Replace in release build
        Timber.plant(DebugApexOwnerLogTree(mainTag))
    }

    fun writeDebugLog(message: Any) {
        when (message) {
            is Throwable -> Timber.d(message)
            is String -> Timber.d(message)
            else -> Timber.d(message.toString())
        }
    }
}