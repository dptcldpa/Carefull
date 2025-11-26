package com.cases.carefull.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.cases.carefull.data.dao.BmrDao
import com.cases.carefull.data.dao.FavoriteFoodDao
import com.cases.carefull.data.dao.RecentFoodSearchDao
import com.cases.carefull.data.entity.BmrEntity
import com.cases.carefull.data.entity.FavoriteFoodEntity
import com.cases.carefull.data.entity.RecentFoodSearchEntity

@Database(
    entities = [
        BmrEntity::class,
        FavoriteFoodEntity::class,
        RecentFoodSearchEntity::class
    ],
    version = 1,
    exportSchema = false // 배포 시엔 true로
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun bmrDao(): BmrDao
    abstract fun favoriteMealDao(): FavoriteFoodDao
    abstract fun recentFoodSearchDao(): RecentFoodSearchDao
}