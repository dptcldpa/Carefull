package com.cases.carefull.data.network

import com.google.gson.annotations.SerializedName


// 2. 실제 응답 내용을 담는 클래스: JSON의 'response' 객체에 해당
data class DietResponse(
	@SerializedName("header")
	val header: FoodHeader?,
	@SerializedName("body")
	val body: FoodBody?
)

// 3. API 응답 상태를 담는 헤더
data class FoodHeader(
	@SerializedName("resultCode")
	val resultCode: String?,
	@SerializedName("resultMsg")
	val resultMsg: String?
)

data class FoodBody(
	// items 키 안에는 FoodData 객체들의 배열이 들어갑니다.
	@SerializedName("items")
	val items: List<FoodData>?,
	@SerializedName("numOfRows")
	val numOfRows: Int?,
	@SerializedName("pageNo")
	val pageNo: Int?,
	@SerializedName("totalCount")
	val totalCount: Int?
)

data class FoodData(
	@SerializedName("FOOD_NM_KR")
	val name: String?,
	@SerializedName("SERVING_SIZE")
	val serving: String?,
	@SerializedName("AMT_NUM1")
	val kcal: String?,
	@SerializedName("AMT_NUM6")
	val carbohydrate: String?,
	@SerializedName("AMT_NUM7")
	val carbohydrateSugar: String?,
	@SerializedName("AMT_NUM3")
	val protein: String?,
	@SerializedName("AMT_NUM4")
	val fat: String?,
	@SerializedName("AMT_NUM24")
	val saturatedFat: String?,
	@SerializedName("AMT_NUM13")
	val sodium: String?
)