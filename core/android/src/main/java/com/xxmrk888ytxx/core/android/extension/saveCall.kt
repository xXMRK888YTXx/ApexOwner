package com.xxmrk888ytxx.core.android.extension

import com.xxmrk888ytxx.core.android.logs.Logger
import kotlinx.coroutines.CancellationException

inline fun saveCall(isPrintToDebug: Boolean = true, block: () -> Unit) {
    try {
        block()
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        if (isPrintToDebug)
            Logger.writeDebugLog(e)
    }
}