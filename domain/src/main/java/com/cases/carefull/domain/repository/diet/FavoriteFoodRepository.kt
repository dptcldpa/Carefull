package com.cases.carefull.domain.repository.diet

import com.cases.carefull.domain.model.diet.FavoriteFood
import kotlinx.coroutines.flow.Flow

interface FavoriteFoodRepository {
    fun getAllFavoriteFoods(): Flow<List<FavoriteFood>>
    suspend fun saveFavoriteFood(favoriteFood: FavoriteFood)
    suspend fun deleteFavoriteFood(favoriteFood: FavoriteFood)
}
