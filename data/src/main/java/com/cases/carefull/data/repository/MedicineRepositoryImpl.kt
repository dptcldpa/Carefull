package com.cases.carefull.data.repository

import android.util.Log
import com.cases.carefull.data.dto.MedicineItemDto
import com.cases.carefull.data.network.MedicineApiService
import com.cases.carefull.domain.model.MedicineItem
import com.cases.carefull.data.mapper.toDomain
import com.cases.carefull.domain.repository.MedicineRepository
//import com.cases.carefull.BuildConfig

class MedicineRepositoryImpl(
    private val apiService: MedicineApiService
) : MedicineRepository {

    private val serviceKey
    
    override suspend fun searchMedicines(query: String): Result<List<MedicineItem>> {
        if (query.isBlank()) {
            return Result.success(emptyList())
        }
        Log.d("API_TEST", "Repository: API 호출 시도, query = $query")
        return try {
            val response = apiService.getMedicineList(
                serviceKey = serviceKey,
//                serviceKey = BuildConfig.medicine_api_key,
                itemName = query
            )
            Log.d("API_TEST", "Repository: API 호출 시도, ${response.header.resultCode}")
            if (response.header.resultCode == "00") {
                val dtoList: List<MedicineItemDto> = response.body?.items ?: emptyList()

                Log.d("API_TEST", "Repository: API 호출 성공, ${dtoList.size}개 받음")
                val domainList = dtoList.map { it.toDomain() }
                Result.success(domainList)
            } else {
                Log.e("API_TEST", "Repository: API 오류 - ${response.header.resultMsg}")
                Result.failure(Exception("API Error: ${response.header.resultMsg}"))
            }
        } catch (e: Exception) {
            Log.e("API_TEST", "Repository: 네트워크 예외 발생", e)
            Result.failure(e)
        }
    }
}