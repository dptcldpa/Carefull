package com.cases.carefull.data.model

import com.google.gson.annotations.SerializedName


data class DietResponse(
	@SerializedName("header")
	val header: FoodHeader,
	@SerializedName("body")
	val body: FoodBody
)

data class FoodHeader(
	@SerializedName("resultCode")
	val resultCode: String,
	@SerializedName("resultMsg")
	val resultMsg: String
)

data class FoodBody(
    @SerializedName("items")
	val items: List<DietItemDto>,
    @SerializedName("numOfRows")
	val numOfRows: Int,
    @SerializedName("pageNo")
	val pageNo: Int,
    @SerializedName("totalCount")
	val totalCount: Int
)