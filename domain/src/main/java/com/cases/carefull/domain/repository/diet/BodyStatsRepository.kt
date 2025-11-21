package com.cases.carefull.domain.repository.diet

import com.cases.carefull.domain.model.diet.Bmr
import kotlinx.coroutines.flow.Flow

interface BodyStatsRepository {
    fun getMyBmr(userId: String): Flow<Bmr?>
    suspend fun updateMyBmr(bmr: Bmr)
}