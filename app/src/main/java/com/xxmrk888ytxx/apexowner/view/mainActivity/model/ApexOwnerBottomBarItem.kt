package com.xxmrk888ytxx.apexowner.view.mainActivity.model

import androidx.annotation.DrawableRes
import com.xxmrk888ytxx.apexowner.R
import com.xxmrk888ytxx.core.base.android.uiText.UiText
import com.xxmrk888ytxx.core.base.android.uiText.uiText

sealed class ApexOwnerBottomBarItem(
    val id: Int,
    val text: UiText,
    @param:DrawableRes val icon: Int
) {
    data object MainScreen: ApexOwnerBottomBarItem(0, R.string.main_screen.uiText(), R.drawable.home)
    data object Settings: ApexOwnerBottomBarItem(1, R.string.settings.uiText(), R.drawable.settings)

    companion object {
        val itemList = listOf(MainScreen, Settings)
    }
}
