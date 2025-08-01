package com.cases.carefull.domain.repository

import com.cases.carefull.domain.model.MedicineItem

interface MedicineRepository {
    suspend fun searchMedicines(medicineApiKey: String, query: String): Result<List<MedicineItem>> // 파라미터로 api 키 받기
}