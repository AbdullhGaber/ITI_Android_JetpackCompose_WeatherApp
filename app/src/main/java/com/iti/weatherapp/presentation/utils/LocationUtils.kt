package com.iti.weatherapp.presentation.utils

import android.annotation.SuppressLint
import android.location.Location
import android.os.Looper
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.coroutines.resume

object LocationUtils {
    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocation(
        fusedLocationClient: FusedLocationProviderClient
    ): Location? {
        // Automatically timeout after 10 seconds if GPS is stuck
        return withTimeoutOrNull(10000L) {
            suspendCancellableCoroutine { continuation ->
                val locationRequest = LocationRequest.Builder(Priority.PRIORITY_BALANCED_POWER_ACCURACY, 1000)
                    .setWaitForAccurateLocation(false)
                    .build()

                val locationCallback = object : LocationCallback() {
                    override fun onLocationResult(result: LocationResult) {
                        result.lastLocation?.let { location ->
                            // Once we get a location, stop listening and resume the coroutine
                            fusedLocationClient.removeLocationUpdates(this)
                            if (continuation.isActive) {
                                continuation.resume(location)
                            }
                        }
                    }
                }

                // Start the request
                fusedLocationClient.requestLocationUpdates(
                    locationRequest,
                    locationCallback,
                    Looper.getMainLooper()
                ).addOnFailureListener {
                    if (continuation.isActive) {
                        continuation.resume(null)
                    }
                }

                // Cleanup if the coroutine gets cancelled
                continuation.invokeOnCancellation {
                    fusedLocationClient.removeLocationUpdates(locationCallback)
                }
            }
        }
    }
}