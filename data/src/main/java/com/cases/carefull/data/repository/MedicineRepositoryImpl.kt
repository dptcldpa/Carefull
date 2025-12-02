package com.cases.carefull.data.repository

import android.util.Log
import com.cases.carefull.data.di.MedicineApiKey
import com.cases.carefull.data.mapper.toDomain
import com.cases.carefull.data.dto.MedicineItemDto
import com.cases.carefull.data.network.MedicineApiService
import com.cases.carefull.domain.model.MedicineItem
import com.cases.carefull.domain.repository.MedicineRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class MedicineRepositoryImpl @Inject constructor(
    private val apiService: MedicineApiService,
    @MedicineApiKey private val medicineApiKey: String
) : MedicineRepository {
    override suspend fun searchMedicines(query: String): List<MedicineItem> = withContext(
        Dispatchers.IO) {
        val response = apiService.getMedicineList(
            serviceKey = medicineApiKey,
            itemName = query
        )

        if (response.header.resultCode != "00") {
            throw IllegalStateException("API Error: ${response.header.resultCode}")
        }

        response.body.items.map { it.toDomain() }
    }
}