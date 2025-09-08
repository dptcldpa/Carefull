package com.cases.carefull.data.dto

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.cases.carefull.domain.model.diet.BmrActivity

@Entity(tableName = "bmr_collection")
data class BmrCollection (
	@PrimaryKey
	val userId: String,
	
	val gender: Boolean,
	val age: Int,
	val height: Int,
	val weight: Int,
	val activity: BmrActivity = BmrActivity.NONE,
	val bmr: Int,
	val activityBmr: Int
)