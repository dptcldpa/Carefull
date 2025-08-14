package com.cases.carefull.features.carefullcontents.routine.diet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cases.carefull.domain.model.diet.DietCollection
import com.cases.carefull.domain.model.diet.MealType
import com.cases.carefull.domain.repository.DietRepository
import com.cases.carefull.domain.util.DataResult
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
		fetchAllMeals()
	}
	
	fun fetchAllMeals() {
		viewModelScope.launch {
			_uiState.update { it.copy(isLoading = true) }
			val result = dietRepository.getAllMeal()
			when (result) {
				is DataResult.Success -> {
					val meals = result.data
					val mealsByTime = meals.groupBy {
						try {
							MealType.valueOf(it.mealType)
						} catch (e: IllegalArgumentException) {
							MealType.SNACK
						}
					}
					_uiState.update { it ->
						it.copy(
							mealsByTime = mealsByTime,
							totalCalories = meals.sumOf { it.kcal },
							totalCarbs = meals.sumOf { it.carbohydrate },
							totalProteins = meals.sumOf { it.protein },
							totalFats = meals.sumOf { it.fat },
							isLoading = false,
							isError = false
						)
					}
				}
				
				is DataResult.Error -> {
					_uiState.update { it.copy(isLoading = false, isError = true) }
				}
				
				is DataResult.Loading -> {
					_uiState.update { it.copy(isLoading = true) }
				}
			}
		}
	}
	
	fun onAddMeal(
		dietCollection: DietCollection,
		mealType: MealType,
		updateWeight: Int? = null
	) {
		viewModelScope.launch {
			_uiState.update { it.copy(isLoading = true) }
			
			val updatedDietCollection =
				updateWeight?.let { dietCollection.divideWeight(it) } ?: dietCollection
			
			val currentTime = System.currentTimeMillis()
			
			val newDietCollection = updatedDietCollection.copy(
				mealType = mealType.name,
				createdAt = currentTime,
				updatedAt = currentTime
			)
			
			val result = dietRepository.addMeal(newDietCollection)
			when (result) {
				is DataResult.Success -> {
					fetchAllMeals()
				}
				
				is DataResult.Error -> {
					_uiState.update { it.copy(isLoading = false, isError = true) }
				}
				
				is DataResult.Loading -> {
					_uiState.update { it.copy(isLoading = true) }
				}
			}
		}
	}
	
	fun onRemoveMeal(dietInfo: DietCollection) {
		viewModelScope.launch {
			dietRepository.removeMeal(dietInfo)
		}
	}
	
	fun onSearch(query: String) {
		viewModelScope.launch {
			_uiState.update { it.copy(isLoading = true, searchResults = emptyList()) }
			val result: List<DietCollection> = dietRepository.searchMeals(query = query)
			_uiState.update { it.copy(isLoading = false, searchResults = result) }
		}
	}
}