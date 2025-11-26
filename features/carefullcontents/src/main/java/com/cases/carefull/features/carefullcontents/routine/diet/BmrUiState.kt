package com.cases.carefull.features.carefullcontents.routine.diet

import com.cases.carefull.domain.model.diet.Bmr
import com.cases.carefull.domain.model.diet.BmrMovementLevel
import com.cases.carefull.domain.model.diet.Gender

data class BmrUiState(
    val gender: Gender = Gender.MALE,
    val height: String = "",
    val weight: String = "",
    val age: String = "",
    val movementLevel: BmrMovementLevel = BmrMovementLevel.NONE,
    val calculatedBmr: Int = 0,
    val movementLevelMetabolism: Int = 0,
    val savedBmr: Bmr? = null,

    val isLoading: Boolean = true,
    val isError: Boolean = false
) {
    val isBmrChanged: Boolean
        get() {
            if (savedBmr == null) return true

            val isGenderSame = (savedBmr.gender == (gender == Gender.MALE))
            val isHeightSame = (savedBmr.height.toString() == height)
            val isWeightSame = (savedBmr.weight.toString() == weight)
            val isAgeSame = (savedBmr.age.toString() == age)
            val isMovementLevelSame = (savedBmr.movementLevel == movementLevel)

            return !(isGenderSame && isHeightSame && isWeightSame && isAgeSame && isMovementLevelSame)
        }

    fun recalculate(): BmrUiState {
        val heightInt = this.height.toIntOrNull() ?: 0
        val weightInt = this.weight.toIntOrNull() ?: 0
        val ageInt = this.age.toIntOrNull() ?: 0
        val (bmr, tdee) = Bmr.calculate(
            gender = this.gender,
            height = heightInt,
            weight = weightInt,
            age = ageInt,
            movementLevel = this.movementLevel
        )
        return this.copy(
            calculatedBmr = bmr,
            movementLevelMetabolism = tdee
        )
    }
}