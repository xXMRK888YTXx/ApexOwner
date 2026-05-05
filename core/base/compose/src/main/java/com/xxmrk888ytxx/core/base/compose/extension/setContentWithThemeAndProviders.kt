package com.xxmrk888ytxx.core.base.compose.extension

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import com.xxmrk888ytxx.core.base.android.Navigator
import com.xxmrk888ytxx.core.base.android.ToastManager
import com.xxmrk888ytxx.core.base.compose.LocalNavigator
import com.xxmrk888ytxx.core.base.compose.LocalToastManager
import com.xxmrk888ytxx.core.base.compose.theme.AppSeedColors
import com.xxmrk888ytxx.core.base.compose.theme.AppTheme
import kotlinx.coroutines.flow.StateFlow

fun ComponentActivity.setContentWithThemeAndProviders(
    useDarkTheme: Boolean? = null,
    navigator: Navigator,
    toastManager: ToastManager,
    themeColor: StateFlow<Color?>,
    content: @Composable () -> Unit,
) {
    setContent {
        val themeColor by themeColor.collectAsState()
        val randomColor = remember(themeColor) {
            AppSeedColors.allColors.random()
        }

        CompositionLocalProvider(
            LocalNavigator provides navigator,
            LocalToastManager provides toastManager,
        ) {
            AppTheme(
                darkTheme = useDarkTheme ?: isSystemInDarkTheme(),
                seedColor = if (AppSeedColors.RandomColor == themeColor) randomColor else themeColor,
                content = content
            )
        }
    }
}