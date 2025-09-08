package com.cases.carefull.data.dto

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
	tableName = "recent_meal_searches",
	indices = [Index(value = ["name"], unique = true)]
)
data class RecentMealSearchEntity(
	@PrimaryKey(autoGenerate = true)
	val id: Int = 0,
	
	val name: String,
	val timestamp: Long
)