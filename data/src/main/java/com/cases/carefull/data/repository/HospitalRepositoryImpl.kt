package com.cases.carefull.data.repository

import com.cases.carefull.data.datasource.HospitalDataSource
import com.cases.carefull.data.mapper.toDomainList
import com.cases.carefull.domain.model.HospitalItem
import com.cases.carefull.domain.repository.HospitalRepository
import com.cases.carefull.domain.util.DataResourceResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class HospitalRepositoryImpl @Inject constructor(
    private val hospitalDataSource: HospitalDataSource
) : HospitalRepository {
    override suspend fun getHospitals(
        query: String,
        latitude: Double,
        longitude: Double
    ): Flow<DataResourceResult<List<HospitalItem>>> = flow {
        emit(DataResourceResult.Loading)

        val result = runCatching {
            hospitalDataSource.getHospitals(query, latitude, longitude).toDomainList()
        }

        result.fold(
            onSuccess = { hospitalList -> emit(DataResourceResult.Success(hospitalList)) },
            onFailure = { exception -> emit(DataResourceResult.Error(exception)) }
        )
    }
}