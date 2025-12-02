package com.cases.carefull.domain.repository.routine.diet

import com.cases.carefull.domain.model.routine.diet.RecentFoodSearch
import kotlinx.coroutines.flow.Flow

interface RecentMealSearchRepository {
    fun getRecentSearches(): Flow<List<RecentFoodSearch>>
    suspend fun saveRecentSearch(query: String)
    suspend fun deleteRecentSearch(search: RecentFoodSearch)
    suspend fun clearAllRecentSearches()
}
