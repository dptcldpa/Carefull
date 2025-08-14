package com.cases.carefull.data.mapper

import com.cases.carefull.data.model.DietItemDto
import com.cases.carefull.domain.model.diet.DietCollection
import com.cases.carefull.domain.model.diet.MealType

fun DietItemDto.toDomain(): DietCollection {
	
	val kcalValue = this.kcal?.toDoubleOrNull()?.toInt()?:0
	val carbohydrateValue = this.carbohydrate?.toDoubleOrNull()?.toInt()?:0
	val proteinValue = this.protein?.toDoubleOrNull()?.toInt()?:0
	val fatValue = this.fat?.toDoubleOrNull()?.toInt()?:0
	val servingValue = this.serving?.filter { it.isDigit() }?.toIntOrNull()?:0

	return DietCollection(
		mealName = this.name?:"",
		kcal = kcalValue,
		carbohydrate = carbohydrateValue,
		protein = proteinValue,
		fat = fatValue,
		weight = servingValue,
		mealType = MealType.SNACK.name
	)
}