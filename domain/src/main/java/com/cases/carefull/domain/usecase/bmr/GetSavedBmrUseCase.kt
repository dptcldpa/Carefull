package com.cases.carefull.domain.usecase.bmr

import com.cases.carefull.domain.model.diet.Bmr
import com.cases.carefull.domain.repository.DietRepository
import kotlinx.coroutines.flow.Flow

class GetSavedBmrUseCase(
    private val dietRepository: DietRepository
) {
    suspend operator fun invoke(userId: String): Flow<Bmr?> {
        return dietRepository.getMyBmr(userId)
    }
}