package com.cases.carefull.domain.usecase.diagnosis

import com.cases.carefull.domain.model.MedicineItem
import com.cases.carefull.domain.repository.diagnosis.MedicineRepository
import com.cases.carefull.domain.util.DataResourceResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class MedicineSearchUseCase(
    private val repository: MedicineRepository,
) {
    operator fun invoke(query: String): Flow<DataResourceResult<List<MedicineItem>>> = flow {
        emit(DataResourceResult.Loading)
        val items = repository.searchMedicines(query)
        emit(DataResourceResult.Success(items))
    }
        .catch { emit(DataResourceResult.Error(it)) }
}