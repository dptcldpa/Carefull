package com.cases.carefull.data.dto.diagnosis

import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.PropertyElement
import com.tickaroo.tikxml.annotation.Xml

@Xml(name = "header")
data class HospitalHeader(
    @PropertyElement(name = "resultCode") val resultCode: String?,

    @PropertyElement(name = "resultMsg") val resultMsg: String?
)

// 병원 목록
@Xml(name = "response")
data class HospitalListResponseDto(
    @Element(name = "header") val header:HospitalHeader?,

    @Element(name = "body") val body: HospitalListBody?
)

@Xml(name = "body")
data class HospitalListBody(
    @Element(name = "items") val items: Items?,

    @PropertyElement(name = "numOfRows") val numOfRows: Int?,

    @PropertyElement(name = "pageNo") val pageNo: Int?,

    @PropertyElement(name = "totalCount") val totalCount: Int?
)

@Xml(name = "items")
data class Items(
    @Element(name = "item") val hospitals: List<HospitalItemDto>? = null
)

// 우수병원
@Xml(name = "response")
data class HospitalExcellResponseDto(
    @Element(name = "header") val header: HospitalHeader?,
    @Element(name = "body") val body: HospitalExcellBody?
)

@Xml(name = "body")
data class HospitalExcellBody(
    @Element(name = "items") val items: HospitalExcellItems?
)

@Xml(name = "items")
data class HospitalExcellItems(
    @Element(name = "item") val item: List<HospitalExcellItem>?
)

@Xml(name = "item")
data class HospitalExcellItem(
    @PropertyElement(name = "ykiho") val ykiho: String?,
    @PropertyElement(name = "asmGrd") val asmGrd: String?,
    @PropertyElement(name = "asmNm") val asmNm: String?
)