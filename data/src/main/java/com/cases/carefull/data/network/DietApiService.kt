package com.cases.carefull.data.network

import com.cases.carefull.data.dto.diet.DietResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

interface DietApiService {
    @GET("getFoodNtrCpntDbInq02")
    suspend fun getFoodList(
        @Query("serviceKey") apiKey: String,
        @Query("pageNo") pageNo: Int = 1,
        @Query("numOfRows") numOfRows: Int = 20,
        @Query("type") type: String = "json",
        @Query("FOOD_NM_KR") query: String
    ): DietResponseDto
}
