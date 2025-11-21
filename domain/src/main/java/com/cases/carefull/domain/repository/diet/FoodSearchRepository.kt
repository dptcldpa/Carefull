package com.cases.carefull.domain.repository.diet

import com.cases.carefull.domain.model.diet.DietCollection

interface FoodSearchRepository {
    suspend fun searchMeals(query: String): List<DietCollection>
}