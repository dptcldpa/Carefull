package com.cases.carefull.features.carefullcontents.diagnosis.medicine

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cases.carefull.domain.model.MedicineItem
import com.cases.carefull.domain.repository.MedicineRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MedicineViewModel(
    private val repository: MedicineRepository
) : ViewModel() {

    private val _medicineList = MutableStateFlow<List<MedicineItem>>(emptyList())
    val medicineList = _medicineList.asStateFlow()

    private val _selectedItem = MutableStateFlow<MedicineItem?>(null)
    val selectedItem = _selectedItem.asStateFlow()

    private val _recentSearches = MutableStateFlow<List<String>>(emptyList())
    val recentSearches = _recentSearches.asStateFlow()

    fun searchMedicine(query: String) {
        viewModelScope.launch {
            Log.d("API_TEST", "ViewModel: Repository에 검색 요청")
            val result = repository.searchMedicines(query)

            result.onSuccess { items ->
                Log.d("API_TEST", "ViewModel: Repository로부터 성공 응답 받음, ${items.size}개")
                _medicineList.value = items
            }.onFailure { throwable ->
                Log.e("API_TEST", "ViewModel: Repository로부터 실패 응답 받음", throwable)
                _medicineList.value = emptyList()
            }
        }
    }

    fun setSelectedItem(item: MedicineItem) {
        Log.d("NAVIGATION_TEST", "ViewModel: setSelectedItem 호출됨. 선택된 약: ${item.itemName}")
        _selectedItem.value = item
    }

    fun addRecentSearch(query: String) {
        val trimmedQuery = query.trim()
        if (trimmedQuery.isBlank()) return

        val currentList = _recentSearches.value.toMutableList()
        currentList.remove(trimmedQuery)
        currentList.add(0, trimmedQuery)

        _recentSearches.value = currentList.take(10)
    }

    fun removeRecentSearch(query: String) {
        val currentList = _recentSearches.value.toMutableList()
        currentList.remove(query)
        _recentSearches.value = currentList
    }

    fun clearRecentSearches() {
        _recentSearches.value = emptyList()
    }
}