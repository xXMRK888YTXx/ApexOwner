package com.xxmrk888ytxx.core.devicepolicy.di

import android.app.admin.DevicePolicyManager
import android.content.Context
import androidx.core.content.getSystemService
import com.xxmrk888ytxx.core.devicepolicy.AndroidDeviceOwnerManager
import com.xxmrk888ytxx.core.devicepolicy.DeviceOwnerManager
import com.xxmrk888ytxx.core.devicepolicy.DeviceRestrictionManager
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal interface DevicePolicyModule {

    @Binds
    @Singleton
    fun bindsDeviceOwnerManager(androidDeviceOwnerManager: AndroidDeviceOwnerManager): DeviceOwnerManager

    @Binds
    fun bindsRestrictionManager(deviceOwnerManager: DeviceOwnerManager): DeviceRestrictionManager

    companion object {
        @Provides
        fun provideDevicePolicyManager(@ApplicationContext context: Context): DevicePolicyManager = context.getSystemService()!!
    }
}