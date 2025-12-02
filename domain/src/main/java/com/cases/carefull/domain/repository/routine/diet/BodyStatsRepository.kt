package com.cases.carefull.domain.repository.routine.diet

import com.cases.carefull.domain.model.routine.diet.Bmr
import kotlinx.coroutines.flow.Flow

interface BodyStatsRepository {
    fun getBmr(userId: String): Flow<Bmr?>
    suspend fun updateBmr(bmr: Bmr)
}
