package com.cases.carefull.domain.usecase.bmr

import com.cases.carefull.domain.model.diet.BmrCalculationResult
import com.cases.carefull.domain.model.diet.BmrMovementLevel
import com.cases.carefull.domain.model.diet.Gender

class CalculateBmrUseCase(
) {
	operator fun invoke(
		gender: Gender,
		height: Int,
		weight: Int,
		age: Int,
		movementLevel: BmrMovementLevel
	): BmrCalculationResult {
		if (height <= 0 || weight <= 0 || age <= 0) {
			return BmrCalculationResult(0, 0)
		}
		val bmr = when (gender) {
			Gender.MALE -> (10 * weight) + (6.25 * height) - (5 * age) + 5
			Gender.FEMALE -> (10 * weight) + (6.25 * height) - (5 * age) - 161
		}
		val tdee = bmr * movementLevel.multiplier
		return BmrCalculationResult(bmr = bmr.toInt(), tdee = tdee.toInt())
	}
}
