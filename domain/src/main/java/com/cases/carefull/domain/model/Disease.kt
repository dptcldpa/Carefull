package com.cases.carefull.domain.model

data class Disease(
    val diseaseName: String,
    val contentSn: String,
    val contentList: List<DiseaseContent>
)

data class DiseaseContent(
    val sectionName: String,
    val content: String
)