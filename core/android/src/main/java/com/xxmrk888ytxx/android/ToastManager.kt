package com.xxmrk888ytxx.android

import android.content.Context
import android.widget.Toast
import androidx.annotation.StringRes
import com.xxmrk888ytxx.android.extension.runOnUiThread

open class ToastManager(private val context: Context) {
    open fun showToast(text: String) = runOnUiThread {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
    }

    open fun showToast(@StringRes resId: Int) {
        showToast(context.getString(resId))
    }
}