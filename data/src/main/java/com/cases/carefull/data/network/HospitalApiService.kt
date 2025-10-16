package com.cases.carefull.data.network

import com.cases.carefull.data.dto.HospitalResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

interface HospitalApiService {
    @GET("getHospBasisList") // ?추가
    suspend fun searchHospitals(
        @Query("serviceKey") serviceKey: String,
        @Query("pageNo") pageNo: Int = 1,
        @Query("numOfRows") numOfRows: Int = 100,
//        @Query("sidoCd") sidoCd: Int = 110000,
//        @Query("sgguCd") sgguCd: Int = 110019,
//        @Query("emdongNm") emdongNm: String? = null,
//        @Query("yadmNm") yadmNm: String? = null,
//        @Query("zipCd") zipCd: Int = 2010,
//        @Query("clCd") clCd: Int = 11,
        @Query("dgsbjtCd") dgsbjtCd: String = "01",
        @Query("xPos") xPos: Double = 127.09854004628151,
        @Query("yPos") yPos: Double = 37.6132113197367,
        @Query("radius") radius: Int = 2000
    ): HospitalResponseDto
}