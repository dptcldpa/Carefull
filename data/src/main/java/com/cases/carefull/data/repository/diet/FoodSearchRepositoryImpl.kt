package com.cases.carefull.data.repository.diet

import com.cases.carefull.data.di.DietApiKey
import com.cases.carefull.data.dto.DietItemDto
import com.cases.carefull.data.mapper.toDomain
import com.cases.carefull.data.network.DietApiService
import com.cases.carefull.domain.model.diet.DietCollection
import com.cases.carefull.domain.repository.diet.FoodSearchRepository
import javax.inject.Inject

class FoodSearchRepositoryImpl @Inject constructor(
    private val apiService: DietApiService,
    @DietApiKey private val dietApiKey: String,
) : FoodSearchRepository {

    override suspend fun searchMeals(
        query: String
    ): List<DietCollection> = runCatching {
        val response = apiService.getFoodList(
            apiKey = dietApiKey,
            foodName = query
        )
        if (response.header.resultCode == "00" && response.body.items.isNotEmpty()) {
            val dtoList: List<DietItemDto> = response.body.items
            dtoList.map { it.toDomain() }
        } else {
            emptyList()
        }
    }.getOrElse {
        emptyList()
    }
}