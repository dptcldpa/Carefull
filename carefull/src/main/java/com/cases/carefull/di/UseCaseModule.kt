package com.cases.carefull.di

import com.cases.carefull.domain.repository.diagnosis.MedicineRepository
import com.cases.carefull.domain.repository.routine.diet.BodyStatsRepository
import com.cases.carefull.domain.repository.routine.exercise.WorkOutRecordRepository
import com.cases.carefull.domain.usecase.routine.diet.GetSavedBmrUseCase
import com.cases.carefull.domain.usecase.diagnosis.MedicineSearchUseCase
import com.cases.carefull.domain.usecase.routine.diet.BmrUseCases
import com.cases.carefull.domain.usecase.routine.diet.CalculateBmrUseCase
import com.cases.carefull.domain.usecase.routine.diet.SaveBmrUseCase
import com.cases.carefull.domain.usecase.routine.exercise.CalculateWorkOutStatsUseCase
import com.cases.carefull.domain.usecase.routine.exercise.GetWorkOutAnalyzerUseCase
import com.cases.carefull.domain.usecase.routine.exercise.GetWorkOutListUseCase
import com.cases.carefull.domain.usecase.routine.exercise.SaveWorkOutUseCase
import com.cases.carefull.domain.usecase.routine.exercise.WorkOutCounterUseCase
import com.cases.carefull.domain.usecase.routine.exercise.WorkOutRecordUseCases
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
object UseCaseModule {

    @Provides
    @ViewModelScoped
    fun provideMedicineSearchUseCase(
        repository: MedicineRepository
    ): MedicineSearchUseCase {
        return MedicineSearchUseCase(repository)
    }

    @Provides
    @ViewModelScoped
    fun provideBmrUseCases(
        repository: BodyStatsRepository,
    ): BmrUseCases {
        return BmrUseCases(
            getSavedBmr = GetSavedBmrUseCase(repository),
            saveBmr = SaveBmrUseCase(repository),
            calculateBmr = CalculateBmrUseCase(repository)
        )
    }

    @Provides
    @ViewModelScoped
    fun provideWorkOutUseCases(
        repository: WorkOutRecordRepository,
    ): WorkOutRecordUseCases {
        return WorkOutRecordUseCases(
            getWorkOutList = GetWorkOutListUseCase(repository),
            saveWorkOut = SaveWorkOutUseCase(repository),
            calculateStats = CalculateWorkOutStatsUseCase(),
            getAnalyzer = GetWorkOutAnalyzerUseCase(),
            counter = WorkOutCounterUseCase()
        )
    }
}
