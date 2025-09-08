package com.cases.carefull.data.mapper

import com.cases.carefull.data.dto.FavoriteMealEntity
import com.cases.carefull.domain.model.diet.FavoriteMeal

fun FavoriteMealEntity.toDomain(): FavoriteMeal {
	return FavoriteMeal(
		id = this.id,
		name = this.name,
		weight = this.weight,
		kcal = this.kcal,
		carbohydrate = this.carbohydrate,
		protein = this.protein,
		fat = this.fat
	)
}

fun FavoriteMeal.toEntity(): FavoriteMealEntity {
	return FavoriteMealEntity(
		id = this.id,
		name = this.name,
		weight = this.weight,
		kcal = this.kcal,
		carbohydrate = this.carbohydrate,
		protein = this.protein,
		fat = this.fat
	)
}