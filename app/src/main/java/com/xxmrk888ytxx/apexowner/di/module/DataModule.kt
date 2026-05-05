package com.xxmrk888ytxx.apexowner.di.module

import android.content.Context
import com.xxmrk888ytxx.common.preferencesstorage.PreferencesStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface DataModule {
    companion object {
        @Provides
        @Singleton
        fun providePreferencesStorage(@ApplicationContext context: Context) : PreferencesStorage {
            return PreferencesStorage.Factory().create("preferences",context)
        }
    }
}