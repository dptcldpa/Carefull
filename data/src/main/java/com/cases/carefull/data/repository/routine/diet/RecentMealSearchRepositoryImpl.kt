package com.cases.carefull.data.repository.routine.diet

import com.cases.carefull.data.dao.RecentFoodSearchDao
import com.cases.carefull.data.entity.RecentFoodSearchEntity
import com.cases.carefull.data.mapper.toDomain
import com.cases.carefull.data.mapper.toEntity
import com.cases.carefull.domain.model.routine.diet.RecentFoodSearch
import com.cases.carefull.domain.repository.routine.diet.RecentMealSearchRepository
import com.cases.carefull.domain.util.DataResourceResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

class RecentMealSearchRepositoryImpl @Inject constructor(
    private val recentFoodSearchDao: RecentFoodSearchDao
) : RecentMealSearchRepository {

    override fun getRecentSearches(): Flow<DataResourceResult<List<RecentFoodSearch>>> {
        return recentFoodSearchDao.getRecentSearches()
            .map { entities ->
                val domainModel = entities.map { it.toDomain() }
                DataResourceResult.Success(domainModel) as DataResourceResult<List<RecentFoodSearch>>
            }
            .onStart { emit(DataResourceResult.Loading) }
            .catch { emit(DataResourceResult.Error(it)) }
            .flowOn(Dispatchers.IO)
    }

    override fun saveRecentSearch(query: String): Flow<DataResourceResult<Boolean>> = flow {
        emit(DataResourceResult.Loading)

        val entity =
            RecentFoodSearchEntity(
                query = query.trim(),
                searchedAt = System.currentTimeMillis()
            )
        recentFoodSearchDao.insertOrReplaceRecentSearch(entity)

        emit(DataResourceResult.Success(true))
    }
        .catch { emit(DataResourceResult.Error(it)) }
        .flowOn(Dispatchers.IO)

    override fun deleteRecentSearch(search: RecentFoodSearch): Flow<DataResourceResult<Boolean>> =
        flow {
            emit(DataResourceResult.Loading)

            recentFoodSearchDao.deleteRecentSearch(search.toEntity())
            emit(DataResourceResult.Success(true))
        }
            .catch { emit(DataResourceResult.Error(it)) }
            .flowOn(Dispatchers.IO)

    override fun clearAllRecentSearches(): Flow<DataResourceResult<Boolean>> = flow {
        emit(DataResourceResult.Loading)

        recentFoodSearchDao.clearAllRecentSearches()
        emit(DataResourceResult.Success(true))
    }
        .catch { emit(DataResourceResult.Error(it)) }
        .flowOn(Dispatchers.IO)
}

