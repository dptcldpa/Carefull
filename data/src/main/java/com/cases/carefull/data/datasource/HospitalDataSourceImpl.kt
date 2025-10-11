package com.cases.carefull.data.datasource

import com.cases.carefull.data.di.HospitalApiKey
import com.cases.carefull.data.dto.HospitalItemDto
import com.cases.carefull.data.network.HospitalApiService
import javax.inject.Inject

class HospitalDataSourceImpl @Inject constructor(
    private val apiService: HospitalApiService,
    @HospitalApiKey private val hospitalApiKey: String
): HospitalDataSource {
    override suspend fun getHospitals(query: String, latitude: Double, longitude: Double): List<HospitalItemDto> {
        val departmentCode = convertQueryToDeptCode(query)

        return runCatching {
            val response = apiService.searchHospitals(
                serviceKey = hospitalApiKey,
                xPos = longitude,
                yPos = latitude ,
                dgsbjtCd = departmentCode
            )

            if (response.header.resultCode == "00") {
                response.body.items.hospitals ?: emptyList()
            } else {
                throw Exception("API Error: ${response.header.resultMsg}")
            }
        }.getOrThrow()
    }

    private fun convertQueryToDeptCode(query: String): String {
        return "01"
//        return when (query.trim()) {
//            "내과" -> "01"
//            "소아청소년과", "소아과" -> "04"
//            "정형외과" -> "05"
//            "신경외과" -> "06"
//            "성형외과" -> "08"
//            "외과" -> "09"
//            "산부인과" -> "10"
//            "안과" -> "11"
//            "이비인후과" -> "12"
//            "피부과" -> "13"
//            "비뇨기과", "비뇨의학과" -> "14"
//            "정신건강의학과", "정신과" -> "23"
//            else -> "00" // 그 외에는 '전체'
//        }
    }
}