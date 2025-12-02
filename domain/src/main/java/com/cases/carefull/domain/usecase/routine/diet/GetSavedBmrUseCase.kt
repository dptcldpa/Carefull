package com.cases.carefull.domain.usecase.routine.diet

import com.cases.carefull.domain.model.routine.diet.Bmr
import com.cases.carefull.domain.repository.routine.diet.BodyStatsRepository
import kotlinx.coroutines.flow.Flow

class GetSavedBmrUseCase(
    private val bodyStatsRepository: BodyStatsRepository
) {
    operator fun invoke(userId: String): Flow<Bmr?> {
        return bodyStatsRepository.getBmr(userId)
    }
}