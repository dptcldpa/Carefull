package com.cases.carefull.domain.repository.diet

import com.cases.carefull.domain.model.diet.FoodItem
import com.cases.carefull.domain.util.DataResourceResult

interface FoodSearchRepository {
    suspend fun searchFoods(query: String): DataResourceResult<List<FoodItem>>
}
