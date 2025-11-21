package com.cases.carefull.data.di

import androidx.room.PrimaryKey
import com.cases.carefull.data.network.ChatbotApiService
import com.cases.carefull.data.network.DietApiService
import com.cases.carefull.data.network.HospitalApiService
import com.cases.carefull.data.network.MedicineApiService
import com.google.gson.Gson
import com.tickaroo.tikxml.TikXml
import com.tickaroo.tikxml.retrofit.TikXmlConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
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
	private const val HOSPITAL_URL = "https://apis.data.go.kr/B551182/"
	private const val Chatbot_URL = "https://api.openai.com/"

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

	@Provides
	@Singleton
	fun provideGson(): Gson {
		return Gson()
	}

	@Provides
	@Singleton
	@ChatbotInterceptor
	fun provideChatbotInterceptor(@ChatbotApiKey chatbotApiKey: String): Interceptor {
		return Interceptor { chain ->
			val request = chain.request().newBuilder()
				.addHeader("Authorization", "Bearer $chatbotApiKey")
				.build()
			chain.proceed(request)
		}
	}

	@Provides
	@Singleton
	@ChatbotOkHttpClient
	fun provideChatbotOkHttpClient(@ChatbotInterceptor interceptor: Interceptor): OkHttpClient {
		val loggingInterceptor = HttpLoggingInterceptor().apply {
			level = HttpLoggingInterceptor.Level.BODY
		}
		return OkHttpClient.Builder()
			.addInterceptor(interceptor)
			.addInterceptor(loggingInterceptor)
			.build()
	}

	@Provides
	@Singleton
	@ChatbotRetrofit
	fun provideChatbotRetrofit(@ChatbotOkHttpClient okHttpClient: OkHttpClient): Retrofit {
		return Retrofit.Builder()
			.baseUrl(Chatbot_URL)
			.client(okHttpClient)
			.addConverterFactory(GsonConverterFactory.create())
			.build()
	}

	@Provides
	@Singleton
	fun provideChatbotApiService(@ChatbotRetrofit retrofit: Retrofit): ChatbotApiService {
		return retrofit.create(ChatbotApiService::class.java)
	}
}