package com.cases.carefull.features.carefullcontents.diagnosis.medicine

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cases.carefull.domain.model.MedicineItem
import com.cases.carefull.domain.repository.MedicineRepository
import com.cases.carefull.domain.usecase.MedicineSearchUseCase
import com.cases.carefull.features.carefullcontents.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

//class MedicineViewModel(
//	private val medicineSearchUseCase: MedicineSearchUseCase,
//	private val medicineApiKey: String
//) : ViewModel() {
//
//	private val _searchResultState =
//		MutableStateFlow<UiState<List<MedicineItem>>>(UiState.Success(emptyList()))
//	val searchResultState = _searchResultState.asStateFlow()
//
//	private val _selectedItem = MutableStateFlow<MedicineItem?>(null)
//	val selectedItem = _selectedItem.asStateFlow()
//
//	private val _recentSearches = MutableStateFlow<List<String>>(emptyList())
//	val recentSearches = _recentSearches.asStateFlow()
//
//	private val _searchQuery = MutableStateFlow("")
//	val searchQuery = _searchQuery.asStateFlow()
//
//	fun onQueryChange(query: String, medicineApiKey: String) {
//		_searchQuery.value = query
//
//		if (query.isNotBlank()) {
//			searchMedicine(medicineApiKey, query)
//		} else {
//			_searchResultState.value = UiState.Success(emptyList())
//		}
//	}
//
//	private fun searchMedicine(medicineApiKey: String, query: String) {
//		viewModelScope.launch {
//			_searchResultState.value = UiState.Loading
//			Log.d("API_TEST", "ViewModel: Repository에 검색 요청 (쿼리: $query)")
//
//			val result = medicineSearchUseCase(
//				medicineApiKey = medicineApiKey,
//				query = query
//			)
//
//			result.onSuccess { items ->
//				Log.d("API_TEST", "ViewModel: Repository로부터 성공 응답 받음, ${items.size}개")
//				_searchResultState.value = UiState.Success(items)
//			}.onFailure { throwable ->
//				Log.e("API_TEST", "ViewModel: Repository로부터 실패 응답 받음", throwable)
//				_searchResultState.value = UiState.Error(throwable.message ?: "알 수 없는 에러")
//			}
//		}
//	}
//
//	fun setSelectedItem(item: MedicineItem) {
//		Log.d("NAVIGATION_TEST", "ViewModel: setSelectedItem 호출됨. 선택된 약: ${item.itemName}")
//		_selectedItem.value = item
//	}
//
//	fun addRecentSearch(query: String) {
//		val trimmedQuery = query.trim()
//		if (trimmedQuery.isBlank()) return
//
//		_recentSearches.update { currentList ->
//			val newList = currentList.toMutableList().apply {
//				remove(trimmedQuery)
//				add(0, trimmedQuery)
//			}
//			newList.take(10)
//		}
//	}
//
//	fun removeRecentSearch(query: String) {
//		_recentSearches.update { currentList ->
//			currentList.filter { it != query }
//		}
//	}
//
//	fun clearRecentSearches() {
//		_recentSearches.value = emptyList()
//	}
//}

class MedicineViewModel(
    private val medicineSearchUseCase: MedicineSearchUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(UiState())
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