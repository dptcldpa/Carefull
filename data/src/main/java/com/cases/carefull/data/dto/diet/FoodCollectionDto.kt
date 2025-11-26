package com.cases.carefull.data.dto.diet

import com.cases.carefull.domain.model.diet.FoodItem
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class FoodCollectionDto(
    val user_id: String = "",
    val meal_type: String = "",
    val food_name: String = "",
    val kcal: Int = 0,
    val carbohydrate: Int = 0,
    val protein: Int = 0,
    val fat: Int = 0,
    val servingSize: Int = 0,
    val created_at: Timestamp? = null,
    @ServerTimestamp
    val updated_at: Timestamp? = null
)

fun FoodItem.toFirestoreFoodCollectionDto(): FoodCollectionDto {
    return FoodCollectionDto(
        user_id = this.userId,
        meal_type = this.type,
        food_name = this.name,
        kcal = this.kcal,
        carbohydrate = this.carbohydrate,
        protein = this.protein,
        fat = this.fat,
        servingSize = this.servingSize,
        created_at = Timestamp(Date(this.createdAt)),
        updated_at = null
    )
}

fun FoodCollectionDto.toDomainFoodCollection(): FoodItem {
    return FoodItem(
        userId = this.user_id,
        type = this.meal_type,
        name = this.food_name,
        kcal = this.kcal,
        carbohydrate = this.carbohydrate,
        protein = this.protein,
        fat = this.fat,
        servingSize = this.servingSize,
        createdAt = this.created_at?.toDate()?.time ?: 0L,
        updatedAt = this.updated_at?.toDate()?.time ?: 0L
    )
}
