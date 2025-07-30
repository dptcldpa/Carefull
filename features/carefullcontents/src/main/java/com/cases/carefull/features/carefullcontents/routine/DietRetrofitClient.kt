package com.cases.carefull.features.carefullcontents.routine

import com.cases.carefull.data.network.DietApiService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object DietRetrofitClient {
	private const val BASE_URL = "https://apis.data.go.kr/1471000/FoodNtrCpntDbInfo02/"
	
	// 통신 로그를 확인하기 위한 Interceptor
	private val loggingInterceptor = HttpLoggingInterceptor().apply {
		level = HttpLoggingInterceptor.Level.BODY
	}
	
	// OkHttpClient에 Interceptor 추가
	private val okHttpClient = OkHttpClient.Builder()
		.addInterceptor(loggingInterceptor)
		.build()
	
	val instance: Retrofit by lazy {
		Retrofit.Builder()
			.baseUrl(BASE_URL)
			.client(okHttpClient)
			.addConverterFactory(GsonConverterFactory.create())
			.build()
	}
	val api: DietApiService by lazy {
		instance.create(DietApiService::class.java)
	}
}