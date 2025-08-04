package com.cases.carefull.features.carefullcontents

import com.cases.carefull.domain.model.MedicineItem

data class UiState(
    val searchResult: List<MedicineItem> = emptyList(),
    val selectedItem: MedicineItem? = null,
    val recentSearches: List<String> = emptyList(),
    val searchQuery: String = "",
    
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)