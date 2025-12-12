package com.cases.carefull.domain.repository.routine.diet

import com.cases.carefull.domain.model.routine.diet.RecentFoodSearch
import com.cases.carefull.domain.util.DataResourceResult
import kotlinx.coroutines.flow.Flow

interface RecentMealSearchRepository {
    fun getRecentSearches(): Flow<DataResourceResult<List<RecentFoodSearch>>>
    fun saveRecentSearch(query: String): Flow<DataResourceResult<Boolean>>
    fun deleteRecentSearch(search: RecentFoodSearch): Flow<DataResourceResult<Boolean>>
    fun clearAllRecentSearches(): Flow<DataResourceResult<Boolean>>
}
