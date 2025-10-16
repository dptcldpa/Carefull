package com.cases.carefull.features.carefullcontents.routine.diet

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cases.carefull.domain.model.diet.DietCollection
import com.cases.carefull.domain.model.diet.FavoriteMeal
import com.cases.carefull.domain.model.diet.Gender
import com.cases.carefull.domain.model.diet.MealType
import com.cases.carefull.domain.model.diet.RecentMealSearch
import com.cases.carefull.domain.repository.DietRepository
import com.cases.carefull.domain.usecase.bmr.CalculateBmrUseCase
import com.cases.carefull.domain.usecase.bmr.GetSavedBmrUseCase
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
    private val dietRepository: DietRepository,
    private val getSavedBmrUseCase: GetSavedBmrUseCase,
    private val calculateBmrUseCase: CalculateBmrUseCase,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _uiState = MutableStateFlow(MainDietUiState())
    val uiState = _uiState.asStateFlow()
    private val _navigationEvent = MutableSharedFlow<NavigationEvent>()
    val searchQuery = savedStateHandle.getStateFlow(key = "searchQuery", initialValue = "")
    val navigationEvent = _navigationEvent.asSharedFlow()

    init {
        loadMyBmr()
        observeAllMeals()
        observeFavorites()
        observeRecentSearches()
    }

    fun onFavoriteMealWeightConfirmed(updatedWeight: Int, mealType: MealType, date: LocalDate) {
        val favoriteToAdd = _uiState.value.favoriteState.selectedFavoriteForEditing ?: return

        viewModelScope.launch {
            var dietCollection = DietCollection(
                mealName = favoriteToAdd.name,
                weight = favoriteToAdd.weight,
                kcal = favoriteToAdd.kcal,
                carbohydrate = favoriteToAdd.carbohydrate,
                protein = favoriteToAdd.protein,
                fat = favoriteToAdd.fat
            )
            dietCollection = dietCollection.divideWeight(updatedWeight)
            onAddMeal(
                dietCollection = dietCollection,
                mealType = mealType,
                date = date,
            )
            dismissEditWeightDialog()
            _navigationEvent.emit(NavigationEvent.NavigateBackToDietScreen)
        }
    }

    fun deleteFavoriteMeal(meal: FavoriteMeal) {
        viewModelScope.launch {
            dietRepository.deleteFavorite(meal)
        }
    }

    private fun observeFavorites() {
        viewModelScope.launch {
            dietRepository.getFavorites().collect { favorites ->
                _uiState.update { it.copy(favoriteState = it.favoriteState.copy(favoriteMeals = favorites)) }
            }
        }
    }

    fun dismissEditWeightDialog() {
        _uiState.update {
            it.copy(
                favoriteState =
                    it.favoriteState.copy(selectedFavoriteForEditing = null)
            )
        }
    }

    fun onFavoriteMealClicked(favorite: FavoriteMeal) {
        _uiState.update {
            it.copy(
                favoriteState =
                    it.favoriteState.copy(
                        selectedFavoriteForEditing = favorite,
                        isFavoritesDialogVisible = false
                    )
            )
        }
    }

    fun showFavoritesDialog() {
        _uiState.update {
            it.copy(
                favoriteState =
                    it.favoriteState.copy(isFavoritesDialogVisible = true)
            )
        }
    }

    fun hideFavoritesDialog() {
        _uiState.update {
            it.copy(
                favoriteState =
                    it.favoriteState.copy(isFavoritesDialogVisible = false)
            )
        }
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
                                sections.find { it.date == currentState.dateDietState.selectedDate }
                            val newDateDietState = currentState.dateDietState.copy(
                                allDietSections = sections,
                                allMealLoggedDates = sections.map { it.date }.toSet(),
                                selectedDateSection = sectionForSelectedDate,
                                totalCalories = sectionForSelectedDate?.totalCalories ?: 0,
                                totalCarbs = sectionForSelectedDate?.totalCarbs ?: 0,
                                totalProteins = sectionForSelectedDate?.totalProteins ?: 0,
                                totalFats = sectionForSelectedDate?.totalFats ?: 0
                            )
                            currentState.copy(
                                isLoading = false,
                                isError = false,
                                dateDietState = newDateDietState
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
        date: LocalDate,
        updateWeight: Int? = null
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val updatedDietCollection =
                updateWeight?.let { dietCollection.divideWeight(it) } ?: dietCollection
            val currentTime = LocalTime.now()
            val combinedDateTime = date.atTime(currentTime)
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
                    _uiState.update { it.copy(isLoading = false) }
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
        savedStateHandle["searchQuery"] = query
        if (query.isBlank()) {
            _uiState.update {
                it.copy(
                    dietSearchState = it.dietSearchState.copy(searchResults = emptyList())
                )
            }
        }
    }

    fun emptySearchList() {
        _uiState.update {
            it.copy(
                dietSearchState = it.dietSearchState.copy(searchResults = emptyList())
            )
        }
    }

    private fun loadMyBmr() {
        viewModelScope.launch {
            getSavedBmrUseCase("test").collect { savedBmr ->
                if (savedBmr != null) {
                    val baseBmrState = BmrUiState(
                        savedBmr = savedBmr,
                        gender = if (savedBmr.gender) Gender.MALE else Gender.FEMALE,
                        height = savedBmr.height.toString(),
                        weight = savedBmr.weight.toString(),
                        age = savedBmr.age.toString(),
                        movementLevel = savedBmr.movementLevel
                    )

                    val result = calculateBmrUseCase(
                        gender = baseBmrState.gender,
                        height = savedBmr.height,
                        weight = savedBmr.weight,
                        age = savedBmr.age,
                        movementLevel = baseBmrState.movementLevel
                    )
                    val finalBmrState = baseBmrState.copy(
                        calculatedBmr = result.bmr,
                        movementLevelMetabolism = result.tdee
                    )
                    _uiState.update {
                        it.copy(
                            bmrState = finalBmrState
                        )
                    }
                }
            }
        }
    }

    fun onDateSelected(date: LocalDate) {
        savedStateHandle["selectedDate"] = date
        if (date == _uiState.value.dateDietState.selectedDate) {
            hideDatePicker()
            return
        }

        _uiState.update {
            it.copy(
                isLoading = true,
                dietSearchState = it.dietSearchState.copy(searchResults = emptyList())
            )
        }
        _uiState.update { currentState ->
            val sectionForSelectedDate =
                currentState.dateDietState.allDietSections.find { it.date == date }
            val newDateDietState = currentState.dateDietState.copy(
                selectedDate = date,
                selectedDateSection = sectionForSelectedDate,
                totalCalories = sectionForSelectedDate?.totalCalories ?: 0,
                totalCarbs = sectionForSelectedDate?.totalCarbs ?: 0,
                totalProteins = sectionForSelectedDate?.totalProteins ?: 0,
                totalFats = sectionForSelectedDate?.totalFats ?: 0
            )
            currentState.copy(
                dateDietState = newDateDietState
            )
        }
        hideDatePicker()
    }

    fun showDatePicker() {
        val currentSelectedDate = _uiState.value.dateDietState.selectedDate
        val initialMonth = YearMonth.from(currentSelectedDate)
        _uiState.update {
            it.copy(
                dateDietState = it.dateDietState.copy(
                    isDatePickerVisible = true,
                    datePickerDisplayedMonth = initialMonth,
                    datePickerCalendarDates = getDaysOfMonth(initialMonth)
                )
            )
        }
    }

    fun hideDatePicker() {
        _uiState.update {
            it.copy(
                dateDietState = it.dateDietState.copy(isDatePickerVisible = false)
            )
        }
    }

    fun onDatePickerMonthChanged(monthsToAdd: Long) {
        val currentMonth = _uiState.value.dateDietState.datePickerDisplayedMonth
        val newMonth = currentMonth.plusMonths(monthsToAdd)
        _uiState.update {
            it.copy(
                dateDietState = it.dateDietState.copy(
                    datePickerDisplayedMonth = newMonth,
                    datePickerCalendarDates = getDaysOfMonth(newMonth)
                )
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
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
                _uiState.update {
                    it.copy(
                        dietSearchState = it.dietSearchState.copy(recentSearches = searches)
                    )
                }
            }
        }
    }

    fun onRecentMealSearch() {
        val query = searchQuery.value
        if (query.isBlank()) {
            emptySearchList()
            return
        }
        viewModelScope.launch {
            dietRepository.addSearch(query)
            _uiState.update {
                it.copy(
                    isLoading = true,
                    dietSearchState = it.dietSearchState.copy(searchResults = emptyList())
                )
            }
            val result: List<DietCollection> = dietRepository.searchMeals(query = query)
            _uiState.update {
                it.copy(
                    isLoading = false,
                    dietSearchState = it.dietSearchState.copy(searchResults = result)
                )
            }
        }
    }

    fun onRecentSearchClicked(query: String) {
        savedStateHandle["searchQuery"] = query
        onRecentMealSearch()
    }

    fun onDeleteRecentSearch(search: RecentMealSearch) {
        viewModelScope.launch {
            dietRepository.deleteSearch(search)
        }
    }

    fun onClearAllRecentMealSearches() {
        viewModelScope.launch {
            dietRepository.clearAllSearches()
        }
    }

    fun showCustomInputDialog() {
        _uiState.update {
            it.copy(customInputState = it.customInputState.copy(isDialogVisible = true))
        }
    }

    fun hideCustomInputDialog() {
        _uiState.update {
            it.copy(customInputState = CustomInputState(isDialogVisible = false))
        }
    }

    fun onCustomInputNameChanged(name: String) {
        _uiState.update {
            it.copy(customInputState = it.customInputState.copy(name = name))
        }
    }

    fun onCustomInputWeightChanged(weight: String) {
        if (weight.isDigitsOnly()) {
            _uiState.update {
                it.copy(customInputState = it.customInputState.copy(weight = weight))
            }
        }
    }

    fun onCustomInputCarbsChanged(carbs: String) {
        if (carbs.isDigitsOnly()) {
            _uiState.update {
                it.copy(customInputState = it.customInputState.copy(carbohydrate = carbs))
            }
        }
    }

    fun onCustomInputProteinChanged(protein: String) {
        if (protein.isDigitsOnly()) {
            _uiState.update {
                it.copy(customInputState = it.customInputState.copy(protein = protein))
            }
        }
    }

    fun onCustomInputFatChanged(fat: String) {
        if (fat.isDigitsOnly()) {
            _uiState.update {
                it.copy(customInputState = it.customInputState.copy(fat = fat))
            }
        }
    }

    fun onCustomInputFavoriteChanged(isFavorite: Boolean) {
        _uiState.update {
            it.copy(customInputState = it.customInputState.copy(isFavorite = isFavorite))
        }
    }

    fun calculateCustomInputKcal() {
        val customState = _uiState.value.customInputState
        val carbsGram = customState.carbohydrate.toIntOrNull() ?: 0
        val proteinGram = customState.protein.toIntOrNull() ?: 0
        val fatGram = customState.fat.toIntOrNull() ?: 0
        val totalKcal = (carbsGram * 4) + (proteinGram * 4) + (fatGram * 9)

        _uiState.update {
            it.copy(
                customInputState = it.customInputState.copy(calculatedKcal = totalKcal.toString())
            )
        }
    }

    fun onCustomMealConfirm(date: LocalDate) {
        val customState = _uiState.value.customInputState
        if (!customState.isConfirmEnabled) return

        val newMeal = DietCollection(
            mealName = customState.name,
            weight = customState.weight.toInt(),
            kcal = customState.calculatedKcal.toInt(),
            carbohydrate = customState.carbohydrate.toInt(),
            protein = customState.protein.toInt(),
            fat = customState.fat.toInt()
        )
        viewModelScope.launch {
            onAddMeal(
                dietCollection = newMeal,
                mealType = MealType.SNACK,
                date = date
            )
            if (customState.isFavorite) {
                val favoriteMeal = FavoriteMeal(
                    name = newMeal.mealName,
                    weight = newMeal.weight,
                    kcal = newMeal.kcal,
                    carbohydrate = newMeal.carbohydrate,
                    protein = newMeal.protein,
                    fat = newMeal.fat
                )
                dietRepository.addFavorite(favoriteMeal)
            }
            _navigationEvent.emit(NavigationEvent.NavigateBackToDietScreen)
        }
        hideCustomInputDialog()
    }
}
