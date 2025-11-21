package com.cases.carefull.data.repository.diet

import android.content.Context
import com.cases.carefull.data.dao.RecentMealSearchDao
import com.cases.carefull.data.database.AppDatabase
import com.cases.carefull.data.mapper.toDomain
import com.cases.carefull.data.mapper.toEntity
import com.cases.carefull.domain.model.diet.RecentMealSearch
import com.cases.carefull.domain.repository.diet.DietSearchRecordRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DietSearchRecordRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : DietSearchRecordRepository {

    private val recentMealSearchDao: RecentMealSearchDao =
        AppDatabase.Companion.getInstance(context).recentMealSearchDao()

    override fun getRecentSearches(): Flow<List<RecentMealSearch>> {
        return recentMealSearchDao.getAll().map { entities -> entities.map { it.toDomain() } }
    }

    override suspend fun addSearch(query: String) {
        recentMealSearchDao.insertOrUpdate(query.trim())
    }

    override suspend fun deleteSearch(search: RecentMealSearch) {
        recentMealSearchDao.delete(search.toEntity())
    }

    override suspend fun clearAllSearches() {
        recentMealSearchDao.clearAll()
    }
}