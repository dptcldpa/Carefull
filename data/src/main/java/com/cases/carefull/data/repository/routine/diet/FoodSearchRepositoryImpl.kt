package com.cases.carefull.data.repository.routine.diet

import com.cases.carefull.data.di.DietApiKey
import com.cases.carefull.data.mapper.toDomain
import com.cases.carefull.data.network.DietApiService
import com.cases.carefull.domain.model.routine.diet.FoodItem
import com.cases.carefull.domain.repository.routine.diet.FoodSearchRepository
import com.cases.carefull.domain.util.DataResourceResult
import javax.inject.Inject

class FoodSearchRepositoryImpl @Inject constructor(
    private val apiService: DietApiService,
    @param:DietApiKey private val dietApiKey: String,
) : FoodSearchRepository {
    override suspend fun searchFoods(query: String): DataResourceResult<List<FoodItem>> =
        runCatching {
            val response = apiService.getFoodList(
                apiKey = dietApiKey,
                query = query
            )
            if (response.header.resultCode == "00") {
                val items = response.body.items
                val domainList = items?.map { it.toDomain() } ?: emptyList()
                DataResourceResult.Success(domainList)
            } else {
                DataResourceResult.Error(Exception(response.header.resultMsg))
            }
        }.getOrElse {
            DataResourceResult.Error(it)
        }
}
