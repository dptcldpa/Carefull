package com.cases.carefull.di

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.cases.carefull.common.MainViewModel
import com.cases.carefull.domain.repository.DietRepository
import com.cases.carefull.domain.repository.ExerciseRepository
import com.cases.carefull.domain.repository.CalendarRepository
import com.cases.carefull.domain.repository.UserRepository
import com.cases.carefull.domain.repository.RankingRepository
import com.cases.carefull.domain.usecase.MedicineSearchUseCase
import com.cases.carefull.features.carefullcommon.model.NavigationRepository
import com.cases.carefull.features.carefullcontents.diagnosis.medicine.MedicineViewModel
import com.cases.carefull.features.carefullcontents.feed.RankingViewModel
import com.cases.carefull.features.carefullcontents.routine.diet.DietViewModel
import com.cases.carefull.features.carefullcontents.routine.exercise.ExerciseViewModel
import com.cases.carefull.features.carefullmainui.home.HomeViewModel
import com.cases.carefull.features.carefullmainui.screen.auth.OAuthViewModel

class ViewModelFactory(
	private val navigationRepository: NavigationRepository,
	private val medicineSearchUseCase: MedicineSearchUseCase,
	private val dietRepository: DietRepository,
	private val exerciseRepository: ExerciseRepository,
	private val userRepository: UserRepository,
	private val rankingRepository: RankingRepository,
	private val calendarRepository: CalendarRepository,
) : ViewModelProvider.Factory {
	@RequiresApi(Build.VERSION_CODES.O)
	override fun <T : ViewModel> create(modelClass: Class<T>): T {
		if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
			@Suppress("UNCHECKED_CAST")
			return MainViewModel(navigationRepository) as T
		}
		if (modelClass.isAssignableFrom(MedicineViewModel::class.java)) {
			@Suppress("UNCHECKED_CAST")
			return MedicineViewModel(medicineSearchUseCase) as T
		}
		if (modelClass.isAssignableFrom(DietViewModel::class.java)) {
			@Suppress("UNCHECKED_CAST")
			return DietViewModel(dietRepository) as T
		}
		if (modelClass.isAssignableFrom(ExerciseViewModel::class.java)) {
			@Suppress("UNCHECKED_CAST")
			return ExerciseViewModel(exerciseRepository) as T
		}
		if (modelClass.isAssignableFrom(OAuthViewModel::class.java)) {
			@Suppress("UNCHECKED_CAST")
			return OAuthViewModel(userRepository) as T
		}
		if (modelClass.isAssignableFrom(RankingViewModel::class.java)) {
			@Suppress("UNCHECKED_CAST")
			return RankingViewModel(rankingRepository) as T
		}
		if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
			@Suppress("UNCHECKED_CAST")
			return HomeViewModel(calendarRepository,exerciseRepository,dietRepository) as T
		}
		throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
	}
}
