package com.cases.carefull.di

import com.cases.carefull.BuildConfig
import com.cases.carefull.data.di.ChatbotApiKey
import com.cases.carefull.data.di.DietApiKey
import com.cases.carefull.data.di.DiseaseApiKey
import com.cases.carefull.data.di.HospitalApiKey
import com.cases.carefull.data.di.MedicineApiKey
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
	
	@Provides
	@Singleton
	@MedicineApiKey
	fun provideMedicineApiKey(): String {
		return BuildConfig.medicine_api_key
	}

	@Provides
	@Singleton
	@DiseaseApiKey
	fun provideDiseaseApiKey(): String {
		return BuildConfig.disease_api_key
	}

	@Provides
	@Singleton
	@HospitalApiKey
	fun provideHospitalApiKey(): String {
		return BuildConfig.hospital_api_key
	}

	@Provides
	@Singleton
	@ChatbotApiKey
	fun provideChatbotApiKey(): String {
		return BuildConfig.OPENAI_API_KEY
	}
}