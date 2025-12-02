package com.cases.carefull.data.network

import com.cases.carefull.data.dto.diagnosis.HospitalExcellResponseDto
import com.cases.carefull.data.dto.diagnosis.HospitalListResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

interface HospitalApiService {
    // 병원 목록
    @GET("hospInfoServicev2/getHospBasisList")
    suspend fun getHospitalList(
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
        @Query("xPos") xPos: String,
        @Query("yPos") yPos: String,
        @Query("radius") radius: String
    ): HospitalListResponseDto

    // 우수 병원
    @GET("exclInstHospAsmInfoService1/getExclInstHospAsmInfo1")
    suspend fun getHospitalsExcell(
        @Query("serviceKey") serviceKey: String,
        @Query("ykiho") ykiho: String,
//        @Query("_type") type: String = "xml"
    ): HospitalExcellResponseDto
}