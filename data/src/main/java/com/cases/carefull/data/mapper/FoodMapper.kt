package com.cases.carefull.data.mapper

import com.cases.carefull.data.dto.diet.FoodItemDto
import com.cases.carefull.domain.model.diet.FoodItem
import com.cases.carefull.domain.model.diet.MealType
import com.cases.carefull.domain.util.toSafeInt

fun FoodItemDto.toDomain(): FoodItem {
    val servingValue = this.servingSize?.filter { it.isDigit() }?.toIntOrNull() ?: 0

    return FoodItem(
        name = this.name ?: "",
        kcal = this.kcal.toSafeInt(),
        carbohydrate = this.carbohydrate.toSafeInt(),
        protein = this.protein.toSafeInt(),
        fat = this.fat.toSafeInt(),
        servingSize = servingValue,
        type = MealType.SNACK.name
    )
}
