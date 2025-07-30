package com.cases.carefull.domain.usecase

import com.cases.carefull.domain.repository.MedicineRepository


class MedicineSearchUseCase(private val repository: MedicineRepository) {
    suspend operator fun invoke(query: String) = repository.searchMedicines(query)
}
