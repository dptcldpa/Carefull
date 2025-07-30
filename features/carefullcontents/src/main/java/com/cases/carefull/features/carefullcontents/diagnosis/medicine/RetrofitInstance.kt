package com.cases.carefull.features.carefullcontents.diagnosis.medicine

import com.cases.carefull.data.network.MedicineApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    private const val BASE_URL = "http://apis.data.go.kr/1471000/DrbEasyDrugInfoService/"

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val medicineApi: MedicineApiService by lazy {
        retrofit.create(MedicineApiService::class.java)
    }
}