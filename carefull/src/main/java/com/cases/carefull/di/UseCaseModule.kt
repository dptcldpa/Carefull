package com.cases.carefull.di

import com.cases.carefull.domain.repository.MedicineRepository
import com.cases.carefull.domain.usecase.MedicineSearchUseCase
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
}