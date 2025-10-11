package com.cases.carefull.domain.model

data class HospitalItem(
    val ykiho: String,                    // 요양기호
    val yadmNm: String,                   // 병원명
    val clCd: String?,                    // 종별코드
    val clCdNm: String?,                  // 종별코드명
    val sidoCd: String?,                  // 시도코드
    val sidoCdNm: String?,                // 시도명
    val sgguCd: String?,                  // 시군구코드
    val sgguCdNm: String?,                // 시군구명
    val emdongNm: String?,                // 읍면동명
    val postNo: String?,                  // 우편번호
    val addr: String,                     // 주소
    val telno: String?,                   // 전화번호
    val hospUrl: String?,                 // 홈페이지
    val estbDd: String?,                  // 개설일자
    val drTotCnt: Int?,                   // 의사 총 인원
    val gdrCnt: Int?,                     // 일반의 인원
    val gdrnCnt: Int?,                    // 일반의 인원
    val sdrnCnt: Int?,                    // 전문의 인원
    val sdrCnt: Int?,                     // 전문의 인원
    val pnursCnt: Int?,                   // 간호사 인원
    val resdntCnt: Int?,                  // 레지던트 인원
    val intnCnt: Int?,                    // 인턴 인원
    val XPos: String?,                    // X좌표(경도)
    val YPos: String?,                    // Y좌표(위도)
    val dgidIdName: String?               // 진료과목
)