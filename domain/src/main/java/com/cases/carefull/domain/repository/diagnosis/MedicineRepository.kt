package com.cases.carefull.domain.repository.diagnosis

import com.cases.carefull.domain.model.MedicineItem

interface MedicineRepository {
    suspend fun searchMedicines(
            query: String
    ):List<MedicineItem>
}