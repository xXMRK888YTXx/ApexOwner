package com.xxmrk888ytxx.apexowner.core.navigation

import androidx.navigation3.runtime.NavKey
import com.xxmrk888ytxx.apexowner.view.mainActivity.model.ApexOwnerBottomBarItem
import kotlinx.serialization.Serializable

@Serializable
sealed interface Screen : NavKey {
    @Serializable
    object Stub : Screen
    @Serializable
    object OnboardingScreen : Screen
    @Serializable
    data object MainScreen : Screen, BottomBarScreen {
        override val itemId: Int
            get() = ApexOwnerBottomBarItem.MainScreen.id
    }

    @Serializable
    data object DeviceRestrictionModuleScreen : Screen

    @Serializable
    data object WorkProfileModuleScreen : Screen

    @Serializable
    data object GlobalPermissionModuleScreen : Screen

    @Serializable
    data class AppPermissionsModuleScreen(
        val packageName: String,
        val appName: String
    ) : Screen
}
