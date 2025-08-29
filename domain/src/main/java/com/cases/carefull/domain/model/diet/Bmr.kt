package com.cases.carefull.domain.model.diet

data class Bmr(
	val userId: String,
	val gender: Boolean,
	val age: Int,
	val height: Int,
	val weight: Int,
	val activity: BmrActivity = BmrActivity.NONE,
	val bmr: Int,
	val activityBmr: Int
)