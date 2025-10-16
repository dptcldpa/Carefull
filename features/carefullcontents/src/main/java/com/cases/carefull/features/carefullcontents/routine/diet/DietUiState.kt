package com.cases.carefull.features.carefullcontents.routine.diet

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Stable
import androidx.core.text.isDigitsOnly
import com.cases.carefull.domain.model.diet.DietCollection
import com.cases.carefull.domain.model.diet.FavoriteMeal
import com.cases.carefull.domain.model.diet.RecentMealSearch
import java.time.LocalDate
import java.time.YearMonth

@RequiresApi(Build.VERSION_CODES.O)
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
    val searchResults: List<DietCollection> = emptyList(),
    val recentSearches: List<RecentMealSearch> = emptyList()
)

@RequiresApi(Build.VERSION_CODES.O)
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
    val favoriteMeals: List<FavoriteMeal> = emptyList(),
    val selectedFavoriteForEditing: FavoriteMeal? = null,

    val isFavoritesDialogVisible: Boolean = false,
)

@Stable
data class CustomInputState(
    val name: String = "",
    val weight: String = "",
    val carbohydrate: String = "",
    val protein: String = "",
    val fat: String = "",
    val calculatedKcal: String = "0",
    val isFavorite: Boolean = false,
    val isDialogVisible: Boolean = false
) {
    val isConfirmEnabled: Boolean
        get() = name.isNotBlank() &&
                weight.isDigitsOnly() && weight.isNotEmpty() &&
                carbohydrate.isDigitsOnly() && carbohydrate.isNotEmpty() &&
                protein.isDigitsOnly() && protein.isNotEmpty() &&
                fat.isDigitsOnly() && fat.isNotEmpty()
}

data class DietDateSection(
    val date: LocalDate,
    val meals: List<DietCollection>,
    val totalCalories: Int,
    val totalCarbs: Int = 0,
    val totalProteins: Int = 0,
    val totalFats: Int = 0
)

sealed class NavigationEvent {
    data object NavigateBackToDietScreen : NavigationEvent()
}