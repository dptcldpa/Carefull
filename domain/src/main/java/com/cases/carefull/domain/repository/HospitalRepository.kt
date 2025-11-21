package com.cases.carefull.domain.repository

import com.cases.carefull.domain.model.Hospital
import com.cases.carefull.domain.util.DataResourceResult
import kotlinx.coroutines.flow.Flow

interface HospitalRepository {
    fun getHospitals(query: String, latitude: Double, longitude: Double): Flow<DataResourceResult<List<Hospital>>>
}