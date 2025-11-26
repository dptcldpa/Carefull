package com.cases.carefull.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_food")
data class FavoriteFoodEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val name: String,
    val weight: Int,
    val kcal: Int,
    val carbohydrate: Int,
    val protein: Int,
    val fat: Int
)
