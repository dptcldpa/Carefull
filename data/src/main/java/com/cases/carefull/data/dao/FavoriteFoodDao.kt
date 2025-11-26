package com.cases.carefull.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.cases.carefull.data.entity.FavoriteFoodEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteFoodDao {

    @Query("SELECT * FROM favorite_food ORDER BY id DESC")
    fun getAllFavoriteFoods(): Flow<List<FavoriteFoodEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavoriteFood(food: FavoriteFoodEntity)

    @Delete
    suspend fun deleteFavoriteFood(food: FavoriteFoodEntity)
}