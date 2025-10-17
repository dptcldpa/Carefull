package com.cases.carefull.domain.usecase.bmr

import com.cases.carefull.domain.model.diet.Bmr
import com.cases.carefull.domain.repository.DietRepository

class SaveBmrUseCase(
	private val dietRepository: DietRepository
) {
	suspend operator fun invoke(bmrToSave: Bmr) {
		dietRepository.insertBmr(bmrToSave)
	}
}