package com.cases.carefull.data.datasource

import com.cases.carefull.data.di.HospitalApiKey
import com.cases.carefull.data.dto.diagnosis.HospitalExcellItem
import com.cases.carefull.data.dto.diagnosis.HospitalItemDto
import com.cases.carefull.data.network.HospitalApiService
import javax.inject.Inject

class HospitalDataSourceImpl @Inject constructor(
    private val hospitalApiService: HospitalApiService,
    @HospitalApiKey private val hospitalApiKey: String
): HospitalDataSource {
    // 병원 목록
    override suspend fun getHospitalList(departmentCode: String, latitude: Double, longitude: Double): List<HospitalItemDto> {
        val response = hospitalApiService.getHospitalList(
            serviceKey = hospitalApiKey,
            xPos = longitude.toString(),
            yPos = latitude.toString(),
            dgsbjtCd = departmentCode,
            radius = "1300"
        )

        if (response.header?.resultCode != "00") {
            throw Exception("API Error (getHospitals): ${response.header?.resultMsg}")
        }

        return response.body?.items?.hospitals ?: emptyList()
    }

    // 우수 병원
    override suspend fun getHospitalExcell(ykiho: String): List<HospitalExcellItem> {
        if (ykiho.isBlank()) {
            return emptyList()
        }

        val response = hospitalApiService.getHospitalsExcell(
            serviceKey = hospitalApiKey,
            ykiho = ykiho
        )

        // 평가 정보 API는 정보가 없는 경우에도 정상 응답(00)을 줄 수 있음
        if (response.header?.resultCode != "00") {
            return emptyList()
        }

        return response.body?.items?.item ?: emptyList()
    }
}