package com.cases.carefull.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.cases.carefull.common.MainViewModel
import com.cases.carefull.domain.repository.MedicineRepository
import com.cases.carefull.domain.usecase.MedicineSearchUseCase
import com.cases.carefull.features.carefullcommon.model.NavigationRepository
import com.cases.carefull.features.carefullcontents.diagnosis.medicine.MedicineViewModel

class MainViewModelFactory(
	private val navigationRepository: NavigationRepository,
	private val medicineRepository: MedicineRepository
) : ViewModelProvider.Factory {
	override fun <T : ViewModel> create(modelClass: Class<T>): T {
		if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
			@Suppress("UNCHECKED_CAST")
			return MainViewModel(navigationRepository) as T
		}
		else if (modelClass.isAssignableFrom(MedicineViewModel::class.java)) {
			@Suppress("UNCHECKED_CAST")
			return MedicineViewModel(medicineRepository) as T
		}
		throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
	}
}