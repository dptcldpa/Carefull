package com.cases.carefull.data.mapper

import com.cases.carefull.data.dto.HospitalItemDto
import com.cases.carefull.domain.model.HospitalItem

fun HospitalItemDto.toDomain(): HospitalItem {
    return HospitalItem(
        ykiho = this.ykiho ?: "",
        yadmNm = this.hospitalName ?: "병원 정보 없음",
        addr = this.address ?: "주소 정보 없음",

        clCd = this.medicalClassCode,
        clCdNm = this.medicalClassName,
        sidoCd = this.sidoCode,
        sidoCdNm = this.sidoName,
        sgguCd = this.sgguCode,
        sgguCdNm = this.sgguName,
        emdongNm = this.townName,
        postNo = this.postCode,
        telno = this.telephone,
        hospUrl = this.hospitalUrl,
        estbDd = this.establishmentDate,
        drTotCnt = this.doctorCount,
        gdrCnt = this.generalDoctorCount,
        gdrnCnt = this.generalDoctorSpecialistCount,
        sdrnCnt = this.residentSpecialistCount,
        sdrCnt = this.specialistDoctorCount,
        pnursCnt = this.nurseCount,
        resdntCnt = this.residentCount,
        intnCnt = this.internCount,
        XPos = this.longitude?.toString(),
        YPos = this.latitude?.toString(),
        dgidIdName = this.department
    )
}

fun List<HospitalItemDto>.toDomainList(): List<HospitalItem> {
    return this.map { it.toDomain() }
}