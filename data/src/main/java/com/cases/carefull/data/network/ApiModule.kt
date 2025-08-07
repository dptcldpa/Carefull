package com.cases.carefull.data.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiModule {

	private val okHttpClient: OkHttpClient by lazy {
		val loggingInterceptor = HttpLoggingInterceptor().apply {
			level = HttpLoggingInterceptor.Level.BODY
		}
		OkHttpClient.Builder()
			.addInterceptor(loggingInterceptor)
			.build()
	}

	fun <T> createApiService(baseUrl: String, service: Class<T>): T {
		return Retrofit.Builder()
			.baseUrl(baseUrl)
			.client(okHttpClient)
			.addConverterFactory(GsonConverterFactory.create())
			.build()
			.create(service)
	}

	private const val MEDICINE_BASE_URL = "http://apis.data.go.kr/1471000/DrbEasyDrugInfoService/"
	private const val DIET_BASE_URL = "https://apis.data.go.kr/1471000/FoodNtrCpntDbInfo02/"

	val medicineApi: MedicineApiService by lazy {
		createApiService(MEDICINE_BASE_URL, MedicineApiService::class.java)
	}

	val dietApi: DietApiService by lazy {
		createApiService(DIET_BASE_URL, DietApiService::class.java)
	}
}