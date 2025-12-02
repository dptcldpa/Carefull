package com.cases.carefull.domain.repository.diagnosis

import com.cases.carefull.domain.model.CareFullLocation

interface CareFullLocationRepository {
    suspend fun getLastKnownLocation(): CareFullLocation?

    suspend fun getCurrentLocation(): CareFullLocation?
}