package com.cases.carefull.domain.repository

import com.cases.carefull.domain.model.diet.DietCollection
import com.cases.carefull.domain.model.exercise.Pose
import com.cases.carefull.domain.util.DataResult

interface DietRepository {
    suspend fun getAllMeal(): DataResult<List<DietCollection>>
    suspend fun addMeal(mealData: DietCollection): DataResult<Unit>
    suspend fun searchMeals(query: String): List<DietCollection>
    suspend fun analyzeImage(image: Any): DataResult<Pose>
    suspend fun removeMeal(mealData: DietCollection)
}