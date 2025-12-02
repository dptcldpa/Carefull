package com.cases.carefull.domain.repository.diagnosis

import com.cases.carefull.domain.model.Disease
import com.cases.carefull.domain.util.DataResourceResult
import kotlinx.coroutines.flow.Flow

interface DiseaseRepository {
    fun getDiseaseList(): Flow<DataResourceResult<List<Disease>>>
    fun getDiseaseDetail(contentSn: String): Flow<DataResourceResult<Disease>>
}