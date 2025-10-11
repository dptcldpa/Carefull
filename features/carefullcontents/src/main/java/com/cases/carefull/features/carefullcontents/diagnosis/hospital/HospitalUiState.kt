package com.cases.carefull.features.carefullcontents.diagnosis.hospital

import com.cases.carefull.domain.model.HospitalItem

data class HospitalUiState(
    val hospitals: List<HospitalItem> = emptyList(), // 병원 목록
    val selectedItem: HospitalItem? = null,
    val recentSearches: List<String> = emptyList(),
    val searchQuery: String = "",

    val isLoading: Boolean = false,                     // 로딩 중인지 여부
    val errorMessage: String? = null                    // 에러 메시지
)