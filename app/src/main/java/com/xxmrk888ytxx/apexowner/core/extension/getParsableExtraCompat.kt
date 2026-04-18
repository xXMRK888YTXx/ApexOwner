package com.xxmrk888ytxx.apexowner.core.extension

import android.content.Intent
import android.os.Build

@Suppress("DEPRECATION")
fun <T> Intent.getParsableExtraCompat(name: String, clazz: Class<T>): T? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        getParcelableExtra(name, clazz)
    } else {
        getParcelableExtra(name)
    }
}