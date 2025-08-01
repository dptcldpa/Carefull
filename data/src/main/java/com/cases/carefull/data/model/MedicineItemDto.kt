package com.cases.carefull.data.model

import com.google.gson.annotations.SerializedName

data class MedicineItemDto(
    @SerializedName("entpName") val entpName: String?,            // 업체명
    @SerializedName("itemName") val itemName: String?,            // 제품명
//    @SerializedName("itemSeq") val itemSeq: String,              // 품목 기준 코드
    @SerializedName("efcyQesitm") val efcyQesitm: String?,        // 효능
    @SerializedName("useMethodQesitm") val useMethodQesitm: String?, // 사용법
    @SerializedName("atpnWarnQesitm") val atpnWarnQesitm: String?, // 주의사항 경고 (null일 수 있음)
    @SerializedName("atpnQesitm") val atpnQesitm: String?,        // 주의사항
    @SerializedName("intrcQesitm") val intrcQesitm: String?,      // 상호작용 (null일 수 있음)
    @SerializedName("seQesitm") val seQesitm: String?,            // 부작용 (null일 수 있음)
    @SerializedName("depositMethodQesitm") val depositMethodQesitm: String?, // 보관법
    @SerializedName("itemImage") val itemImage: String?          // 약 이미지 URL (null일 수 있음)
)