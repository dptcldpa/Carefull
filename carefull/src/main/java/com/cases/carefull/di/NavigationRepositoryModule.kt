package com.cases.carefull.di

import com.cases.carefull.features.carefullcommon.components.NavigationRepositoryImpl
import com.cases.carefull.features.carefullcommon.model.NavigationRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class NavigationRepositoryModule {
	
	@Binds
	@Singleton
	abstract fun bindNavigationRepository(
		navigationRepositoryImpl: NavigationRepositoryImpl
	): NavigationRepository
}

