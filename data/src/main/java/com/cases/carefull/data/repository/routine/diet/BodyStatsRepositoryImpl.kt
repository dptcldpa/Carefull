package com.cases.carefull.data.repository.routine.diet

import com.cases.carefull.data.dao.BmrDao
import com.cases.carefull.data.mapper.toDomain
import com.cases.carefull.data.mapper.toEntity
import com.cases.carefull.domain.model.routine.diet.Bmr
import com.cases.carefull.domain.model.routine.diet.BmrCalculationResult
import com.cases.carefull.domain.model.routine.diet.BmrMovementLevel
import com.cases.carefull.domain.model.routine.diet.Gender
import com.cases.carefull.domain.repository.routine.diet.BodyStatsRepository
import com.cases.carefull.domain.util.DataResourceResult
import com.cases.carefull.domain.util.toDataResourceResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

class BodyStatsRepositoryImpl @Inject constructor(
    private val bmrDao: BmrDao
) : BodyStatsRepository {

    override fun getBmr(userId: String): Flow<DataResourceResult<Bmr?>> {
        return bmrDao.findBmr(userId)
            .map { entity ->
                val domainModel = entity?.toDomain()
                DataResourceResult.Success(domainModel) as DataResourceResult<Bmr?>
            }
            .onStart { emit(DataResourceResult.Loading) }
            .catch { emit(DataResourceResult.Error(it)) }
            .flowOn(Dispatchers.IO)
    }

    override fun updateBmr(bmr: Bmr): Flow<DataResourceResult<Boolean>> = flow {
        emit(DataResourceResult.Loading)

        bmrDao.insertBmr(bmr.toEntity())
        emit(DataResourceResult.Success(true))
    }
        .catch { emit(DataResourceResult.Error(it)) }
        .flowOn(Dispatchers.IO)

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
