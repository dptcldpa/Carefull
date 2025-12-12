package com.cases.carefull.domain.usecase.routine.diet

data class BmrUseCases(
    val getSavedBmr: GetSavedBmrUseCase,
    val saveBmr: SaveBmrUseCase,
    val calculateBmr: CalculateBmrUseCase
)
