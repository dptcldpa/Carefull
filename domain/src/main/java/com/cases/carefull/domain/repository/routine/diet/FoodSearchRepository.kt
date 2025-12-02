package com.cases.carefull.domain.repository.routine.diet

import com.cases.carefull.domain.model.routine.diet.FoodItem
import com.cases.carefull.domain.util.DataResourceResult

interface FoodSearchRepository {
    suspend fun searchFoods(query: String): DataResourceResult<List<FoodItem>>
}
