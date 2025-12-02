package com.cases.carefull.domain.model.routine.diet

data class Bmr(
    val userId: String,
    val gender: Boolean,
    val age: Int,
    val height: Int,
    val weight: Int,
    val movementLevel: BmrMovementLevel = BmrMovementLevel.NONE,
    val bmr: Int,
    val tdee: Int
) {
    companion object {
        fun calculate(
            gender: Gender,
            height: Int,
            weight: Int,
            age: Int,
            movementLevel: BmrMovementLevel
        ): BmrCalculationResult {
            if (height <= 0 || weight <= 0 || age <= 0) {
                return BmrCalculationResult(0, 0)
            }
            val baseBmr = (10 * weight) + (6.25 * height) - (5 * age)
            val calculatedBmr = when (gender) {
                Gender.MALE -> baseBmr + 5
                Gender.FEMALE -> baseBmr - 161
            }
            val tdee = calculatedBmr * movementLevel.multiplier

            return BmrCalculationResult(calculatedBmr.toInt(), tdee.toInt())
        }
    }
}

data class BmrCalculationResult(
    val bmr: Int,
    val tdee: Int
)
