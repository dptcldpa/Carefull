package com.cases.carefull.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.cases.carefull.common.MainViewModel
import com.cases.carefull.features.carefullcommon.model.NavigationRepository

class MainViewModelFactory(private val repository: NavigationRepository) :
	ViewModelProvider.Factory {
	override fun <T : ViewModel> create(modelClass: Class<T>): T {
		if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
			@Suppress("UNCHECKED_CAST")
			return MainViewModel(repository) as T
		}
		throw IllegalArgumentException("Unknown ViewModel class")
	}
}