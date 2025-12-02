package com.cases.carefull.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.cases.carefull.domain.model.routine.diet.BmrMovementLevel

@Entity(tableName = "bmr_collection")
data class BmrEntity(
    @PrimaryKey
    val userId: String,

    val gender: Boolean,
    val age: Int,
    val height: Int,
    val weight: Int,
    val movementLevel: BmrMovementLevel = BmrMovementLevel.NONE,
    val bmr: Int,
    val movementLevelBmr: Int
)
