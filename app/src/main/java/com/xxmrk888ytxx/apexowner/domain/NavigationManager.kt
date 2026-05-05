package com.xxmrk888ytxx.apexowner.domain

import com.xxmrk888ytxx.apexowner.core.navigation.Screen
import com.xxmrk888ytxx.core.base.android.Navigator
import kotlinx.coroutines.flow.StateFlow

interface NavigationManager : Navigator {
    val backStack: StateFlow<List<Screen>>
    fun navigate(screen: Screen)
    fun replaceCurrentScreenAndNavigate(screen: Screen)
    suspend fun setStartDestination(screen: Screen)
}