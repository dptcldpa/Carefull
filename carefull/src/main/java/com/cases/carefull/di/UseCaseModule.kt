package com.cases.carefull.di

import com.cases.carefull.domain.repository.MedicineRepository
import com.cases.carefull.domain.repository.diet.BodyStatsRepository
import com.cases.carefull.domain.usecase.bmr.CalculateBmrUseCase
import com.cases.carefull.domain.usecase.bmr.GetSavedBmrUseCase
import com.cases.carefull.domain.usecase.MedicineSearchUseCase
import com.cases.carefull.domain.usecase.bmr.SaveBmrUseCase
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
    fun provideCalculateBmrUseCase(): CalculateBmrUseCase {
        return CalculateBmrUseCase()
    }

    @Provides
    @ViewModelScoped
    fun provideGetSavedBmrUseCase(
        repository: BodyStatsRepository
    ): GetSavedBmrUseCase {
        return GetSavedBmrUseCase(repository)
    }

    @Provides
    @ViewModelScoped
    fun provideSaveBmrUseCase(
        repository: BodyStatsRepository
    ): SaveBmrUseCase {
        return SaveBmrUseCase(repository)
    }
}
