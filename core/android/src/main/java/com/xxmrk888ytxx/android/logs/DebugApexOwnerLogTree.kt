package com.xxmrk888ytxx.android.logs

import timber.log.Timber

internal class DebugApexOwnerLogTree(private val globalTag: String?) : Timber.DebugTree() {
    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        // 1. Get the entire call stack
        val stackTrace = Throwable().stackTrace

        // 2. Find the first element in the stack that doesn't belong to Timber or our Logger wrapper.
        // This identifies the exact location where Logger.writeDebugLog was called.
        val element = stackTrace.firstOrNull { ste ->
            !ste.className.contains("Timber") &&
                    !ste.className.contains("LogTree") &&
                    !ste.className.contains(".Logger")
        }

        // 3. Create a clickable prefix: (File.kt:Line)#Method
        // Android Studio recognizes this format and makes it a direct link to the code.
        val callSite = element?.let {
            "(${it.fileName}:${it.lineNumber})#${it.methodName}"
        } ?: "UnknownSource"

        // 4. Combine the call site information with the original message
        val finalMessage = "$callSite: $message"

        // 5. Send to the system.
        // Use globalTag if provided, otherwise fallback to the tag calculated by Timber.
        super.log(priority, globalTag ?: tag, finalMessage, t)
    }
}