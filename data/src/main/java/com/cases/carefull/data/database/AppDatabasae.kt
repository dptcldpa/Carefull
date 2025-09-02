package com.cases.carefull.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.cases.carefull.data.dao.BmrDao
import com.cases.carefull.data.dao.FavoriteMealDao
import com.cases.carefull.data.dao.RecentMealSearchDao
import com.cases.carefull.data.model.BmrCollection
import com.cases.carefull.data.model.FavoriteMealEntity
import com.cases.carefull.data.model.RecentMealSearchEntity

@Database(
	entities = [
		BmrCollection::class,
		FavoriteMealEntity::class,
		RecentMealSearchEntity::class
	],
	version = 1,
	exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
	abstract fun bmrDao(): BmrDao
	abstract fun favoriteMealDao(): FavoriteMealDao
	abstract fun recentMealSearchDao(): RecentMealSearchDao
	
	companion object {
		@Volatile
		private var INSTANCE: AppDatabase? = null
		
		fun getInstance(context: Context): AppDatabase {
			return INSTANCE ?: synchronized(this) {
				val instance = Room.databaseBuilder(
					context.applicationContext,
					AppDatabase::class.java,
					"carefull_database"
				)
					.build()
				INSTANCE = instance
				instance
			}
		}
	}
}