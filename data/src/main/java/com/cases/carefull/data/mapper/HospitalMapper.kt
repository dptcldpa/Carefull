package com.cases.carefull.data.mapper

import android.location.Location
import com.cases.carefull.data.dto.HospitalItemDto
import com.cases.carefull.domain.model.Hospital

fun HospitalItemDto.toDomain(
    currentLat: Double,
    currentLon: Double,
    isExcellent: Boolean,
    departmentQuery: String
): Hospital {

    val hospitalLat = this.latitude
    val hospitalLon = this.longitude

    val distanceInKm = if (hospitalLat != null && hospitalLon != null) {
        calculateDistanceKm(currentLat, currentLon, hospitalLat, hospitalLon)
    } else {
        this.distance
    }

    return Hospital(
        id = this.ykiho ?: "",
        name = this.hospitalName ?: "이름 정보 없음",
        address = this.address ?: "주소 정보 없음",
        tel = this.telephone,
        distance = distanceInKm,
        isExcellent = isExcellent,
        XPos = this.latitude,
        YPos = this.longitude,
        department = departmentQuery
    )
}

private fun calculateDistanceKm(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
    val location1 = Location("CurrentUserLocation")
    location1.latitude = lat1
    location1.longitude = lon1

    val location2 = Location("HospitalLocation")
    location2.latitude = lat2
    location2.longitude = lon2

    val distanceInMeters = location1.distanceTo(location2)

    return Math.round(distanceInMeters / 100.0) / 10.0
}