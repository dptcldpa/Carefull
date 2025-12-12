package com.cases.carefull.domain.usecase.routine.exercise

data class WorkOutRecordUseCases(
    val getWorkOutList: GetWorkOutListUseCase,
    val saveWorkOut: SaveWorkOutUseCase,

    val calculateStats: CalculateWorkOutStatsUseCase,
    val getAnalyzer: GetWorkOutAnalyzerUseCase,
    val counter: WorkOutCounterUseCase
)
