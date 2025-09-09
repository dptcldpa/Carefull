package com.cases.carefull.di

import com.cases.carefull.BuildConfig
import com.cases.carefull.data.di.DietApiKey
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApiKeyModule {
	
	@Provides
	@Singleton
	@DietApiKey
	fun provideDietApiKey(): String {
		return BuildConfig.diet_api_key
	}
}