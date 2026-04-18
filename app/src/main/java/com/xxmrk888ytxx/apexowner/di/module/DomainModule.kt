package com.xxmrk888ytxx.apexowner.di.module

import com.xxmrk888ytxx.apexowner.data.Navigation3NavigationManager
import com.xxmrk888ytxx.apexowner.data.SettingsRepositoryImpl
import com.xxmrk888ytxx.apexowner.domain.NavigationManager
import com.xxmrk888ytxx.apexowner.domain.SettingsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface DomainModule {
    @Binds
    @Singleton
    fun bindNavigationManager(navigationManager: Navigation3NavigationManager): NavigationManager

    @Binds
    fun bindSettingsRepository(settingsRepositoryImpl: SettingsRepositoryImpl): SettingsRepository
}