package com.cases.carefull.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.cases.carefull.data.dto.BmrCollection
import kotlinx.coroutines.flow.Flow

@Dao
interface BmrDao {
	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insertBmr(bmr: BmrCollection)
	
	@Query("SELECT * FROM bmr_collection")
	fun getAllBmr(): Flow<List<BmrCollection>>
	
	@Delete
	suspend fun deleteBmr(bmr: BmrCollection)
	
	@Query("SELECT * FROM bmr_collection WHERE userId = :userId")
	fun getBmrByUserId(userId: String): Flow<BmrCollection?>
}