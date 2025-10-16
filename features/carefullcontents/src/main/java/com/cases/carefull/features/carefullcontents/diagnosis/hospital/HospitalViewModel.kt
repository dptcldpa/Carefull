package com.cases.carefull.features.carefullcontents.diagnosis.hospital

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cases.carefull.domain.model.HospitalItem
import com.cases.carefull.domain.repository.HospitalRepository
import com.cases.carefull.domain.util.DataResourceResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HospitalViewModel @Inject constructor(
    private val hospitalRepository: HospitalRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(HospitalUiState())
    val uiState = _uiState.asStateFlow()

    private var searchJob: Job? = null

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun searchHospitals(latitude: Double, longitude: Double) {
        searchJob?.cancel()

        searchJob = viewModelScope.launch {
            val currentQuery = _uiState.value.searchQuery

            hospitalRepository.getHospitals(
                query = currentQuery,
                latitude = latitude,
                longitude = longitude
            ).collect { result ->
                _uiState.update { currentState ->
                    when (result) {
                        is DataResourceResult.Success -> {
                            val errorMessage = if (result.data.isEmpty() && currentState.searchQuery.isNotBlank()) {
                                "검색 결과가 없습니다."
                            } else {
                                null
                            }
                            currentState.copy(
                                isLoading = false,
                                hospitals = result.data,
                                errorMessage = errorMessage
                            )
                        }
                        is DataResourceResult.Loading -> currentState.copy(
                            isLoading = true,
                            errorMessage = null
                        )
                        is DataResourceResult.Error -> currentState.copy(
                            isLoading = false,
                            hospitals = emptyList(),
                            errorMessage = result.exception.message
                        )
                    }
                }
            }
        }
    }

    fun selectHospital(hospital: HospitalItem) {
        _uiState.update { it.copy(selectedItem = hospital) }
    }

    fun clearHospitalSelection() {
        _uiState.update { it.copy(selectedItem = null) }
    }

    fun errorMessageShown() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}