package com.cases.carefull.domain.repository

import com.cases.carefull.domain.model.diet.Bmr
import com.cases.carefull.domain.model.diet.DietCollection
import com.cases.carefull.domain.model.exercise.Pose
import com.cases.carefull.domain.util.DataResourceResult
import kotlinx.coroutines.flow.Flow

interface DietRepository {
	suspend fun getAllMeal(): DataResourceResult<List<DietCollection>>
	suspend fun addMeal(mealData: DietCollection): DataResourceResult<Unit>
	suspend fun searchMeals(query: String): List<DietCollection>
	suspend fun analyzeImage(image: Any): DataResourceResult<Pose>
	suspend fun removeMeal(mealData: DietCollection)
	suspend fun getMyBmr(userId: String): Flow<Bmr?>
	suspend fun insertBmr(bmr: Bmr)
}