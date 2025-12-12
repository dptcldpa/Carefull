package com.cases.carefull.domain.usecase.routine.diet

import com.cases.carefull.domain.model.routine.diet.Bmr
import com.cases.carefull.domain.repository.routine.diet.BodyStatsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSavedBmrUseCase @Inject constructor(
    private val repository: BodyStatsRepository
) {
    operator fun invoke(userId: String): Flow<Bmr?> {
        return repository.getBmr(userId)
    }
}
