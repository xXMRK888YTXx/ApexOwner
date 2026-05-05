package com.xxmrk888ytxx.core.base.compose

import androidx.compose.runtime.compositionLocalOf
import com.xxmrk888ytxx.core.base.android.Navigator
import com.xxmrk888ytxx.core.base.android.ToastManager

val LocalNavigator = compositionLocalOf<Navigator> { error("LocalNavigator not provided") }
val LocalToastManager = compositionLocalOf<ToastManager> { error("LocalToastManager not provided") }