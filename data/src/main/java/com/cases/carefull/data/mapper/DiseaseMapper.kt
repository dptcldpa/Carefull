package com.cases.carefull.data.mapper

import com.cases.carefull.data.dto.diagnosis.ContentItem
import com.cases.carefull.data.dto.diagnosis.DiseaseResponseDto
import com.cases.carefull.domain.model.Disease
import com.cases.carefull.domain.model.DiseaseContent

fun DiseaseResponseDto.toDomain(): Disease {
    return Disease(
        diseaseName = this.svc?.diseaseName?.trim() ?: "",
        contentSn = this.svc?.contentSn ?: "",
        contentList = this.svc?.contentList?.items
            ?.filter { !it.content.isNullOrBlank() && !it.content.startsWith("http") }
            ?.map { it.toDomain() }
            ?: emptyList()
    )
}

fun ContentItem.toDomain(): DiseaseContent {
    return DiseaseContent(
        sectionName = this.sectionName?.trim() ?: "",
        content = this.content?.trim() ?: ""
    )
}