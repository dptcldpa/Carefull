package com.cases.carefull.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.cases.carefull.data.entity.RecentFoodSearchEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RecentFoodSearchDao {

    @Query("SELECT * FROM recent_food_searches ORDER BY searchedAt DESC LIMIT 20")
    fun getRecentSearches(): Flow<List<RecentFoodSearchEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrReplaceRecentSearch(search: RecentFoodSearchEntity)

    @Delete
    suspend fun deleteRecentSearch(search: RecentFoodSearchEntity)

    @Query("DELETE FROM recent_food_searches")
    suspend fun clearAllRecentSearches()
}