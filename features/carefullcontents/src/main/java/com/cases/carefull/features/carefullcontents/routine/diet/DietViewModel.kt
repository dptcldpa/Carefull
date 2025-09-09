package com.cases.carefull.features.carefullcontents.routine.diet

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cases.carefull.domain.model.diet.Bmr
import com.cases.carefull.domain.model.diet.BmrActivity
import com.cases.carefull.domain.model.diet.DietCollection
import com.cases.carefull.domain.model.diet.FavoriteMeal
import com.cases.carefull.domain.model.diet.Gender
import com.cases.carefull.domain.model.diet.MealType
import com.cases.carefull.domain.model.diet.RecentMealSearch
import com.cases.carefull.domain.repository.DietRepository
import com.cases.carefull.domain.util.DataResourceResult
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.YearMonth
import java.time.ZoneId
import java.time.temporal.TemporalAdjusters

@RequiresApi(Build.VERSION_CODES.O)
@HiltViewModel
class DietViewModel @Inject constructor(
	private val dietRepository: DietRepository
) : ViewModel() {
	private val _uiState = MutableStateFlow(DietUiState())
	val uiState = _uiState.asStateFlow()
	private val _navigationEvent = MutableSharedFlow<NavigationEvent>()
	val navigationEvent = _navigationEvent.asSharedFlow()
	
	init {
		loadMyBmr()
		observeAllMeals()
		observeFavorites()
		observeRecentSearches()
	}
	
	private fun observeFavorites() {
		viewModelScope.launch {
			dietRepository.getFavorites().collect { favorites ->
				_uiState.update { it.copy(favoriteMeals = favorites) }
			}
		}
	}
	
	fun onCustomMealConfirmed(meal: DietCollection, isFavorite: Boolean) {
		onAddMeal(meal, mealType = MealType.SNACK)
		
		if (isFavorite) {
			viewModelScope.launch {
				val favoriteMeal = FavoriteMeal(
					name = meal.mealName,
					weight = meal.weight,
					kcal = meal.kcal,
					carbohydrate = meal.carbohydrate,
					protein = meal.protein,
					fat = meal.fat
				)
				dietRepository.addFavorite(favoriteMeal)
			}
		}
		
		viewModelScope.launch {
			_navigationEvent.emit(NavigationEvent.NavigateBackToDietScreen)
		}
	}
	
	fun onFavoriteMealClicked(favorite: FavoriteMeal) {
		_uiState.update {
			it.copy(
				selectedFavoriteForEditing = favorite,
				isFavoritesDialogVisible = false
			)
		}
	}
	
	fun onFavoriteMealWeightConfirmed(updatedWeight: Int, mealType: MealType) {
		val favoriteToadd = _uiState.value.selectedFavoriteForEditing ?: return
		var dietCollection = DietCollection(
			mealName = favoriteToadd.name,
			weight = favoriteToadd.weight,
			kcal = favoriteToadd.kcal,
			carbohydrate = favoriteToadd.carbohydrate,
			protein = favoriteToadd.protein,
			fat = favoriteToadd.fat
		)
		dietCollection = dietCollection.divideWeight(updatedWeight)
		onAddMeal(dietCollection, mealType)
		dismissEditWeightDialog()
		
		viewModelScope.launch {
			_navigationEvent.emit(NavigationEvent.NavigateBackToDietScreen)
		}
	}
	
	fun dismissEditWeightDialog() {
		_uiState.update { it.copy(selectedFavoriteForEditing = null) }
	}
	
	fun deleteFavoriteMeal(meal: FavoriteMeal) {
		viewModelScope.launch {
			dietRepository.deleteFavorite(meal)
		}
	}
	
	fun showFavoritesDialog() {
		_uiState.update { it.copy(isFavoritesDialogVisible = true) }
	}
	
	fun hideFavoritesDialog() {
		_uiState.update { it.copy(isFavoritesDialogVisible = false) }
	}
	
	@RequiresApi(Build.VERSION_CODES.O)
	fun observeAllMeals() {
		viewModelScope.launch {
			_uiState.update { it.copy(isLoading = true) }
			dietRepository.getAllMeal().collect { result ->
				when (result) {
					is DataResourceResult.Success -> {
						val meals = result.data
						val sections = meals.entries
							.sortedByDescending { it.key }
							.map { (date, meals) ->
								DietDateSection(
									date = date,
									meals = meals,
									totalCalories = meals.sumOf { it.kcal },
									totalCarbs = meals.sumOf { it.carbohydrate },
									totalProteins = meals.sumOf { it.protein },
									totalFats = meals.sumOf { it.fat }
								)
							}
						_uiState.update { currentState ->
							val sectionForSelectedDate =
								sections.find { it.date == currentState.selectedDate }
							
							currentState.copy(
								isLoading = false,
								isError = false,
								allDietSections = sections,
								allMealLoggedDates = sections.map { it.date }.toSet(),
								
								selectedDateSection = sectionForSelectedDate,
								
								totalCalories = sectionForSelectedDate?.totalCalories ?: 0,
								totalCarbs = sectionForSelectedDate?.totalCarbs ?: 0,
								totalProteins = sectionForSelectedDate?.totalProteins ?: 0,
								totalFats = sectionForSelectedDate?.totalFats ?: 0
							)
						}
					}
					
					is DataResourceResult.Error -> {
						_uiState.update { it.copy(isLoading = false, isError = true) }
					}
					
					is DataResourceResult.Loading -> {
					}
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
			val selectedDate = _uiState.value.selectedDate
			val currentTime = LocalTime.now()
			val combinedDateTime = selectedDate.atTime(currentTime)
			val createdAtTimestamp = combinedDateTime.atZone(ZoneId.systemDefault())
				.toInstant()
				.toEpochMilli()
			val updatedAtTimestamp = System.currentTimeMillis()
			val newDietCollection = updatedDietCollection.copy(
				mealType = mealType.name,
				createdAt = createdAtTimestamp,
				updatedAt = updatedAtTimestamp
			)
			val result = dietRepository.addMeal(newDietCollection)
			when (result) {
				is DataResourceResult.Success -> {
					_uiState.update { it.copy(isLoading = false, searchQuery = "") }
					emptySearchList()
					observeAllMeals()
				}
				
				is DataResourceResult.Error -> {
					_uiState.update { it.copy(isLoading = false, isError = true) }
				}
				
				is DataResourceResult.Loading -> {
				}
			}
		}
	}
	
	fun onRemoveMeal(dietInfo: DietCollection) {
		viewModelScope.launch {
			val result = dietRepository.removeMeal(dietInfo.documentId)
			when (result) {
				is DataResourceResult.Success -> {
					observeAllMeals()
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
	
	fun onSearchQueryChanged(query: String) {
		_uiState.update { it.copy(searchQuery = query) }
		
		if (query.isBlank()) {
			_uiState.update { it.copy(searchResults = emptyList()) }
		}
	}
	
	fun emptySearchList() {
		_uiState.update { it.copy(searchResults = emptyList()) }
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
	
	fun onDateSelected(date: LocalDate) {
		if (date == _uiState.value.selectedDate) {
			hideDatePicker()
			return
		}
		_uiState.update { currentState ->
			val sectionForSelectedDate = currentState.allDietSections.find { it.date == date }
			currentState.copy(
				selectedDate = date,
				selectedDateSection = sectionForSelectedDate,
				totalCalories = sectionForSelectedDate?.totalCalories ?: 0,
				totalCarbs = sectionForSelectedDate?.totalCarbs ?: 0,
				totalProteins = sectionForSelectedDate?.totalProteins ?: 0,
				totalFats = sectionForSelectedDate?.totalFats ?: 0
			)
		}
		hideDatePicker()
	}
	
	fun showDatePicker() {
		val currentSelectedDate = _uiState.value.selectedDate
		val initialMonth = YearMonth.from(currentSelectedDate)
		_uiState.update {
			it.copy(
				isDatePickerVisible = true,
				datePickerDisplayedMonth = initialMonth,
				datePickerCalendarDates = getDaysOfMonth(initialMonth)
			)
		}
	}
	
	fun hideDatePicker() {
		_uiState.update { it.copy(isDatePickerVisible = false) }
	}
	
	fun onDatePickerMonthChanged(monthsToAdd: Long) {
		val currentMonth = _uiState.value.datePickerDisplayedMonth
		val newMonth = currentMonth.plusMonths(monthsToAdd)
		_uiState.update {
			it.copy(
				datePickerDisplayedMonth = newMonth,
				datePickerCalendarDates = getDaysOfMonth(newMonth)
			)
		}
	}
	
	private fun getDaysOfMonth(yearMonth: YearMonth): List<LocalDate> {
		val firstDayOfMonth = yearMonth.atDay(1)
		val firstDayOfFirstWeek =
			firstDayOfMonth.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY))
		
		val lastDayOfMonth = yearMonth.atEndOfMonth()
		val lastDayOfLastWeek =
			lastDayOfMonth.with(TemporalAdjusters.nextOrSame(DayOfWeek.SATURDAY))
		
		val days = mutableListOf<LocalDate>()
		var currentDate = firstDayOfFirstWeek
		while (!currentDate.isAfter(lastDayOfLastWeek)) {
			days.add(currentDate)
			currentDate = currentDate.plusDays(1)
		}
		return days
	}
	
	fun onGoToToday() {
		onDateSelected(LocalDate.now())
	}
	
	private fun observeRecentSearches() {
		viewModelScope.launch {
			dietRepository.getRecentSearches().collect { searches ->
				_uiState.update { it.copy(recentSearches = searches) }
			}
		}
	}
	
	fun onRecentMealSearch() {
		val query = _uiState.value.searchQuery
		if (query.isBlank()) {
			emptySearchList()
			return
		}
		
		viewModelScope.launch {
			dietRepository.addSearch(query)
		}
		
		viewModelScope.launch {
			_uiState.update { it.copy(isLoading = true, searchResults = emptyList()) }
			val result: List<DietCollection> = dietRepository.searchMeals(query = query)
			_uiState.update { it.copy(isLoading = false, searchResults = result) }
		}
	}
	
	fun onRecentSearchClicked(query: String) {
		_uiState.update { it.copy(searchQuery = query) }
		onRecentMealSearch()
	}
	
	fun onDeleteRecentSearch(search: RecentMealSearch) {
		viewModelScope.launch {
			dietRepository.deleteSearch(search)
		}
	}
	fun onClearAllRecentMealSearches(){
		viewModelScope.launch {
			dietRepository.clearAllSearches()
		}
	}
}
