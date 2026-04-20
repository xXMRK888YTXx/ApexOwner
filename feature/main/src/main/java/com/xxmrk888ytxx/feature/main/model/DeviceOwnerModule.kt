package com.xxmrk888ytxx.feature.main.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class DeviceOwnerModule(
    val id: Byte,
    @param:StringRes val title: Int,
    @param:StringRes val description: Int,
    @param:DrawableRes val iconResId: Int,
    val onClick: () -> Unit
)