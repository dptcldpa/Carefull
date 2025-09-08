package com.cases.carefull.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.cases.carefull.data.dto.FavoriteMealEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteMealDao {
	
	@Query("SELECT * FROM favorite_meals ORDER BY id DESC")
	fun getAll(): Flow<List<FavoriteMealEntity>>
	
	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insert(meal: FavoriteMealEntity)
	
	@Delete
	suspend fun delete(meal: FavoriteMealEntity)
}