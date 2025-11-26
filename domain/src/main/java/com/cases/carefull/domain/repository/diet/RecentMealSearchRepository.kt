package com.cases.carefull.domain.repository.diet

import com.cases.carefull.domain.model.diet.RecentFoodSearch
import kotlinx.coroutines.flow.Flow

interface RecentMealSearchRepository {
    fun getRecentSearches(): Flow<List<RecentFoodSearch>>
    suspend fun saveRecentSearch(query: String)
    suspend fun deleteRecentSearch(search: RecentFoodSearch)
    suspend fun clearAllRecentSearches()
}
