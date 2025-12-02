package com.cases.carefull.data.datasource

import com.cases.carefull.data.dto.diagnosis.HospitalExcellItem
import com.cases.carefull.data.dto.diagnosis.HospitalItemDto

interface HospitalDataSource {
    // 병원 목록
    suspend fun getHospitalList(departmentCode: String, latitude: Double, longitude: Double): List<HospitalItemDto>

    // 우수 병원
    suspend fun getHospitalExcell(ykiho: String): List<HospitalExcellItem>
}