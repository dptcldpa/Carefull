package com.cases.carefull.domain.repository

import com.cases.carefull.domain.model.Location

interface LocationRepository {
    suspend fun getLastKnownLocation(): Location?
}