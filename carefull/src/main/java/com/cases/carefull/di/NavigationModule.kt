package com.cases.carefull.di

import com.cases.carefull.features.carefullcommon.components.AppNavigationProviderImpl
import com.cases.carefull.features.carefullcommon.model.AppNavigationProvider
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class NavigationModule {
	
	@Binds
	@Singleton
	abstract fun bindAppNavigationProvider(
		appNavigationProviderImpl: AppNavigationProviderImpl
	): AppNavigationProvider
}

