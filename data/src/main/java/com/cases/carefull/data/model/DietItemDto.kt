package com.cases.carefull.data.model

import com.google.gson.annotations.SerializedName

//data class DietItemDto(
//	@SerializedName("FOOD_NM_KR")
//	val name: String?,
//	@SerializedName("SERVING_SIZE")
//	val serving: String?,
//	@SerializedName("AMT_NUM1")
//	val kcal: String?,
//	@SerializedName("AMT_NUM6")
//	val carbohydrate: String?,
//	@SerializedName("AMT_NUM3")
//	val protein: String?,
//	@SerializedName("AMT_NUM4")
//	val fat: String?
//)

data class DietItemDtoTwo(
	@SerializedName("FOOD_NM_KR")
	val name: String?,
	@SerializedName("SERVING_SIZE")
	val serving: String?,
	@SerializedName("AMT_NUM1")
	val kcal: String?,
	@SerializedName("AMT_NUM6")
	val carbohydrate: String?,
	@SerializedName("AMT_NUM3")
	val protein: String?,
	@SerializedName("AMT_NUM4")
	val fat: String?
)