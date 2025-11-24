package com.cases.carefull.data.datasource

import com.cases.carefull.data.dto.DiseaseResponseDto

interface DiseaseDataSource {
    suspend fun getDiseaseList(): DiseaseResponseDto

    suspend fun getDiseaseDetail(contentSn: String): DiseaseResponseDto
}