package com.cases.carefull.data.di

import com.cases.carefull.data.repository.CalendarRepositoryImpl
import com.cases.carefull.data.repository.DietRepositoryImpl
import com.cases.carefull.data.repository.ExerciseRepositoryImpl
import com.cases.carefull.data.repository.HospitalRepositoryImpl
import com.cases.carefull.data.repository.MedicineRepositoryImpl
import com.cases.carefull.data.repository.RankingRepositoryImpl
import com.cases.carefull.data.repository.UserRepositoryImpl
import com.cases.carefull.domain.repository.CalendarRepository
import com.cases.carefull.domain.repository.DietRepository
import com.cases.carefull.domain.repository.ExerciseRepository
import com.cases.carefull.domain.repository.HospitalRepository
import com.cases.carefull.domain.repository.MedicineRepository
import com.cases.carefull.domain.repository.RankingRepository
import com.cases.carefull.domain.repository.UserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
	
	@Binds
	@Singleton
	abstract fun bindRankingRepository(
		rankingRepositoryImpl: RankingRepositoryImpl
	): RankingRepository
	
	@Binds
	@Singleton
	abstract fun bindExerciserRepository(
		exerciseRepositoryImpl: ExerciseRepositoryImpl
	): ExerciseRepository
	
	
	@Binds
	@Singleton
	abstract fun bindDietRepository(
		dietRepositoryImpl: DietRepositoryImpl
	): DietRepository
	
	@Binds
	@Singleton
	abstract fun bindCalendarRepository(
		calendarRepositoryImpl: CalendarRepositoryImpl
	): CalendarRepository
	
	@Binds
	@Singleton
	abstract fun bindMedicineRepository(
		medicineRepositoryImpl: MedicineRepositoryImpl
	): MedicineRepository
	
	@Binds
	@Singleton
	abstract fun bindUserRepository(
		userRepositoryImpl: UserRepositoryImpl
	): UserRepository

	@Binds
	@Singleton
	abstract fun bindHospitalRepository(
		hospitalRepositoryImpl: HospitalRepositoryImpl
	): HospitalRepository
}