package com.cases.carefull.data.mapper

import com.cases.carefull.data.model.DietItemDto
import com.cases.carefull.domain.model.DietInfo
import com.cases.carefull.domain.model.MealType

fun DietItemDto.toDomain(): DietInfo {
	return DietInfo(
		name = this.name,
		calories = this.kcal?.toDoubleOrNull()?.toInt(),
		carbs = this.carbohydrate?.toDoubleOrNull()?.toInt(),
		proteins = this.protein?.toDoubleOrNull()?.toInt(),
		fats = this.fat?.toDoubleOrNull()?.toInt(),
		weight = this.serving?.filter { it.isDigit() }?.toIntOrNull(),
		mealType = MealType.SNACK
	)
}