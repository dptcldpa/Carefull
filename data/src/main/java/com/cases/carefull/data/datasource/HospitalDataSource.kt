package com.cases.carefull.data.datasource

import com.cases.carefull.data.dto.HospitalItemDto

interface HospitalDataSource {
    suspend fun getHospitals(query: String, latitude: Double, longitude: Double): List<HospitalItemDto>
}