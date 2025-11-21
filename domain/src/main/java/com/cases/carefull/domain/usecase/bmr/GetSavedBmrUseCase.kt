package com.cases.carefull.domain.usecase.bmr

import com.cases.carefull.domain.model.diet.Bmr
import com.cases.carefull.domain.repository.diet.BodyStatsRepository
import kotlinx.coroutines.flow.Flow

class GetSavedBmrUseCase(
	private val bodyStatsRepository: BodyStatsRepository
) {
	operator fun invoke(userId: String): Flow<Bmr?> {
		return bodyStatsRepository.getMyBmr(userId)
	}
}