package com.cases.carefull.features.carefullcontents.routine.diet

import android.os.Build
import androidx.annotation.RequiresApi
import com.cases.carefull.domain.model.CalendarViewType
import com.cases.carefull.domain.model.diet.Bmr
import com.cases.carefull.domain.model.diet.BmrActivity
import com.cases.carefull.domain.model.diet.DietCollection
import com.cases.carefull.domain.model.diet.FavoriteMeal
import com.cases.carefull.domain.model.diet.Gender
import com.cases.carefull.domain.model.diet.MealType
import com.cases.carefull.domain.model.diet.RecentMealSearch
import java.time.LocalDate
import java.time.YearMonth

data class DietDateSection(
	val date: LocalDate,
	val meals: List<DietCollection>,
	val totalCalories: Int,
	val totalCarbs: Int = 0,
	val totalProteins: Int = 0,
	val totalFats: Int = 0
)
@RequiresApi(Build.VERSION_CODES.O)
data class DietUiState(
	val dietSections: List<DietDateSection> = emptyList(),
	val allDietSections: List<DietDateSection> = emptyList(),
	val selectedDateSection: DietDateSection? = null,
	
	val totalCalories: Int = 0,
	val totalCarbs: Int = 0,
	val totalProteins: Int = 0,
	val totalFats: Int = 0,
	
	val searchResults: List<DietCollection> = emptyList(),
	val searchQuery: String = "",
	
	val isLoading: Boolean = true,
	val isError: Boolean = false,
	
	val mealTypeSelection: MealType? = null,
	val selectedDate: LocalDate = LocalDate.now(),
	
	val datePickerDisplayedMonth: YearMonth = YearMonth.from(selectedDate),
	val datePickerCalendarDates: List<LocalDate> = emptyList(),
	val allMealLoggedDates: Set<LocalDate> = emptySet(),
	
	val bmrState: BmrUiState = BmrUiState(),
	val savedBmr: Bmr? = null,
	
	val favoriteMeals: List<FavoriteMeal> = emptyList(),
	val selectedFavoriteForEditing: FavoriteMeal? = null,
	
	val isFavoritesDialogVisible: Boolean = false,
	val isDatePickerVisible: Boolean = false,
	val recentSearches: List<RecentMealSearch> = emptyList()
	
//	val viewType: CalendarViewType = CalendarViewType.MONTHLY,
//	val displayedYearMonth: YearMonth = YearMonth.now(),
//	val calendarDates: List<LocalDate> = emptyList(),
//	val loggedMealDates: Set<LocalDate> = emptySet(),
//	val hasLoggedMealToday: Boolean = false,
//	val selectedDateInfo: String = "오늘",
//	val pagerTargetPage: Int = START_PAGE,
//	val isYearMonthPickerVisible: Boolean = false,
//	val selectedDateMealInfo: String? = null,
	) {
//	companion object {
//		const val START_PAGE = Int.MAX_VALUE / 2
//	}
	val isBmrChanged: Boolean
		get() {
			if (savedBmr == null) return true
			
			val isGenderSame = (savedBmr.gender == (bmrState.gender == Gender.MALE))
			val isHeightSame = (savedBmr.height.toString() == bmrState.height)
			val isWeightSame = (savedBmr.weight.toString() == bmrState.weight)
			val isAgeSame = (savedBmr.age.toString() == bmrState.age)
			val isActivitySame = (savedBmr.activity == bmrState.activity)
			
			return !(isGenderSame && isHeightSame && isWeightSame && isAgeSame && isActivitySame)
		}
}

data class BmrUiState(
	val gender: Gender = Gender.MALE,
	val height: String = "",
	val weight: String = "",
	val age: String = "",
	val activity: BmrActivity = BmrActivity.NONE,
	val calculatedBmr: Int = 0,
	val activityMetabolism: Int = 0,
	val isLoading: Boolean = false
)

sealed class NavigationEvent {
	data object NavigateBackToDietScreen : NavigationEvent()
}