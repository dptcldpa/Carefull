package com.cases.carefull.domain.repository.routine.diet

import com.cases.carefull.domain.model.routine.diet.FavoriteFood
import kotlinx.coroutines.flow.Flow

interface FavoriteFoodRepository {
    fun getAllFavoriteFoods(): Flow<List<FavoriteFood>>
    suspend fun saveFavoriteFood(favoriteFood: FavoriteFood)
    suspend fun deleteFavoriteFood(favoriteFood: FavoriteFood)
}
