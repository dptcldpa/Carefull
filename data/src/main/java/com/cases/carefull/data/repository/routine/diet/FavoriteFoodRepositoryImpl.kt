package com.cases.carefull.data.repository.routine.diet

import com.cases.carefull.data.dao.FavoriteFoodDao
import com.cases.carefull.data.mapper.toDomain
import com.cases.carefull.data.mapper.toEntity
import com.cases.carefull.domain.model.routine.diet.FavoriteFood
import com.cases.carefull.domain.repository.routine.diet.FavoriteFoodRepository
import com.cases.carefull.domain.util.DataResourceResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

class FavoriteFoodRepositoryImpl @Inject constructor(
    private val favoriteFoodDao: FavoriteFoodDao
) : FavoriteFoodRepository {

    override fun getAllFavoriteFoods(): Flow<DataResourceResult<List<FavoriteFood>>> {
        return favoriteFoodDao.getAllFavoriteFoods()
            .map { entities ->
                val domainModel = entities.map { it.toDomain() }
                DataResourceResult.Success(domainModel) as DataResourceResult<List<FavoriteFood>>
            }
            .onStart { emit(DataResourceResult.Loading) }
            .catch { emit(DataResourceResult.Error(it)) }
            .flowOn(Dispatchers.IO)
    }

    override fun saveFavoriteFood(favoriteFood: FavoriteFood): Flow<DataResourceResult<Boolean>> =
        flow {
            emit(DataResourceResult.Loading)
            favoriteFoodDao.insertFavoriteFood(favoriteFood.toEntity())
            emit(DataResourceResult.Success(true))
        }
            .catch { emit(DataResourceResult.Error(it)) }
            .flowOn(Dispatchers.IO)

    override fun deleteFavoriteFood(favoriteFood: FavoriteFood): Flow<DataResourceResult<Boolean>> =
        flow {
            emit(DataResourceResult.Loading)
            favoriteFoodDao.deleteFavoriteFood(favoriteFood.toEntity())
            emit(DataResourceResult.Success(true))
        }
            .catch { emit(DataResourceResult.Error(it)) }
            .flowOn(Dispatchers.IO)
}
