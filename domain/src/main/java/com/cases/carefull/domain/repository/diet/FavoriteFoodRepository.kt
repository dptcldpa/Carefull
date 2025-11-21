package com.cases.carefull.domain.repository.diet

import com.cases.carefull.domain.model.diet.FavoriteMeal
import kotlinx.coroutines.flow.Flow

interface FavoriteFoodRepository {
    fun getFavorites(): Flow<List<FavoriteMeal>>
    suspend fun addFavorite(meal: FavoriteMeal)
    suspend fun deleteFavorite(meal: FavoriteMeal)
}