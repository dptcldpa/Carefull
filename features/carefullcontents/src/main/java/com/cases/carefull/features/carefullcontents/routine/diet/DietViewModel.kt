package com.cases.carefull.features.carefullcontents.routine.diet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cases.carefull.domain.model.diet.Bmr
import com.cases.carefull.domain.model.diet.BmrActivity
import com.cases.carefull.domain.model.diet.DietCollection
import com.cases.carefull.domain.model.diet.Gender
import com.cases.carefull.domain.model.diet.MealType
import com.cases.carefull.domain.repository.DietRepository
import com.cases.carefull.domain.util.DataResourceResult
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
		loadMyBmr()
	}
	
	fun fetchAllMeals() {
		viewModelScope.launch {
			_uiState.update { it.copy(isLoading = true) }
			val result = dietRepository.getAllMeal()
			when (result) {
				is DataResourceResult.Success -> {
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
				
				is DataResourceResult.Error -> {
					_uiState.update { it.copy(isLoading = false, isError = true) }
				}
				
				is DataResourceResult.Loading -> {
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
				is DataResourceResult.Success -> {
					fetchAllMeals()
				}
				
				is DataResourceResult.Error -> {
					_uiState.update { it.copy(isLoading = false, isError = true) }
				}
				
				is DataResourceResult.Loading -> {
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
	
	fun getMyBmr() {
		viewModelScope.launch {
		}
	}
	
	fun onGenderSelected(gender: Gender) {
		_uiState.update { currentState ->
			val newBmrState = currentState.bmrState.copy(gender = gender)
			currentState.copy(bmrState = newBmrState)
		}
		calculateMetabolism()
	}
	
	fun onHeightChanged(height: String) {
		if (height.all { it.isDigit() }) {
			_uiState.update { currentState ->
				val newBmrState = currentState.bmrState.copy(height = height)
				currentState.copy(bmrState = newBmrState)
			}
			calculateMetabolism()
		}
	}
	
	fun onWeightChanged(weight: String) {
		if (weight.all { it.isDigit() }) {
			_uiState.update { currentState ->
				val newBmrState = currentState.bmrState.copy(weight = weight)
				currentState.copy(bmrState = newBmrState)
			}
			calculateMetabolism()
		}
	}
	
	fun onAgeChanged(age: String) {
		if (age.all { it.isDigit() }) {
			_uiState.update { currentState ->
				val newBmrState = currentState.bmrState.copy(age = age)
				currentState.copy(bmrState = newBmrState)
			}
			calculateMetabolism()
		}
	}
	
	fun onActivitySelected(activity: BmrActivity) {
		_uiState.update { currentState ->
			val newBmrState = currentState.bmrState.copy(activity = activity)
			currentState.copy(bmrState = newBmrState)
		}
		calculateMetabolism()
	}
	
	//미펜-세인트 조르 공식 (Mifflin-St Jeor Equation)
	fun calculateMetabolism() {
		val currentState = _uiState.value.bmrState
		val height = currentState.height.toIntOrNull() ?: 0
		val weight = currentState.weight.toIntOrNull() ?: 0
		val age = currentState.age.toIntOrNull() ?: 0
		// 1. 기초대사량 계산
		val bmrResult = if (height > 0 && weight > 0 && age > 0) {
			when (currentState.gender) {
				Gender.MALE -> (10 * weight) + (6.25 * height) - (5 * age) + 5
				Gender.FEMALE -> (10 * weight) + (6.25 * height) - (5 * age) - 161
			}
		} else {
			0.0
		}
		// 2. 활동대사량 계산
		val tdeeResult = bmrResult * currentState.activity.multiplier
		_uiState.update { currentState ->
			val updatedBmrState = currentState.bmrState.copy(
				calculatedBmr = bmrResult.toInt(),
				activityMetabolism = tdeeResult.toInt()
			)
			currentState.copy(bmrState = updatedBmrState)
		}
	}
	
	private fun loadMyBmr() {
		viewModelScope.launch {
			dietRepository.getMyBmr("test").collect { savedBmr ->
				if (savedBmr != null) {
					
					_uiState.update {
						it.copy(
							savedBmr = savedBmr,
							bmrState = BmrUiState(
								gender = if (savedBmr.gender) Gender.MALE else Gender.FEMALE,
								height = savedBmr.height.toString(),
								weight = savedBmr.weight.toString(),
								age = savedBmr.age.toString(),
								activity = savedBmr.activity
							)
						)
					}
					calculateMetabolism()
				}
			}
		}
	}
	
	fun onSaveClicked() {
		val currentState = _uiState.value.bmrState
		val heightInt = currentState.height.toIntOrNull()
		val weightInt = currentState.weight.toIntOrNull()
		val ageInt = currentState.age.toIntOrNull()
		
		if (heightInt == null || weightInt == null || ageInt == null ||
			heightInt <= 0 || weightInt <= 0 || ageInt <= 0
		) {
			return
		}
		viewModelScope.launch {
			_uiState.update { it.copy(isLoading = true) }
			val bmrToSave = Bmr(
				userId = "test",
				gender = (currentState.gender == Gender.MALE),
				age = ageInt,
				height = heightInt,
				weight = weightInt,
				activity = currentState.activity,
				bmr = currentState.calculatedBmr,
				activityBmr = currentState.activityMetabolism
			)
			dietRepository.insertBmr(bmrToSave)
			_uiState.update { it.copy(isLoading = false) }
		}
	}
}