package com.cases.carefull.data.dto

import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.PropertyElement
import com.tickaroo.tikxml.annotation.Xml

@Xml(name = "response")
data class HospitalResponseDto(
    @Element(name = "header")
    val header:HospitalHeader,

    @Element(name = "body")
    val body: HospitalBody
)

@Xml(name = "header")
data class HospitalHeader(
    @PropertyElement(name = "resultCode")
    val resultCode: String,

    @PropertyElement(name = "resultMsg")
    val resultMsg: String
)

@Xml(name = "body")
data class HospitalBody(
    @Element(name = "items")
    val items: Items,

    @PropertyElement(name = "numOfRows")
    val numOfRows: Int,

    @PropertyElement(name = "pageNo")
    val pageNo: Int,

    @PropertyElement(name = "totalCount")
    val totalCount: Int
)

@Xml(name = "items")
data class Items(
    @Element(name = "item")
    val hospitals: List<HospitalItemDto>? = null
)