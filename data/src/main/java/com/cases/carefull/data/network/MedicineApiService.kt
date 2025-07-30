package com.cases.carefull.data.network

import retrofit2.http.GET
import retrofit2.http.Query

interface MedicineApiService {

    @GET("getDrbEasyDrugList")
    suspend fun getMedicineList(
        @Query("serviceKey") serviceKey: String,
        @Query("itemName") itemName: String,
        @Query("pageNo") pageNo: Int = 1,
        @Query("numOfRows") numOfRows: Int = 20,
        @Query("type") type: String = "json"
    ): MedicineResponse
}