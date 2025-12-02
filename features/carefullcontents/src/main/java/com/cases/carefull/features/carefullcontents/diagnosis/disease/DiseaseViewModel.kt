package com.cases.carefull.features.carefullcontents.diagnosis.disease

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cases.carefull.domain.model.Disease
import com.cases.carefull.domain.repository.diagnosis.DiseaseRepository
import com.cases.carefull.domain.util.DataResourceResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DiseaseViewModel @Inject constructor(
    private val repository: DiseaseRepository
) : ViewModel() {

    private val _diseaseListState = MutableStateFlow(DiseaseUiState())
    val diseaseListState: StateFlow<DiseaseUiState> = _diseaseListState.asStateFlow()

    private val _diseaseDetailState = MutableStateFlow(DiseaseDetailUiState())
    val diseaseDetailState: StateFlow<DiseaseDetailUiState> = _diseaseDetailState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private var allDiseases: List<Disease> = emptyList()

//    init {
//        loadDiseaseList()
//    }

    private fun loadDiseaseList() {
        viewModelScope.launch {
            repository.getDiseaseList().collect { result ->
                Log.d("DiseaseViewModel", "Result: $result")  // 추가

                _diseaseListState.update {
                    when (result) {
                        is DataResourceResult.Loading -> {
                            Log.d("DiseaseViewModel", "Loading...")
                            it.copy(isLoading = true, errorMessage = null)
                        }
                        is DataResourceResult.Success -> {
                            allDiseases = result.data
                            Log.d("DiseaseViewModel", "Success: ${result.data.size} diseases")
                            Log.d("DiseaseViewModel", "First disease: ${result.data.firstOrNull()?.diseaseName}")

                            it.copy(
                                diseases = result.data,
                                isLoading = false,
                                errorMessage = null
                            )
                        }
                        is DataResourceResult.Error -> {
                            Log.e("DiseaseViewModel", "Error: ${result.exception.message}")
                            it.copy(
                                isLoading = false,
                                errorMessage = result.exception.message ?: "알 수 없는 오류"
                            )
                        }
                    }
                }
            }
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        filterDiseases(query)
    }

    private fun filterDiseases(query: String) {
        Log.d("DiseaseViewModel", "Filter query: '$query'")
        Log.d("DiseaseViewModel", "All diseases count: ${allDiseases.size}")

        if (query.isBlank()) {
            _diseaseListState.update { it.copy(diseases = allDiseases, errorMessage = null) }
            return
        }

        val filtered = allDiseases.filter { disease ->
            val match = disease.diseaseName.contains(query, ignoreCase = true)
            Log.d("DiseaseViewModel", "Disease: ${disease.diseaseName}, Match: $match")
            match
        }

        Log.d("DiseaseViewModel", "Filtered count: ${filtered.size}")

        _diseaseListState.update {
            it.copy(
                diseases = filtered,
                errorMessage = if (filtered.isEmpty()) "검색 결과가 없습니다" else null
            )
        }
    }

    fun getDiseaseDetail(contentSn: String) {
        viewModelScope.launch {
            repository.getDiseaseDetail(contentSn).collect { result ->
                when (result) {
                    is DataResourceResult.Loading -> {
                        _diseaseDetailState.update { it.copy(isLoading = true, errorMessage = null) }
                    }
                    is DataResourceResult.Success -> {
                        _diseaseDetailState.update {
                            it.copy(
                                disease = result.data,
                                isLoading = false,
                                errorMessage = null
                            )
                        }
                    }
                    is DataResourceResult.Error -> {
                        _diseaseDetailState.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = result.exception.message ?: "알 수 없는 오류"
                            )
                        }
                    }
                }
            }
        }
    }

    fun clearDiseaseDetail() {
        _diseaseDetailState.value = DiseaseDetailUiState()
    }
}