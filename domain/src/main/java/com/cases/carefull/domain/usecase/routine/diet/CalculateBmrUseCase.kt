package com.cases.carefull.domain.usecase.routine.diet

import com.cases.carefull.domain.model.routine.diet.Bmr
import com.cases.carefull.domain.model.routine.diet.BmrCalculationResult
import com.cases.carefull.domain.model.routine.diet.BmrMovementLevel
import com.cases.carefull.domain.model.routine.diet.Gender
import com.cases.carefull.domain.repository.routine.diet.BodyStatsRepository

class CalculateBmrUseCase(
    private val repository: BodyStatsRepository
) {
    operator fun invoke(
        gender: Gender,
        height: Int,
        weight: Int,
        age: Int,
        movementLevel: BmrMovementLevel
    ): BmrCalculationResult {
        return repository.calculateBmr(gender, height, weight, age, movementLevel)
    }
}
