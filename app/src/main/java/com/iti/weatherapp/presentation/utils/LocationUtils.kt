package com.iti.weatherapp.presentation.utils

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.os.Looper
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeoutOrNull
import java.util.Locale
import kotlin.coroutines.resume

object LocationUtils {
    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocation(
        fusedLocationClient: FusedLocationProviderClient
    ): Location? {
        val lastLocation = suspendCancellableCoroutine<Location?> { cont ->
            fusedLocationClient.lastLocation.addOnCompleteListener { task ->
                if (task.isSuccessful && task.result != null) {
                    cont.resume(task.result)
                } else {
                    cont.resume(null)
                }
            }
        }
        if (lastLocation != null) return lastLocation

        return withTimeoutOrNull(10000L) {
            suspendCancellableCoroutine { continuation ->
                val locationRequest = LocationRequest.Builder(Priority.PRIORITY_BALANCED_POWER_ACCURACY, 1000)
                    .setWaitForAccurateLocation(false)
                    .build()

                val locationCallback = object : LocationCallback() {
                    override fun onLocationResult(result: LocationResult) {
                        result.lastLocation?.let { location ->
                            fusedLocationClient.removeLocationUpdates(this)
                            if (continuation.isActive) {
                                continuation.resume(location)
                            }
                        }
                    }
                }

                fusedLocationClient.requestLocationUpdates(
                    locationRequest,
                    locationCallback,
                    Looper.getMainLooper()
                ).addOnFailureListener {
                    if (continuation.isActive) {
                        continuation.resume(null)
                    }
                }

                continuation.invokeOnCancellation {
                    fusedLocationClient.removeLocationUpdates(locationCallback)
                }
            }
        }
    }

    fun getCityNameFromCoordinates(
        context: Context,
        lat: Double,
        lon: Double,
        onSuccess : (String) -> Unit
    ) {
        try {
            val geocoder = Geocoder(context, Locale.getDefault())
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
                @Suppress("DEPRECATION")
                val addresses = geocoder.getFromLocation(lat, lon, 1)
                if (!addresses.isNullOrEmpty()) {
                    val address = addresses[0]
                    val name = address.locality ?: address.subAdminArea ?: address.adminArea ?: "Unknown Location"
                    onSuccess(name)
                } else {
                    onSuccess("Unknown Location")
                }
            }else{
                geocoder.getFromLocation(lat, lon, 1){addresses ->
                    if (addresses.isNotEmpty()) {
                        val address = addresses[0]
                        val name = address.locality ?: address.subAdminArea ?: address.adminArea ?: "Unknown Location"
                        onSuccess(name)
                    } else {
                        onSuccess("Unknown Location")
                    }
                }
            }
        } catch (e: Exception) {
            onSuccess("Unknown Location")
        }
    }
}