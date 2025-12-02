package com.cases.carefull.data.dto.routine

import com.google.gson.annotations.SerializedName

data class FoodItemDto(
    @SerializedName("FOOD_NM_KR")
    val name: String?,
    @SerializedName("SERVING_SIZE")
    val servingSize: String?,
    @SerializedName("AMT_NUM1")
    val kcal: String?,
    @SerializedName("AMT_NUM6")
    val carbohydrate: String?,
    @SerializedName("AMT_NUM3")
    val protein: String?,
    @SerializedName("AMT_NUM4")
    val fat: String?
)
