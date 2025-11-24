package com.cases.carefull.features.carefullcontents.diagnosis.hospital

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cases.carefull.domain.model.DepartmentCodeItem
import com.cases.carefull.domain.model.Hospital
import com.cases.carefull.domain.model.Location
import com.cases.carefull.domain.repository.HospitalRepository
import com.cases.carefull.domain.repository.LocationRepository
import com.cases.carefull.domain.util.DataResourceResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HospitalViewModel @Inject constructor(
    private val hospitalRepository: HospitalRepository,
    private val locationRepository: LocationRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _uiState = MutableStateFlow(HospitalUiState())
    val uiState = _uiState.asStateFlow()

    val departmentList: List<DepartmentCodeItem> = hospitalRepository.getDepartmentCodes()

    init {
        val department = savedStateHandle.get<String>("department") ?: "정보 없음"

        _uiState.update { it.copy(department = department) }
    }

    fun loadHospitals(latitude: Double, longitude: Double) {
        if (_uiState.value.isLoading) return

        val department = _uiState.value.department
        if (department.isBlank() || department == "정보 없음") return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val location = locationRepository.getLastKnownLocation()
            val lat = location?.latitude ?: 37.5665
            val lon = location?.longitude ?: 126.9780

            hospitalRepository.getHospitals(department, lat, lon)
                .onEach { result ->
                    _uiState.update { currentState ->
                        when (result) {
                            is DataResourceResult.Loading -> currentState

                            is DataResourceResult.Success -> {
                                val allHospitals = result.data ?: emptyList()
                                val bestHospitals = allHospitals.filter { it.isExcellent }
                                val errorMessage = if (allHospitals.isEmpty()) {
                                    "'${department}'에 해당하는 병원 정보가 없습니다."
                                } else {
                                    null
                                }
                                currentState.copy(
                                    isLoading = false,
                                    allHospitals = allHospitals,
                                    bestHospitals = bestHospitals,
                                    filteredHospitals = allHospitals,
                                    selectedDepartment = null,
                                    errorMessage = errorMessage
                                )
                            }

                            is DataResourceResult.Error -> {
                                currentState.copy(
                                    isLoading = false,
                                    errorMessage = "병원 목록을 불러오는 데 실패했습니다."
                                )
                            }
                        }
                    }
                }.launchIn(viewModelScope)
        }
    }

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
                                searchHospitals = result.data,
                                filteredHospitals = result.data,
                                errorMessage = errorMessage
                            )
                        }
                        is DataResourceResult.Loading -> currentState.copy(
                            isLoading = true,
                            errorMessage = null
                        )
                        is DataResourceResult.Error -> currentState.copy(
                            isLoading = false,
                            searchHospitals = emptyList(),
                            errorMessage = result.exception.message
                        )
                    }
                }
            }
        }
    }

    fun selectDepartment(department: String?) {
        _uiState.update { it.copy(selectedDepartment = department) }

        val lat = _uiState.value.mapCenterLatitude
        val lon = _uiState.value.mapCenterLongitude

        if (lat != null && lon != null) {
            if (department != null) {
                _uiState.update { it.copy(searchQuery = department) }
            } else {
                _uiState.update { it.copy(searchQuery = "") }
            }
            searchHospitals(lat, lon)
        }
    }

    fun loadCurrentLocationAndHospitals() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val location = locationRepository.getCurrentLocation()

            _uiState.update { it.copy(isLoading = false) }

            location?.let {
                _uiState.update { state ->
                    state.copy(
                        mapCenterLatitude = it.latitude,
                        mapCenterLongitude = it.longitude
                    )
                }
                loadHospitals(it.latitude, it.longitude)
            }
        }
    }

    fun loadCurrentLocationForSearch(onLocationLoaded: (Location) -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val location = locationRepository.getCurrentLocation()

            _uiState.update { it.copy(isLoading = false) }

            location?.let {
                _uiState.update { state ->
                    state.copy(
                        mapCenterLatitude = it.latitude,
                        mapCenterLongitude = it.longitude
                    )
                }
                onLocationLoaded(it)
            }
        }
    }

    fun onCameraMoved(latitude: Double, longitude: Double) {
        _uiState.update {
            it.copy(
                mapCenterLatitude = latitude,
                mapCenterLongitude = longitude
            )
        }
    }

    fun selectHospital(hospital: Hospital) {
        _uiState.update { it.copy(selectedHospital = hospital) }
    }

    fun clearHospitalSelection() {
        _uiState.update {
            it.copy(
                selectedHospital = null,
                filteredHospitals = emptyList(),
                searchHospitals = emptyList(),
                searchQuery = ""
            )
        }
    }

    fun errorMessageShown() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}