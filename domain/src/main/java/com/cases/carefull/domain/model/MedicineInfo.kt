package com.cases.carefull.domain.model

data class MedicineItem(
    val entpName: String?,            // 업체명
    val itemName: String?,            // 제품명
//    val itemSeq: String?,             // 품목 기준 코드
    val efcyQesitm: String?,        // 효능
    val useMethodQesitm: String?, // 사용법
    val atpnWarnQesitm: String?, // 주의사항 경고 (null일 수 있음)
    val atpnQesitm: String?,        // 주의사항
    val intrcQesitm: String?,      // 상호작용 (null일 수 있음)
    val seQesitm: String?,            // 부작용 (null일 수 있음)
    val depositMethodQesitm: String?, // 보관법
    val itemImage: String?          // 약 이미지 URL (null일 수 있음)
)