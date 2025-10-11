package com.cases.carefull.data.dto

import com.tickaroo.tikxml.annotation.PropertyElement
import com.tickaroo.tikxml.annotation.Xml

@Xml(name = "item")
data class HospitalItemDto (
    @PropertyElement(name = "ykiho")
    val ykiho: String?,             // 요양기호
    @PropertyElement(name = "yadmNm")
    val hospitalName: String?,              // 병원명
    @PropertyElement(name = "clCd")
    val medicalClassCode: String?,  // 종별코드 (11:종합병원, 21:병원, 31:의원 등)
    @PropertyElement(name = "clCdNm")
    val medicalClassName: String?,// 종별코드명
    @PropertyElement(name = "sidoCd")
    val sidoCode: String?,        // 시도코드
    @PropertyElement(name = "sidoCdNm")
    val sidoName: String?,      // 시도명
    @PropertyElement(name = "sgguCd")
    val sgguCode: String?,        // 시군구코드
    @PropertyElement(name = "sgguCdNm")
    val sgguName: String?,      // 시군구명
    @PropertyElement(name = "emdongNmyy")
    val townName: String?,      // 읍면동명
    @PropertyElement(name = "postNo")
    val postCode: String?,        // 우편번호
    @PropertyElement(name = "addr")
    val address: String?,            // 주소
    @PropertyElement(name = "telno")
    val telephone: String?,         // 전화번호
    @PropertyElement(name = "hospUrl")
    val hospitalUrl: String?,     // 홈페이지
    @PropertyElement(name = "estbDd")
    val establishmentDate: String?,// 개설일자
    @PropertyElement(name = "drTotCnt")
    val doctorCount: Int,        // 의사 총 수 (Int로 변경, 필수값으로 가정)
    @PropertyElement(name = "gdrCnt")
    val generalDoctorCount: Int?,  // 일반의 수
    @PropertyElement(name = "gdrnCnt")
    val generalDoctorSpecialistCount: Int?, // 일반의 전문의 수
    @PropertyElement(name = "sdrnCnt")
    val residentSpecialistCount: Int?, // 수련의 전문의 수
    @PropertyElement(name = "sdrCnt")
    val specialistDoctorCount: Int?,// 전문의 수
    @PropertyElement(name = "pnursCnt")
    val nurseCount: Int?,        // 간호사 수
    @PropertyElement(name = "resdntCnt")
    val residentCount: Int?,    // 레지던트 수
    @PropertyElement(name = "intnCnt")
    val internCount: Int?,        // 인턴 수
    @PropertyElement(name = "XPos")
    val longitude: Double?,         // 경도 (WGS84)
    @PropertyElement(name = "YPos")
    val latitude: Double?,          // 위도 (WGS84)
    @PropertyElement(name = "dgidIdName")
    val department: String?,  // 진료과목

    // 7. 반경정보 (위치기반 검색 시에만 제공됨)
    @PropertyElement(name = "distance")
    val distance: Double?       // 중심좌표에서의 거리
)