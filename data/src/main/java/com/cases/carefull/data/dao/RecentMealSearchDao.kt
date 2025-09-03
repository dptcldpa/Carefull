package com.cases.carefull.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.cases.carefull.data.model.RecentMealSearchEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RecentMealSearchDao {
	@Query("SELECT * FROM recent_meal_searches ORDER BY timestamp DESC")
	fun getAll(): Flow<List<RecentMealSearchEntity>>
	@Query("SELECT * FROM recent_meal_searches WHERE `name` = :query")
	suspend fun findByQuery(query: String): RecentMealSearchEntity?
	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insert(search: RecentMealSearchEntity)
	@Query("UPDATE recent_meal_searches SET timestamp = :timestamp WHERE `name` = :query")
	suspend fun updateTimestamp(query: String, timestamp: Long)
	@Transaction
	suspend fun insertOrUpdate(query: String) {
		val existing = findByQuery(query)
		if (existing != null) {
			updateTimestamp(query, System.currentTimeMillis())
		} else {
			insert(RecentMealSearchEntity(name = query, timestamp = System.currentTimeMillis()))
		}
	}
	@Delete
	suspend fun delete(search: RecentMealSearchEntity)
	@Query("DELETE FROM recent_meal_searches")
	suspend fun clearAll()
}