package com.cases.carefull.data.repository.diet

import com.cases.carefull.data.dao.FavoriteFoodDao
import com.cases.carefull.data.mapper.toDomain
import com.cases.carefull.data.mapper.toEntity
import com.cases.carefull.domain.model.diet.FavoriteFood
import com.cases.carefull.domain.repository.diet.FavoriteFoodRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class FavoriteFoodRepositoryImpl @Inject constructor(
    private val favoriteFoodDao: FavoriteFoodDao
) : FavoriteFoodRepository {
    override fun getAllFavoriteFoods(): Flow<List<FavoriteFood>> {
        return favoriteFoodDao.getAllFavoriteFoods()
            .map { entities ->
                entities.map { it.toDomain() }
            }
    }

    override suspend fun saveFavoriteFood(favoriteFood: FavoriteFood) {
        favoriteFoodDao.insertFavoriteFood(favoriteFood.toEntity())
    }

    override suspend fun deleteFavoriteFood(favoriteFood: FavoriteFood) {
        favoriteFoodDao.deleteFavoriteFood(favoriteFood.toEntity())
    }
}
