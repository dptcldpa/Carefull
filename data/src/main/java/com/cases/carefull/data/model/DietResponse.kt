package com.cases.carefull.data.model

import com.google.gson.annotations.SerializedName


// 2. 실제 응답 내용을 담는 클래스: JSON의 'response' 객체에 해당
data class DietResponse(
	@SerializedName("header")
	val header: FoodHeader,
	@SerializedName("body")
	val body: FoodBody
)

// 3. API 응답 상태를 담는 헤더
data class FoodHeader(
	@SerializedName("resultCode")
	val resultCode: String,
	@SerializedName("resultMsg")
	val resultMsg: String
)

data class FoodBody(
	// items 키 안에는 FoodData 객체들의 배열이 들어갑니다.
    @SerializedName("items")
	val items: List<DietItemDto>,
    @SerializedName("numOfRows")
	val numOfRows: Int,
    @SerializedName("pageNo")
	val pageNo: Int,
    @SerializedName("totalCount")
	val totalCount: Int
)