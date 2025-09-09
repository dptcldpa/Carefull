package com.cases.carefull.data.repository

import android.util.Log
import com.cases.carefull.data.di.MedicineApiKey
import com.cases.carefull.data.mapper.toDomain
import com.cases.carefull.data.dto.MedicineItemDto
import com.cases.carefull.data.network.MedicineApiService
import com.cases.carefull.domain.repository.MedicineRepository
import javax.inject.Inject

class MedicineRepositoryImpl @Inject constructor(
    private val apiService: MedicineApiService,
    @MedicineApiKey private val medicineApiKey: String
) : MedicineRepository {
    override suspend fun searchMedicines(
        query: String
    ) = runCatching {
        Log.d("API_TEST", "Repository: API 호출 시도, query = $query")
        val response = apiService.getMedicineList(
            serviceKey = medicineApiKey,
            itemName = query
        )
        Log.d("API_TEST", "Repository: API 호출 시도, ${response.header.resultCode}")
        val result = if (response.header.resultCode == "00") {
            val dtoList: List<MedicineItemDto> = response.body.items
            Log.d("API_TEST", "Repository: API 호출 성공, ${dtoList.size}개 받음")
            dtoList.map {
                it.toDomain()
            }.toList()
        } else {
            emptyList()
        }
        result
    }.getOrElse {
        emptyList()
    }
}