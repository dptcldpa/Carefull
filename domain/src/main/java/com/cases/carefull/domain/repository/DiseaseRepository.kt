package com.cases.carefull.domain.repository

import com.cases.carefull.domain.model.Disease
import com.cases.carefull.domain.util.DataResourceResult
import kotlinx.coroutines.flow.Flow

interface DiseaseRepository {
    suspend fun getDiseaseList(): Flow<DataResourceResult<List<Disease>>>

    suspend fun getDiseaseDetail(contentSn: String): Flow<DataResourceResult<Disease>>
}