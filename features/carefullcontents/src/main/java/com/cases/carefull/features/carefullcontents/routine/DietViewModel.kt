package com.cases.carefull.features.carefullcontents.routine

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cases.carefull.domain.model.DietInfo
import com.cases.carefull.domain.model.MealType
import com.cases.carefull.domain.repository.DietRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DietViewModel(
	private val dietRepository: DietRepository
) : ViewModel() {
	
	private val _uiState = MutableStateFlow(DietUiState())
	val uiState = _uiState.asStateFlow()
	
	init {
		viewModelScope.launch {
			dietRepository.getAllMeals().collect { meals ->
				_uiState.value = DietUiState(
					mealsByTime = meals.groupBy { it.mealType },
					totalCalories = meals.sumOf { it.calories ?: 0 },
					totalCarbs = meals.sumOf { it.carbs ?: 0 },
					totalProteins = meals.sumOf { it.proteins ?: 0 },
					totalFats = meals.sumOf { it.fats ?: 0 },
					isLoading = false
				)
			}
		}
	}
	
	fun onAddMeal(dietInfo: DietInfo, mealType: MealType = MealType.SNACK, newWeight: Int? = null) {
		viewModelScope.launch {
			val updatedDietInfo = newWeight?.let { dietInfo.recalculateFor(it) } ?: dietInfo
			val newDietInfo = updatedDietInfo.copy(mealType = mealType)
			dietRepository.addMeal(newDietInfo)
		}
	}
	
	fun onRemoveFood(dietInfo: DietInfo) {
		viewModelScope.launch {
			dietRepository.removeMeal(dietInfo)
		}
	}
	
	fun onSearch(query: String) {
		viewModelScope.launch {
			_uiState.update { it.copy(isLoading = true, searchResults = emptyList()) }
			val result = dietRepository.searchMeals(query = query)
			_uiState.update { it.copy(isLoading = false, searchResults = result) }
		}
	}
}