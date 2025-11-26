package com.cases.carefull.domain.repository.diet

import com.cases.carefull.domain.model.diet.Bmr
import kotlinx.coroutines.flow.Flow

interface BodyStatsRepository {
    fun getBmr(userId: String): Flow<Bmr?>
    suspend fun updateBmr(bmr: Bmr)
}
