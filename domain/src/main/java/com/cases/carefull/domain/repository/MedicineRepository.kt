package com.cases.carefull.domain.repository

import com.cases.carefull.domain.model.MedicineItem

interface MedicineRepository {
    suspend fun searchMedicines(medicineApiKey: String, query: String):List<MedicineItem>
}