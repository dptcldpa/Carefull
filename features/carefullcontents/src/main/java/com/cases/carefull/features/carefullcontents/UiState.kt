package com.cases.carefull.features.carefullcontents

import com.cases.carefull.domain.model.MedicineItem

//sealed class UiState<out T : Any?> {
//
//    object Loading : UiState<Nothing>()
//
//    data class Success<out T : Any>(val data: T) : UiState<T>()
//
//    data class Error(val errorMessage: String) : UiState<Nothing>()
//}


data class UiState(
    val searchResult: List<MedicineItem> = emptyList(),
    val selectedItem: MedicineItem? = null,
    val recentSearches: List<String> = emptyList(),
    val searchQuery: String = "",
    
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)