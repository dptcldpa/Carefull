package com.cases.carefull.domain.repository

import com.cases.carefull.domain.model.MedicineItem

interface MedicineRepository {
    suspend fun searchMedicines(query: String): Result<List<MedicineItem>>
}