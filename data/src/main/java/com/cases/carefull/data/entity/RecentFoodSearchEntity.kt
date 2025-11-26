package com.cases.carefull.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recent_food_searches")
data class RecentFoodSearchEntity(
    @PrimaryKey
    val query: String,

    val searchedAt: Long
)
