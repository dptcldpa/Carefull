package com.cases.carefull.features.carefullcontents.diagnosis.disease

import com.cases.carefull.domain.model.Disease

data class DiseaseUiState(
    val diseases: List<Disease> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

data class DiseaseDetailUiState(
    val disease: Disease? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)