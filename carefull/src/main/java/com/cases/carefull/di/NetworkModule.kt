package com.cases.carefull.di

import com.cases.carefull.data.network.DietApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
	
	private const val BASE_URL = "https://apis.data.go.kr/1471000/FoodNtrCpntDbInfo02/"
	
	private val loggingInterceptor = HttpLoggingInterceptor().apply {
		level = HttpLoggingInterceptor.Level.BODY
	}
	
	private val okHttpClient = OkHttpClient.Builder()
		.addInterceptor(loggingInterceptor)
		.build()
	
	@Provides
	@Singleton
	fun provideRetrofit(): Retrofit {
		return Retrofit.Builder()
			.baseUrl(BASE_URL)
			.client(okHttpClient)
			.addConverterFactory(GsonConverterFactory.create())
			.build()
	}
	
	@Provides
	@Singleton
	fun provideDietApiService(retrofit: Retrofit): DietApiService {
		return retrofit.create(DietApiService::class.java)
	}
}