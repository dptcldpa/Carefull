package com.cases.carefull.domain.repository.routine.diet

import com.cases.carefull.domain.model.routine.diet.Bmr
import com.cases.carefull.domain.model.routine.diet.BmrCalculationResult
import com.cases.carefull.domain.model.routine.diet.BmrMovementLevel
import com.cases.carefull.domain.model.routine.diet.Gender
import com.cases.carefull.domain.util.DataResourceResult
import kotlinx.coroutines.flow.Flow

interface BodyStatsRepository {
    fun getBmr(userId: String): Flow<DataResourceResult<Bmr?>>
    fun updateBmr(bmr: Bmr): Flow<DataResourceResult<Boolean>>
    fun calculateBmr(
        gender: Gender,
        height: Int,
        weight: Int,
        age: Int,
        movementLevel: BmrMovementLevel
    ): BmrCalculationResult
}
