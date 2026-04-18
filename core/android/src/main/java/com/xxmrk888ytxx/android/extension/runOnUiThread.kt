package com.xxmrk888ytxx.android.extension

import android.os.Handler
import android.os.Looper

private val handler by lazy {
    Handler(Looper.getMainLooper())
}

fun runOnUiThread(action:() -> Unit) {
    handler.post(action)
}