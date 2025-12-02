package com.cases.carefull.features.carefullcontents.routine.diet

import androidx.compose.runtime.Stable
import com.cases.carefull.domain.model.routine.diet.FavoriteFood
import com.cases.carefull.domain.model.routine.diet.FoodItem
import com.cases.carefull.domain.model.routine.diet.RecentFoodSearch
import java.time.LocalDate
import java.time.YearMonth

@Stable
data class MainDietUiState(
    val dietSearchState: DietSearchState = DietSearchState(),
    val bmrState: BmrUiState = BmrUiState(),
    val dateDietState: DateDietState = DateDietState(),
    val favoriteState: FavoriteState = FavoriteState(),
    val customInputState: CustomInputState = CustomInputState(),

    val isLoading: Boolean = true,
    val isError: Boolean = false
)

data class DietSearchState(
    val searchResults: List<FoodItem> = emptyList(),
    val recentSearches: List<RecentFoodSearch> = emptyList()
)

data class DateDietState(
    val dietSections: List<DietDateSection> = emptyList(),
    val allDietSections: List<DietDateSection> = emptyList(),
    val selectedDateSection: DietDateSection? = null,

    val selectedDate: LocalDate = LocalDate.now(),

    val totalCalories: Int = 0,
    val totalCarbs: Int = 0,
    val totalProteins: Int = 0,
    val totalFats: Int = 0,

    val datePickerDisplayedMonth: YearMonth = YearMonth.from(selectedDate),
    val datePickerCalendarDates: List<LocalDate> = emptyList(),
    val allMealLoggedDates: Set<LocalDate> = emptySet(),
    val isDatePickerVisible: Boolean = false,
)

data class FavoriteState(
    val favoriteFoods: List<FavoriteFood> = emptyList(),
    val selectedFavoriteForEditing: FavoriteFood? = null,

    val isFavoritesDialogVisible: Boolean = false,
)

@Stable
data class CustomInputState(
    val name: String = "",
    val servingSize: String = "",
    val carbohydrate: String = "",
    val protein: String = "",
    val fat: String = "",
    val calculatedKcal: String = "0",
    val isFavorite: Boolean = false,
    val isDialogVisible: Boolean = false
)

data class DietDateSection(
    val date: LocalDate,
    val meals: List<FoodItem>,
    val totalCalories: Int,
    val totalCarbs: Int = 0,
    val totalProteins: Int = 0,
    val totalFats: Int = 0
)

sealed class NavigationEvent {
    data object NavigateBackToDietScreen : NavigationEvent()
}