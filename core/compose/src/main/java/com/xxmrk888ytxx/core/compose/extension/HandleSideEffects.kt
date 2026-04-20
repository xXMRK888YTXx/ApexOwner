package com.xxmrk888ytxx.core.compose.extension

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import com.xxmrk888ytxx.core.android.mvi.DefaultSideEffect
import com.xxmrk888ytxx.core.android.mvi.SideEffect
import com.xxmrk888ytxx.core.android.uiText.asString
import kotlinx.coroutines.flow.Flow
import com.xxmrk888ytxx.core.compose.LocalToastManager
import com.xxmrk888ytxx.core.compose.LocalNavigator

@Composable
fun HandleSideEffects(
    sideEffects: Flow<SideEffect>,
) {
    val toastManager = LocalToastManager.current
    val context = LocalContext.current
    val navigator = LocalNavigator.current

    LaunchedEffect(sideEffects) {
        sideEffects.collect {
            when(it) {
                is DefaultSideEffect.ShowToast -> toastManager.showToast(it.message.asString(context))
                is DefaultSideEffect.NavigationBack -> navigator.navigateUp()
                is DefaultSideEffect.NavigationAction -> it.action(navigator)
                else -> {}
            }
        }
    }
}

@Composable
inline fun <reified EFFECT : SideEffect> HandleSideEffects(
    sideEffects: Flow<SideEffect>,
    crossinline onEffect: suspend (EFFECT) -> Unit
) {
    val toastManager = LocalToastManager.current
    val context = LocalContext.current
    val navigator = LocalNavigator.current

    LaunchedEffect(sideEffects) {
        sideEffects.collect {
            when(it) {
                is DefaultSideEffect.ShowToast -> toastManager.showToast(it.message.asString(context))
                is DefaultSideEffect.NavigationBack -> navigator.navigateUp()
                is DefaultSideEffect.NavigationAction -> it.action(navigator)
                is EFFECT -> onEffect(it)
                else -> {}
            }
        }
    }
}