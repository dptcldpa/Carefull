package com.cases.carefull.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.cases.carefull.data.dao.BmrDao
import com.cases.carefull.data.model.BmrCollection

@Database(entities = [BmrCollection::class], version = 1)
abstract class BmrDatabase : RoomDatabase() {
	
	abstract fun bmrDao(): BmrDao
	
	companion object {
		@Volatile
		private var INSTANCE: BmrDatabase? = null
		fun getInstance(context: Context): BmrDatabase {
			return INSTANCE ?: synchronized(this) {
				val instance = Room.databaseBuilder(
					context.applicationContext,
					BmrDatabase::class.java,
					"app_database"
				).build()
				INSTANCE = instance
				instance
			}
		}
	}
}