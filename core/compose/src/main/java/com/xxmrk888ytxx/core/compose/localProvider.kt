package com.xxmrk888ytxx.core.compose

import androidx.compose.runtime.compositionLocalOf
import com.xxmrk888ytxx.core.android.Navigator
import com.xxmrk888ytxx.core.android.ToastManager

val LocalNavigator = compositionLocalOf<Navigator> { error("LocalNavigator not provided") }
val LocalToastManager = compositionLocalOf<ToastManager> { error("LocalToastManager not provided") }