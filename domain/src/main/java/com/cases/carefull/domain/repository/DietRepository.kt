package com.cases.carefull.domain.repository

import com.cases.carefull.domain.model.diet.Bmr
import com.cases.carefull.domain.model.diet.DietCollection
import com.cases.carefull.domain.model.diet.FavoriteMeal
import com.cases.carefull.domain.model.diet.RecentMealSearch
import com.cases.carefull.domain.model.exercise.Pose
import com.cases.carefull.domain.util.DataResourceResult
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface DietRepository {
	fun getAllMeal(): Flow<DataResourceResult<Map<LocalDate, List<DietCollection>>>>
	suspend fun addMeal(mealData: DietCollection): DataResourceResult<Unit>
	suspend fun removeMeal(documentId: String): DataResourceResult<Unit>
	suspend fun searchMeals(query: String): List<DietCollection>
	suspend fun analyzeImage(image: Any): DataResourceResult<Pose>
	fun getMyBmr(userId: String): Flow<Bmr?>
	suspend fun insertBmr(bmr: Bmr)
	fun getFavorites(): Flow<List<FavoriteMeal>>
	suspend fun addFavorite(meal: FavoriteMeal)
	suspend fun deleteFavorite(meal: FavoriteMeal)
	fun getRecentSearches(): Flow<List<RecentMealSearch>>
	suspend fun addSearch(query: String)
	suspend fun deleteSearch(search: RecentMealSearch)
	suspend fun clearAllSearches()
}