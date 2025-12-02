package com.cases.carefull.domain.model.routine.diet

data class FavoriteFood(
    val id: Int = 0,
    val name: String,
    val servingSize: Int,
    val kcal: Int,
    val carbohydrate: Int,
    val protein: Int,
    val fat: Int
)
