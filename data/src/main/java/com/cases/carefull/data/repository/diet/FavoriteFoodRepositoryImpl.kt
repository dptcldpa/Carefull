package com.cases.carefull.data.repository.diet

import android.content.Context
import com.cases.carefull.data.dao.FavoriteMealDao
import com.cases.carefull.data.database.AppDatabase
import com.cases.carefull.data.mapper.toDomain
import com.cases.carefull.data.mapper.toEntity
import com.cases.carefull.domain.model.diet.FavoriteMeal
import com.cases.carefull.domain.repository.diet.FavoriteFoodRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class FavoriteFoodRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : FavoriteFoodRepository {
    private val favoriteMealDao: FavoriteMealDao =
        AppDatabase.Companion.getInstance(context).favoriteMealDao()

    override fun getFavorites(): Flow<List<FavoriteMeal>> {
        return favoriteMealDao.getAll().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun addFavorite(meal: FavoriteMeal) {
        favoriteMealDao.insert(meal.toEntity())
    }

    override suspend fun deleteFavorite(meal: FavoriteMeal) {
        favoriteMealDao.delete(meal.toEntity())
    }

}