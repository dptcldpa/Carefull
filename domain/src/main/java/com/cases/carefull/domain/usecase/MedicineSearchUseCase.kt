package com.cases.carefull.domain.usecase

import com.cases.carefull.domain.repository.MedicineRepository


class MedicineSearchUseCase(private val repository: MedicineRepository) {
    suspend operator fun invoke(medicineApiKey: String, query: String) = repository.searchMedicines(medicineApiKey, query)
}
