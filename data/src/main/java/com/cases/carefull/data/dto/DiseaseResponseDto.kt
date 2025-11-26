package com.cases.carefull.data.dto

import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.PropertyElement
import com.tickaroo.tikxml.annotation.Xml

@Xml(name = "XML")
data class DiseaseResponseDto(
    @Element(name = "HEAD") val head: DiseaseHead?,

    @Element(name = "svc") val svc: DiseaseSvc?
)

@Xml(name = "HEAD")
data class DiseaseHead(
    @PropertyElement(name = "CODE") val code: String?,

    @PropertyElement(name = "MESSAGE") val message: String?
)

@Xml(name = "svc")
data class DiseaseSvc(
    @PropertyElement(name = "CNTNTSSJ") val diseaseName: String?,

    @PropertyElement(name = "CNTNTS_SN") val contentSn: String?,

    @PropertyElement(name = "LCLASSN") val lclassSn: String?,

    @Element(name = "cntntsClList") val contentList: ContentList?
)

@Xml(name = "cntntsClList")
data class ContentList(
    @Element(name = "cntntsCl") val items: List<ContentItem>?
)

@Xml(name = "cntntsCl")
data class ContentItem(
    @PropertyElement(name = "CNTNTS_CL_NM") val sectionName: String?,

    @PropertyElement(name = "CNTNTSCLSN") val sectionSn: String?,

    @PropertyElement(name = "CNTNTS_CL_CN") val content: String?
)