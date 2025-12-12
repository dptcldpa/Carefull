package com.cases.carefull.domain.usecase.routine.diet

import com.cases.carefull.domain.model.routine.diet.Bmr
import com.cases.carefull.domain.repository.routine.diet.BodyStatsRepository

class SaveBmrUseCase(
    private val repository: BodyStatsRepository
) {
    suspend operator fun invoke(bmrToSave: Bmr) {
        repository.updateBmr(bmrToSave)
    }
}
