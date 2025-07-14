//package com.cases.carefull.data.network
//
//import okhttp3.ResponseBody
//import retrofit2.Response
//import retrofit2.http.GET
//import retrofit2.http.Query
//
//interface MedicineApiService {
//    // 기존 함수
//    @GET("getDrbEasyDrugList")
//    suspend fun getDrugInfo(
//        @Query("serviceKey") serviceKey: String,
//        @Query("itemName") itemName: String,
//        @Query("pageNo") pageNo: Int = 1,
//        @Query("numOfRows") numOfRows: Int = 10,
//        @Query("_type") type: String = "json"
//    ): Response<MedicineResponse>
//
//    // 원시 응답 확인용 함수 추가
//    @GET("getDrbEasyDrugList")
//    suspend fun getRawDrugInfo(  // 여기를 추가
//        @Query("serviceKey") serviceKey: String,
//        @Query("itemName") itemName: String,
//        @Query("pageNo") pageNo: Int = 1,
//        @Query("numOfRows") numOfRows: Int = 10,
//        @Query("_type") type: String = "json"
//    ): Response<ResponseBody>  // ResponseBody로 원문 받기
//}