package com.cases.carefull.features.carefullcontents.diagnosis.hospital

import com.cases.carefull.domain.model.Hospital

data class HospitalUiState(
    val selectedHospital: Hospital? = null,
    val mapCenterLatitude: Double? = null,
    val mapCenterLongitude: Double? = null,

    val department: String = "",
    val bestHospitals: List<Hospital> = emptyList(),
    val allHospitals: List<Hospital> = emptyList(),

    val searchQuery: String = "",
    val searchHospitals: List<Hospital> = emptyList(),
    val selectedDepartment: String? = null,
    val filteredHospitals: List<Hospital> = emptyList(),

    val isLoading: Boolean = false,
    val errorMessage: String? = null
)