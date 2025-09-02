package com.cases.carefull.domain.model.diet

data class FavoriteMeal(
	val id: Int = 0,
	val name: String,
	val weight: Int,
	val kcal: Int,
	val carbohydrate: Int,
	val protein: Int,
	val fat: Int
)