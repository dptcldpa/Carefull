package com.cases.carefull.data.mapper

import com.cases.carefull.data.entity.FavoriteFoodEntity
import com.cases.carefull.domain.model.diet.FavoriteFood

fun FavoriteFoodEntity.toDomain(): FavoriteFood {
    return FavoriteFood(
        id = this.id,
        name = this.name,
        servingSize = this.weight,
        kcal = this.kcal,
        carbohydrate = this.carbohydrate,
        protein = this.protein,
        fat = this.fat
    )
}

fun FavoriteFood.toEntity(): FavoriteFoodEntity {
    return FavoriteFoodEntity(
        id = this.id,
        name = this.name,
        weight = this.servingSize,
        kcal = this.kcal,
        carbohydrate = this.carbohydrate,
        protein = this.protein,
        fat = this.fat
    )
}
