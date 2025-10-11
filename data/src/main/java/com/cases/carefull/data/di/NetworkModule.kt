package com.cases.carefull.data.di

import com.cases.carefull.data.network.DietApiService
import com.cases.carefull.data.network.HospitalApiService
import com.cases.carefull.data.network.MedicineApiService
import com.tickaroo.tikxml.TikXml
import com.tickaroo.tikxml.retrofit.TikXmlConverterFactory
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
	
	private const val DIET_URL = "https://apis.data.go.kr/1471000/FoodNtrCpntDbInfo02/"
	private const val MEDICINE_URL = "http://apis.data.go.kr/1471000/DrbEasyDrugInfoService/"
	private const val HOSPITAL_URL = "https://apis.data.go.kr/B551182/hospInfoServicev2/"

	@Provides
	@Singleton
	fun provideOkHttpClient(): OkHttpClient {
		val loggingInterceptor = HttpLoggingInterceptor().apply {
			level = HttpLoggingInterceptor.Level.BODY
		}
		return OkHttpClient.Builder()
			.addInterceptor(loggingInterceptor)
			.build()
	}
	@Provides
	@Singleton
	@DietRetrofit
	fun provideDietRetrofit(okHttpClient: OkHttpClient): Retrofit {
		return Retrofit.Builder()
			.baseUrl(DIET_URL)
			.client(okHttpClient)
			.addConverterFactory(GsonConverterFactory.create())
			.build()
	}
	
	@Provides
	@Singleton
	fun provideDietApiService(@DietRetrofit retrofit: Retrofit): DietApiService {
		return retrofit.create(DietApiService::class.java)
	}
	
	@Provides
	@Singleton
	@MedicineRetrofit
	fun provideMedicineRetrofit(okHttpClient: OkHttpClient): Retrofit {
		return Retrofit.Builder()
			.baseUrl(MEDICINE_URL)
			.client(okHttpClient)
			.addConverterFactory(GsonConverterFactory.create())
			.build()
	}
	
	@Provides
	@Singleton
	fun provideMedicineApiService(@MedicineRetrofit retrofit: Retrofit): MedicineApiService {
		return retrofit.create(MedicineApiService::class.java)
	}

	@Provides
	@Singleton
	@HospitalRetrofit
	fun provideHospitalRetrofit(okHttpClient: OkHttpClient): Retrofit {
		return Retrofit.Builder()
			.baseUrl(HOSPITAL_URL)
			.client(okHttpClient)
			.addConverterFactory(TikXmlConverterFactory.create(TikXml.Builder().exceptionOnUnreadXml(false).build()))
			.build()
	}

	@Provides
	@Singleton
	fun provideHospitalApiService(@HospitalRetrofit retrofit: Retrofit): HospitalApiService {
		return retrofit.create(HospitalApiService::class.java)
	}
}