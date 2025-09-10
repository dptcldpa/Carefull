package com.cases.carefull.domain.usecase

import com.cases.carefull.domain.model.MedicineItem
import com.cases.carefull.domain.repository.MedicineRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class MedicineSearchUseCase(
    private val repository: MedicineRepository,
) {
    operator fun invoke(query: String): Flow<List<MedicineItem>> = flow {
        val items = repository.searchMedicines(
            query = query
        )
        emit(items)
    }
}