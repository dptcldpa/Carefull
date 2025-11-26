package com.cases.carefull.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.cases.carefull.data.entity.BmrEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BmrDao {
	@Query("SELECT * FROM bmr_collection WHERE userId = :userId")
	fun findBmr(userId: String): Flow<BmrEntity?>

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insertBmr(bmr: BmrEntity)
}
