package com.xxmrk888ytxx.core.base.compose

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.xxmrk888ytxx.core.base.android.uiText.UiText
import com.xxmrk888ytxx.core.base.android.uiText.asString

@Composable
fun UiText.asString(): String {
    val context = LocalContext.current
    return asString(context)
}