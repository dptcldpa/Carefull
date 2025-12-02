package com.cases.carefull.data.repository.diagnosis

import android.annotation.SuppressLint
import android.content.Context
import com.cases.carefull.domain.model.CareFullLocation
import com.cases.carefull.domain.repository.diagnosis.CareFullLocationRepository
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import android.location.Location as AndroidLocation

class CareFullLocationRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : CareFullLocationRepository {

    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    @SuppressLint("MissingPermission")
    override suspend fun getLastKnownLocation(): CareFullLocation? {
        return suspendCoroutine { continuation ->
            fusedLocationClient.lastLocation
                .addOnSuccessListener { androidLocation ->
                    continuation.resume(androidLocation?.toDomainModel())
                }
                .addOnFailureListener {
                    continuation.resume(null)
                }
        }
    }

    @SuppressLint("MissingPermission")
    override suspend fun getCurrentLocation(): CareFullLocation? = suspendCoroutine { continuation ->
        try {
            fusedLocationClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                CancellationTokenSource().token
            )
                .addOnSuccessListener { androidLocation ->
                    continuation.resume(androidLocation?.toDomainModel())
                }
                .addOnFailureListener {
                    fusedLocationClient.getCurrentLocation(
                        Priority.PRIORITY_BALANCED_POWER_ACCURACY,
                        CancellationTokenSource().token
                    )
                        .addOnSuccessListener { androidLocation ->
                            continuation.resume(androidLocation?.toDomainModel())
                        }
                        .addOnFailureListener { e ->
                            continuation.resume(null)
                        }
                }
        } catch (e: SecurityException) {
            continuation.resume(null)
        }
    }

    private fun AndroidLocation.toDomainModel(): CareFullLocation {
        return CareFullLocation(
            latitude = this.latitude,
            longitude = this.longitude
        )
    }
}
