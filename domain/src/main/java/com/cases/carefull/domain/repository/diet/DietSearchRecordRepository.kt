package com.cases.carefull.domain.repository.diet

import com.cases.carefull.domain.model.diet.RecentMealSearch
import kotlinx.coroutines.flow.Flow

interface DietSearchRecordRepository {
    fun getRecentSearches(): Flow<List<RecentMealSearch>>
    suspend fun addSearch(query: String)
    suspend fun deleteSearch(search: RecentMealSearch)
    suspend fun clearAllSearches()
}