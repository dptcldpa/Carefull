package com.cases.carefull.data.repository.routine.diet

import com.cases.carefull.data.dao.RecentFoodSearchDao
import com.cases.carefull.data.entity.RecentFoodSearchEntity
import com.cases.carefull.data.mapper.toDomain
import com.cases.carefull.data.mapper.toEntity
import com.cases.carefull.domain.model.routine.diet.RecentFoodSearch
import com.cases.carefull.domain.repository.routine.diet.RecentMealSearchRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class RecentMealSearchRepositoryImpl @Inject constructor(
    private val recentFoodSearchDao: RecentFoodSearchDao
) : RecentMealSearchRepository {

    override fun getRecentSearches(): Flow<List<RecentFoodSearch>> {
        return recentFoodSearchDao.getRecentSearches()
            .map { entities -> entities.map { it.toDomain() } }
    }

    override suspend fun saveRecentSearch(query: String) {
        val entity =
            RecentFoodSearchEntity(
                query = query.trim(),
                searchedAt = System.currentTimeMillis()
            )
        recentFoodSearchDao.insertOrReplaceRecentSearch(entity)
    }

    override suspend fun deleteRecentSearch(search: RecentFoodSearch) {
        recentFoodSearchDao.deleteRecentSearch(search.toEntity())
    }

    override suspend fun clearAllRecentSearches() {
        recentFoodSearchDao.clearAllRecentSearches()
    }
}
