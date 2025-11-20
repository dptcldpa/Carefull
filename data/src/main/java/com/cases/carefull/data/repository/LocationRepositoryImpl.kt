package com.cases.carefull.data.repository

import android.annotation.SuppressLint
import android.content.Context
import com.cases.carefull.domain.model.Location
import com.cases.carefull.domain.repository.LocationRepository
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import android.location.Location as AndroidLocation

class LocationRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : LocationRepository {

    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    @SuppressLint("MissingPermission")
    override suspend fun getLastKnownLocation(): Location? {
        return suspendCoroutine { continuation ->
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                val androidLocation: AndroidLocation? = location
                continuation.resume(androidLocation?.toDomain())
            }.addOnFailureListener {
                continuation.resume(null)
            }
        }
    }
}

private fun AndroidLocation.toDomain(): Location {
    return Location(
        latitude = this.latitude,
        longitude = this.longitude
    )
}