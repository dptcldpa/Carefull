package com.cases.carefull.domain.repository.routine.diet

import com.cases.carefull.domain.model.routine.diet.FavoriteFood
import com.cases.carefull.domain.util.DataResourceResult
import kotlinx.coroutines.flow.Flow

interface FavoriteFoodRepository {
    fun getAllFavoriteFoods(): Flow<DataResourceResult<List<FavoriteFood>>>
    fun saveFavoriteFood(favoriteFood: FavoriteFood): Flow<DataResourceResult<Boolean>>
    fun deleteFavoriteFood(favoriteFood: FavoriteFood): Flow<DataResourceResult<Boolean>>
}
