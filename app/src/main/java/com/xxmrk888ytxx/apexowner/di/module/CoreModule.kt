package com.xxmrk888ytxx.apexowner.di.module

import android.content.Context
import com.xxmrk888ytxx.core.base.android.ToastManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class CoreModule {
    @Provides
    @Singleton
    fun provideToastManager(@ApplicationContext context: Context) : ToastManager = ToastManager(context)
}