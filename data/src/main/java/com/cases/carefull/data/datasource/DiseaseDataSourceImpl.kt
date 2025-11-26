package com.cases.carefull.data.datasource

import com.cases.carefull.data.di.DiseaseApiKey
import com.cases.carefull.data.dto.DiseaseResponseDto
import com.cases.carefull.data.network.DiseaseApiService
import javax.inject.Inject

class DiseaseDataSourceImpl @Inject constructor(
    private val diseaseApiService: DiseaseApiService,
    @DiseaseApiKey private val diseaseApiKey: String
): DiseaseDataSource {
    override suspend fun getDiseaseList(): DiseaseResponseDto {
        return diseaseApiService.getDiseaseList(token = diseaseApiKey)
    }

    override suspend fun getDiseaseDetail(contentSn: String): DiseaseResponseDto {
        return diseaseApiService.getDiseaseDetail(
            token = diseaseApiKey,
            contentSn = contentSn
        )
    }
}