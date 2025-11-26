package com.cases.carefull.domain.model.diet

import kotlin.math.roundToInt


data class FoodItem(
    val documentId: String = "",
    val userId: String = "",
    val type: String = "",
    val name: String = "",
    val kcal: Int = 0,
    val servingSize: Int = 0,
    val carbohydrate: Int = 0,
    val protein: Int = 0,
    val fat: Int = 0,
    val createdAt: Long = 0,
    val updatedAt: Long = 0
) {
    fun adjustPortion(newServingSize: Int): FoodItem {
        if (this.servingSize <= 0 || newServingSize == this.servingSize) return this.copy(
            servingSize = newServingSize
        )
        val ratio = newServingSize.toDouble() / this.servingSize.toDouble()
        return this.copy(
            servingSize = newServingSize,
            kcal = (this.kcal * ratio).roundToInt(),
            carbohydrate = (this.carbohydrate * ratio).roundToInt(),
            protein = (this.protein * ratio).roundToInt(),
            fat = (this.fat * ratio).roundToInt(),
        )
    }

    companion object {
        fun calculateCalories(carbohydrate: Int, protein: Int, fat: Int): Int {
            return (carbohydrate * 4) + (protein * 4) + (fat * 9)
        }
    }
}
