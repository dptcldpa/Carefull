package com.cases.carefull.data.model


import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "favorite_meals")
data class FavoriteMealEntity(
	@PrimaryKey(autoGenerate = true)
	val id: Int = 0,
	val name: String,
	val weight: Int,
	val kcal: Int,
	val carbohydrate: Int,
	val protein: Int,
	val fat: Int
)