package com.cases.carefull.domain.usecase.bmr

import com.cases.carefull.domain.model.diet.Bmr
import com.cases.carefull.domain.repository.diet.BodyStatsRepository

class SaveBmrUseCase(
	private val bodyStatsRepository: BodyStatsRepository
) {
	suspend operator fun invoke(bmrToSave: Bmr) {
		bodyStatsRepository.updateMyBmr(bmrToSave)
	}
}