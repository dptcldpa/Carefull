package com.cases.carefull.features.carefullcontents.routine.diet

import com.cases.carefull.domain.model.diet.DietCollection
import com.cases.carefull.domain.model.diet.MealType

data class DietUiState(
    val mealsByTime: Map<MealType, List<DietCollection>> = emptyMap(),
    val totalCalories: Int = 0,
    val totalCarbs: Int = 0,
    val totalProteins: Int = 0,
    val totalFats: Int = 0,

    val searchResults: List<DietCollection> = emptyList(),
    val isLoading: Boolean = true,
    val isError: Boolean = false,

    val mealTypeSelection: MealType? = null
)