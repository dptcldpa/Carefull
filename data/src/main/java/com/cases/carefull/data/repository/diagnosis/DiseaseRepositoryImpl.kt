package com.cases.carefull.data.repository.diagnosis

import com.cases.carefull.data.datasource.DiseaseDataSource
import com.cases.carefull.data.mapper.toDomain
import com.cases.carefull.domain.model.Disease
import com.cases.carefull.domain.repository.diagnosis.DiseaseRepository
import com.cases.carefull.domain.util.DataResourceResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class DiseaseRepositoryImpl @Inject constructor(
    private val diseaseDataSource: DiseaseDataSource
) : DiseaseRepository {

    override fun getDiseaseList(): Flow<DataResourceResult<List<Disease>>> = flow {
        emit(DataResourceResult.Loading)

        val diseaseList = diseaseDataSource.getDiseaseList().toDomain()
        emit(DataResourceResult.Success(listOf(diseaseList)))
    }
    .catch { emit(DataResourceResult.Error(it)) }
    .flowOn(Dispatchers.IO)

    override fun getDiseaseDetail(contentSn: String): Flow<DataResourceResult<Disease>> = flow {
        emit(DataResourceResult.Loading)

        val disease = diseaseDataSource.getDiseaseDetail(contentSn).toDomain()
        emit(DataResourceResult.Success(disease))
    }
    .catch { emit(DataResourceResult.Error(it)) }
    .flowOn(Dispatchers.IO)
}