package com.cases.carefull.features.carefullcontents.diagnosis.medicine

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cases.carefull.domain.model.MedicineItem
import com.cases.carefull.domain.usecase.MedicineSearchUseCase
import com.cases.carefull.features.carefullcontents.diagnosis.medicine.MedicineUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MedicineViewModel(
    private val medicineSearchUseCase: MedicineSearchUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(MedicineUiState())
    val uiState = _uiState.asStateFlow()
    fun onQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }

        if (query.isNotBlank()) {
            searchMedicine(query)
        } else {
            _uiState.update {
                it.copy(
                    searchResult = emptyList(),
                    isLoading = false,
                    errorMessage = null
                )
            }
        }
    }

    private fun searchMedicine(query: String) {
        viewModelScope.launch {
            medicineSearchUseCase(query = query).onStart {
                Log.d("API_TEST", "ViewModel: Flow 시작, 로딩 상태로 변경")
                _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            }
                .catch { throwable ->
                    Log.e("API_TEST", "ViewModel: Flow 에러 발생", throwable)
                    _uiState.update {
                        it.copy(
                            errorMessage = throwable.message ?: "알 수 없는 에러",
                            isLoading = false
                        )
                    }
                }
                .collect { items ->
                    Log.d("API_TEST", "ViewModel: Flow 데이터 수집 성공, ${items.size}개")
                    _uiState.update {
                        it.copy(
                            searchResult = items,
                            isLoading = false
                        )
                    }
                }
        }
    }


    fun setSelectedItem(item: MedicineItem) {
        Log.d("NAVIGATION_TEST", "ViewModel: setSelectedItem 호출됨. 선택된 약: ${item.itemName}")
        _uiState.update { it.copy(selectedItem = item) }
    }

    fun errorMessageShown() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun addRecentSearch(query: String) {
        val trimmedQuery = query.trim()
        if (trimmedQuery.isBlank()) return

        _uiState.update { currentList ->
            val newList = currentList.recentSearches.toMutableList().apply {
                remove(trimmedQuery)
                add(0, trimmedQuery)
            }
            currentList.copy(recentSearches = newList.take(10))
        }
    }

    fun removeRecentSearch(query: String) {
        _uiState.update { currentList ->
            currentList.copy(recentSearches = currentList.recentSearches.filter { it != query })
        }
    }

    fun clearRecentSearches() {
        _uiState.update { it.copy(recentSearches = emptyList()) }
    }
}