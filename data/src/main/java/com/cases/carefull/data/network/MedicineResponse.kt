package com.cases.carefull.data.network

import com.cases.carefull.data.dto.MedicineItemDto
import com.cases.carefull.domain.model.MedicineItem
import com.google.gson.annotations.SerializedName

data class MedicineResponse(
    @SerializedName("header") val header: Header,
    @SerializedName("body") val body: Body
)

data class Header(
    @SerializedName("resultCode") val resultCode: String,
    @SerializedName("resultMsg") val resultMsg: String
)

data class Body(
    @SerializedName("pageNo") val pageNo: Int,
    @SerializedName("totalCount") val totalCount: Int,
    @SerializedName("numOfRows") val numOfRows: Int,
    @SerializedName("items") val items: List<MedicineItemDto>
)