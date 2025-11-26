package com.cases.carefull.data.di

import android.content.Context
import androidx.room.Room
import com.cases.carefull.data.database.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "carefull_database"
        )
            .build()
    }

    @Provides
    @Singleton
    fun provideBmrDao(database: AppDatabase) = database.bmrDao()

    @Provides
    @Singleton
    fun provideFavoriteMealDao(database: AppDatabase) = database.favoriteMealDao()

    @Provides
    @Singleton
    fun provideRecentMealSearchDao(database: AppDatabase) = database.recentFoodSearchDao()

}