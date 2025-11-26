package com.cases.carefull.data.repository

import com.cases.carefull.data.datasource.DiseaseDataSource
import com.cases.carefull.data.mapper.toDomain
import com.cases.carefull.domain.model.Disease
import com.cases.carefull.domain.repository.DiseaseRepository
import com.cases.carefull.domain.util.DataResourceResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class DiseaseRepositoryImpl @Inject constructor(
    private val diseaseDataSource: DiseaseDataSource
) : DiseaseRepository {

    override suspend fun getDiseaseList(): Flow<DataResourceResult<List<Disease>>> = flow {
        emit(DataResourceResult.Loading)

        val result = runCatching {
            diseaseDataSource.getDiseaseList().toDomain()
        }

        result.fold(
            onSuccess = { emit(DataResourceResult.Success(listOf(it))) },
            onFailure = { emit(DataResourceResult.Error(it)) }
        )
    }

    override suspend fun getDiseaseDetail(contentSn: String): Flow<DataResourceResult<Disease>> = flow {
        emit(DataResourceResult.Loading)

        val result = runCatching {
            diseaseDataSource.getDiseaseDetail(contentSn).toDomain()
        }

        result.fold(
            onSuccess = { emit(DataResourceResult.Success(it)) },
            onFailure = { emit(DataResourceResult.Error(it)) }
        )
    }
}