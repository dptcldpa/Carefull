package com.cases.carefull.data.mapper

import com.cases.carefull.data.model.DietItemDtoTwo
import com.cases.carefull.domain.model.DietCollection
import com.cases.carefull.domain.model.MealType

//fun DietItemDto.toDomain(): DietInfo {
//	return DietInfo(
//		name = this.name,
//		calories = this.kcal?.toDoubleOrNull()?.toInt(),
//		carbs = this.carbohydrate?.toDoubleOrNull()?.toInt(),
//		proteins = this.protein?.toDoubleOrNull()?.toInt(),
//		fats = this.fat?.toDoubleOrNull()?.toInt(),
//		weight = this.serving?.filter { it.isDigit() }?.toIntOrNull(),
//		mealType = MealType.SNACK
//	)
//}

fun DietItemDtoTwo.toDomainTwo(): DietCollection {
	
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