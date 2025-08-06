package com.cases.carefull.features.carefullcontents.routine

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cases.carefull.domain.model.DietCollection
import com.cases.carefull.domain.model.DietInfo
import com.cases.carefull.domain.model.MealType
import com.cases.carefull.domain.repository.DataResult
import com.cases.carefull.domain.repository.DietRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DietViewModel(
	private val dietRepository: DietRepository
) : ViewModel() {
	
	private val _uiState = MutableStateFlow(DietUiStateTwo())
	
	val uiState = _uiState.asStateFlow()
	
	init {
		fetchAllMeals()
	}
	
	fun fetchAllMeals() {
		viewModelScope.launch {
			_uiState.update { it.copy(isLoading = true) }
			val result = dietRepository.getAllMealsFromFirestore()
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


//	fun onAddMeal(
//		dietInfo: DietInfo,
//		mealType: MealType = MealType.SNACK,
//		newWeight: Int? = null
//	) {
//		viewModelScope.launch {
//			val updatedDietInfo = newWeight?.let { dietInfo.recalculateFor(it) } ?: dietInfo
//			val newDietInfo = updatedDietInfo.copy(mealType = mealType)
//			dietRepository.addMeal(newDietInfo)
//		}
//	}
	
	fun onAddMealToFirestore(
		dietCollection: DietCollection,
		mealType: MealType,
		updateWeight: Int? = null
	) {
		Log.d("MEAL_TYPE_TEST", "ViewModel: onAddMealToFirestore 호출됨")
		Log.d("MEAL_TYPE_TEST", "전달받은 mealType: $mealType") // 예: BREAKFAST
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
			
			val result = dietRepository.addMealToFirestore(newDietCollection)
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

//	fun onRemoveFood(dietInfo: DietInfo) {
//		viewModelScope.launch {
//			dietRepository.removeMeal(dietInfo)
//		}
//	}
	
	fun onRemoveFoodFromFirestore(dietInfo: DietCollection) {
		viewModelScope.launch {
			dietRepository.removeMealFromFirestore(dietInfo)
		}
	}
	
	fun onSearch(query: String) {
		viewModelScope.launch {
			_uiState.update { it.copy(isLoading = true, searchResults = emptyList()) }
			val result: List<DietCollection> = dietRepository.searchMeals(query = query)
			Log.d("SEARCH_TEST", "Repository로부터 받은 결과 개수: ${result.size}")
			if (result.isNotEmpty()) {
				Log.d("SEARCH_TEST", "첫 번째 결과: ${result.first()}")
			}
			_uiState.update { it.copy(isLoading = false, searchResults = result) }
		}
	}
}
//	fun onSearchTwo(query: String) {
//		viewModelScope.launch {
//			_uiStateTwo.update { it.copy(isLoading = true, searchResults = emptyList()) }
//			val result = dietRepository.searchMealsFromFirestore(query = query)
//			_uiStateTwo.update { it.copy(isLoading = false, searchResults = result) }
//		}
//	}

