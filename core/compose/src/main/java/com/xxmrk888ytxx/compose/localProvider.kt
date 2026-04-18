package com.xxmrk888ytxx.compose

import androidx.compose.runtime.compositionLocalOf
import com.xxmrk888ytxx.android.Navigator
import com.xxmrk888ytxx.android.ToastManager

val LocalNavigator = compositionLocalOf<Navigator> { error("LocalNavigator not provided") }
val LocalToastManager = compositionLocalOf<ToastManager> { error("LocalToastManager not provided") }