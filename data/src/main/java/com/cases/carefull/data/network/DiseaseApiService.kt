package com.cases.carefull.data.network

import com.cases.carefull.data.dto.diagnosis.DiseaseResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

interface DiseaseApiService {

    @GET("healthInfo")
    suspend fun getDiseaseList(
        @Query("TOKEN", encoded = true) token: String
    ): DiseaseResponseDto

    @GET("healthInfo")
    suspend fun getDiseaseDetail(
        @Query("TOKEN", encoded = true) token: String,
        @Query("cntntsSn") contentSn: String
    ): DiseaseResponseDto
}