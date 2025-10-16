package com.cases.carefull.domain.model.diet

data class Bmr(
	val userId: String,
	val gender: Boolean,
	val age: Int,
	val height: Int,
	val weight: Int,
	val movementLevel: BmrMovementLevel = BmrMovementLevel.NONE,
	val bmr: Int,
	val movementLevelBmr: Int
)

data class BmrCalculationResult(val bmr: Int, val tdee: Int)