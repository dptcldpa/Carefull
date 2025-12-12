package com.cases.carefull.data.repository.routine.diet

import com.cases.carefull.data.dao.BmrDao
import com.cases.carefull.data.mapper.toDomain
import com.cases.carefull.data.mapper.toEntity
import com.cases.carefull.domain.model.routine.diet.Bmr
import com.cases.carefull.domain.model.routine.diet.BmrCalculationResult
import com.cases.carefull.domain.model.routine.diet.BmrMovementLevel
import com.cases.carefull.domain.model.routine.diet.Gender
import com.cases.carefull.domain.repository.routine.diet.BodyStatsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class BodyStatsRepositoryImpl @Inject constructor(
    private val bmrDao: BmrDao
) : BodyStatsRepository {

    override fun getBmr(userId: String): Flow<Bmr?> {
        return bmrDao.findBmr(userId)
            .map { it?.toDomain() }
    }

    override suspend fun updateBmr(bmr: Bmr) {
        val bmrEntity = bmr.toEntity()
        bmrDao.insertBmr(bmrEntity)
    }

    override fun calculateBmr(
        gender: Gender,
        height: Int,
        weight: Int,
        age: Int,
        movementLevel: BmrMovementLevel
    ): BmrCalculationResult {
        return Bmr.calculate(gender, height, weight, age, movementLevel)
    }
}
