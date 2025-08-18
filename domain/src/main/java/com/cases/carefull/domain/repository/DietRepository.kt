package com.cases.carefull.domain.repository

import com.cases.carefull.domain.model.DietCollection
import com.cases.carefull.domain.util.DataResourceResult

interface DietRepository {
    suspend fun getAllMeal(): DataResourceResult<List<DietCollection>>
    suspend fun addMeal(mealData: DietCollection): DataResourceResult<Unit>
    suspend fun searchMeals(query: String): List<DietCollection>
    suspend fun removeMeal(mealData: DietCollection)
}