package com.cases.carefull.features.carefullcontents.routine.diet

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.cases.carefull.domain.model.routine.diet.FavoriteFood
import com.cases.carefull.domain.model.routine.diet.FoodDataInputType
import com.cases.carefull.domain.model.routine.diet.FoodItem
import com.cases.carefull.domain.model.routine.diet.RecentFoodSearch
import com.cases.carefull.domain.repository.routine.diet.DietRecordRepository
import com.cases.carefull.domain.repository.routine.diet.FavoriteFoodRepository
import com.cases.carefull.domain.repository.routine.diet.FoodSearchRepository
import com.cases.carefull.domain.repository.routine.diet.RecentMealSearchRepository
import com.cases.carefull.domain.usecase.routine.diet.GetSavedBmrUseCase
import com.cases.carefull.domain.util.DataResourceResult
import com.cases.carefull.domain.util.toSafeInt
import com.cases.carefull.features.carefullcontents.util.FoodPagingSource
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.temporal.TemporalAdjusters

@HiltViewModel
class DietViewModel @Inject constructor(
    private val dietRecordRepository: DietRecordRepository,
    private val recentMealSearchRepository: RecentMealSearchRepository,
    private val favoriteFoodRepository: FavoriteFoodRepository,
    private val foodSearchRepository: FoodSearchRepository,
    private val getSavedBmrUseCase: GetSavedBmrUseCase,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _uiState = MutableStateFlow(MainDietUiState())
    val uiState = _uiState.asStateFlow()
    private val _navigationEvent = MutableSharedFlow<NavigationEvent>()
    val searchQuery = savedStateHandle.getStateFlow(key = "searchQuery", initialValue = "")
    val navigationEvent = _navigationEvent.asSharedFlow()
    private val userId = "test" //카카오 연동시 수정예정

    @OptIn(ExperimentalCoroutinesApi::class)
    val pagedSearchResults: Flow<PagingData<FoodItem>> = searchQuery
        .flatMapLatest { query ->
            if (query.isBlank()) {
                emptyFlow()
            } else {
                Pager(
                    config = PagingConfig(
                        pageSize = FoodPagingSource.PAGE_SIZE,
                        enablePlaceholders = false,
                        initialLoadSize = FoodPagingSource.PAGE_SIZE
                    ),
                    pagingSourceFactory = {
                        FoodPagingSource(foodSearchRepository, query)
                    }
                ).flow
            }
        }.cachedIn(viewModelScope)

    init {
        observeMealsForDate(LocalDate.now())
        observeSavedBmr()
        observeFavoriteFoods()
        observeRecentSearches()
        savedStateHandle.get<LocalDate>("selectedDate") ?: LocalDate.now()
    }

    fun onSearchFoods(query: String) {
        if (query.isBlank()) {
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, isError = false) }

            launch {
                runCatching {
                    recentMealSearchRepository.saveRecentSearch(query)
                }
            }
        }
    }

    private fun observeRecentSearches() {
        viewModelScope.launch {
            recentMealSearchRepository.getRecentSearches().collectLatest { result ->
                when (result) {
                    is DataResourceResult.Loading -> {
                        _uiState.update { it.copy(isLoading = true) }
                    }

                    is DataResourceResult.Success -> {
                        _uiState.update {
                            it.copy(
                                dietSearchState = it.dietSearchState.copy(
                                    recentSearches =
                                        result.data
                                )
                            )
                        }
                    }

                    else -> {
                        _uiState.update { it.copy(isLoading = false) }
                    }
                }
            }
        }
    }

    fun onDeleteRecentSearch(search: RecentFoodSearch) {
        viewModelScope.launch {
            launch {
                runCatching {

                    recentMealSearchRepository.deleteRecentSearch(search)
                }
            }
        }
    }

    fun onClearAllRecentMealSearches() {
        viewModelScope.launch {
            launch {
                runCatching {

                    recentMealSearchRepository.clearAllRecentSearches()
                }
            }
        }
    }

    private fun observeFavoriteFoods() {
        viewModelScope.launch {
            favoriteFoodRepository.getAllFavoriteFoods().collectLatest { result ->
                when (result) {
                    is DataResourceResult.Loading -> {
                        _uiState.update { it.copy(isLoading = true) }
                    }

                    is DataResourceResult.Success -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                favoriteState = it.favoriteState.copy(
                                    favoriteFoods = result.data
                                )
                            )
                        }
                    }

                    else -> {
                        _uiState.update { it.copy(isLoading = false) }
                    }
                }
            }
        }
    }

    fun onDeleteFavoriteFood(favoriteFood: FavoriteFood) {
        viewModelScope.launch {
            launch {
                runCatching {

                    favoriteFoodRepository.deleteFavoriteFood(favoriteFood)
                }
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        savedStateHandle["searchQuery"] = query
        if (query.isBlank()) {
            _uiState.update {
                it.copy(
                    dietSearchState = it.dietSearchState.copy(
                        searchResults = emptyList()
                    )
                )
            }
        }
    }

    fun onAddFood(
        foodItem: FoodItem,
        mealType: String,
        date: LocalDate,
        updateWeight: Int
    ) {
        val updatedFoodItem = foodItem.adjustPortion(updateWeight)
        val currentUser = userId

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val result =
                dietRecordRepository.addMeal(updatedFoodItem, currentUser, mealType, date)
            when (result) {
                is DataResourceResult.Success -> {
                    onClearSearchQuery()
                    _uiState.update {
                        it.copy(
                            isLoading = false
                        )
                    }
                    _navigationEvent.emit(NavigationEvent.NavigateBackToDietScreen)
                }

                is DataResourceResult.Error -> {
                    _uiState.update { it.copy(isLoading = false, isError = true) }
                }

                is DataResourceResult.Loading -> {
                    _uiState.update { it.copy(isLoading = false) }
                }
            }
        }
    }

    fun onRemoveMeal(dietInfo: FoodItem) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, isError = false) }
            val result = dietRecordRepository.removeMeal(dietInfo.documentId)
            when (result) {
                is DataResourceResult.Success -> {
                    _uiState.update { it.copy(isLoading = false, isError = false) }
                }

                is DataResourceResult.Error -> {
                    _uiState.update { it.copy(isLoading = false, isError = true) }
                }

                is DataResourceResult.Loading -> {
                    _uiState.update { it.copy(isLoading = true, isError = false) }
                }
            }
        }
    }

    fun onClearSearchQuery() {
        _uiState.update {
            it.copy(
                dietSearchState = it.dietSearchState.copy(
                    searchResults = emptyList()
                )
            )
        }
    }

    fun onCustomInputChanged(type: FoodDataInputType, value: String) {
        val isValidInput = when (type) {
            FoodDataInputType.NAME -> true
            else -> value.isEmpty() || value.all { it.isDigit() }
        }

        if (isValidInput) {
            _uiState.update { state ->
                val currentInputState = state.customInputState
                val newInputState = when (type) {
                    FoodDataInputType.NAME -> currentInputState.copy(name = value)
                    FoodDataInputType.SERVING_SIZE -> currentInputState.copy(servingSize = value)
                    FoodDataInputType.CARBOHYDRATE -> currentInputState.copy(carbohydrate = value)
                    FoodDataInputType.PROTEIN -> currentInputState.copy(protein = value)
                    FoodDataInputType.FAT -> currentInputState.copy(fat = value)
                }
                state.copy(customInputState = newInputState)
            }
        }
    }

    fun onAddCustomFood(date: LocalDate, mealType: String) {
        val input = _uiState.value.customInputState

        if (input.name.isBlank() || input.servingSize.toSafeInt() <= 0) {
            return
        }

        val newFood = FoodItem(
            name = input.name,
            servingSize = input.servingSize.toSafeInt(),
            kcal = input.calculatedKcal.toSafeInt(),
            carbohydrate = input.carbohydrate.toSafeInt(),
            protein = input.protein.toSafeInt(),
            fat = input.fat.toSafeInt(),
        )

        viewModelScope.launch {
            onAddFood(newFood, mealType, date, newFood.servingSize)
            if (input.isFavorite) {
                val favoriteFood = FavoriteFood(
                    name = newFood.name,
                    servingSize = newFood.servingSize,
                    kcal = newFood.kcal,
                    carbohydrate = newFood.carbohydrate,
                    protein = newFood.protein,
                    fat = newFood.fat
                )
                launch {
                    runCatching {
                        favoriteFoodRepository.saveFavoriteFood(favoriteFood)
                    }
                }
            }
            hideCustomInputDialog()
            _navigationEvent.emit(NavigationEvent.NavigateBackToDietScreen)
        }
    }

    fun onCustomInputFavoriteChanged(isFavorite: Boolean) {
        _uiState.update {
            it.copy(customInputState = it.customInputState.copy(isFavorite = isFavorite))
        }
    }

    fun onAddFavoriteFood(updatedWeight: Int, mealType: String, date: LocalDate) {
        val favoriteToAdd = _uiState.value.favoriteState.selectedFavoriteForEditing ?: return

        viewModelScope.launch {
            var foodItem = FoodItem(
                name = favoriteToAdd.name,
                servingSize = favoriteToAdd.servingSize,
                kcal = favoriteToAdd.kcal,
                carbohydrate = favoriteToAdd.carbohydrate,
                protein = favoriteToAdd.protein,
                fat = favoriteToAdd.fat
            )
            foodItem = foodItem.adjustPortion(updatedWeight)
            onAddFood(
                foodItem = foodItem,
                mealType = mealType,
                date = date,
                updateWeight = foodItem.servingSize
            )
            hideEditServingSizeDialog()
            _navigationEvent.emit(NavigationEvent.NavigateBackToDietScreen)
        }
    }

    fun hideEditServingSizeDialog() {
        _uiState.update {
            it.copy(
                favoriteState =
                    it.favoriteState.copy(selectedFavoriteForEditing = null)
            )
        }
    }

    fun onFavoriteFoodSelected(favorite: FavoriteFood) {
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

    private fun observeSavedBmr() {
        viewModelScope.launch {
            getSavedBmrUseCase("test").collect { result ->
                when (result) {
                    is DataResourceResult.Loading -> {
                        _uiState.update { it.copy(isLoading = true) }
                    }

                    is DataResourceResult.Success -> {
                        val baseBmrState = BmrUiState(
                            movementLevelMetabolism = result.data?.tdee ?: 0
                        )
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                bmrState = baseBmrState

                            )
                        }
                    }

                    else -> {
                        _uiState.update { it.copy(isLoading = false) }
                    }
                }
            }
        }
    }

    fun observeMealsForDate(date: LocalDate) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            dietRecordRepository.getMealByDate(date, userId).collect { result ->
                when (result) {
                    is DataResourceResult.Success -> {
                        val meals = result.data
                        val currentSection = DietDateSection(
                            date = date,
                            meals = meals,
                            totalCalories = meals.sumOf { it.kcal },
                            totalCarbs = meals.sumOf { it.carbohydrate },
                            totalProteins = meals.sumOf { it.protein },
                            totalFats = meals.sumOf { it.fat }
                        )
                        _uiState.update { currentState ->
                            currentState.copy(
                                isLoading = false,
                                isError = false,
                                dateDietState = currentState.dateDietState.copy(
                                    selectedDate = date,
                                    selectedDateSection = currentSection,
                                    totalCalories = currentSection.totalCalories,
                                    totalCarbs = currentSection.totalCarbs,
                                    totalProteins = currentSection.totalProteins,
                                    totalFats = currentSection.totalFats
                                )
                            )
                        }
                    }

                    is DataResourceResult.Error -> {
                        println("DEBUG_DIET Error: ${result.exception.message}")
                        _uiState.update { it.copy(isLoading = false, isError = true) }
                    }

                    is DataResourceResult.Loading -> {}
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
        observeMealsForDate(date)
        _uiState.update { currentState ->
            currentState.copy(
                dietSearchState = currentState.dietSearchState.copy(
                    searchResults = emptyList()
                ),
                dateDietState = currentState.dateDietState.copy(
                    selectedDate = date,
                    isDatePickerVisible = false
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

    fun calculateCustomInputKcal() {
        val customState = _uiState.value.customInputState
        val carbs = customState.carbohydrate.toIntOrNull() ?: 0
        val protein = customState.protein.toIntOrNull() ?: 0
        val fat = customState.fat.toIntOrNull() ?: 0
        val totalKcal = FoodItem.calculateCalories(carbs, protein, fat)

        _uiState.update {
            it.copy(
                customInputState = it.customInputState.copy(calculatedKcal = totalKcal.toString())
            )
        }
    }
}
