package com.xxmrk888ytxx.core.compose

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.xxmrk888ytxx.core.android.uiText.UiText
import com.xxmrk888ytxx.core.android.uiText.asString

@Composable
fun UiText.asString(): String {
    val context = LocalContext.current
    return asString(context)
}