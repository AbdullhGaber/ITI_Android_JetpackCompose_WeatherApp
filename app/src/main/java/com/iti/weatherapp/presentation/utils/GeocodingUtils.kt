package com.example.weather_app.presentation.components.map

import android.content.Context
import android.location.Geocoder
import android.os.Build
import com.iti.weatherapp.presentation.screens.map.PickedLocation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

suspend fun geocodeCity(context: Context, query: String): PickedLocation? =
    withContext(Dispatchers.IO) {
        try {
            val geocoder = Geocoder(context)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                suspendCoroutine { cont ->
                    geocoder.getFromLocationName(query, 1) { addresses ->
                        val first = addresses.firstOrNull()
                        cont.resume(
                            if (first != null)
                                PickedLocation(
                                    first.latitude,
                                    first.longitude,
                                    first.locality ?: query,
                                    cityName = first.countryCode
                                )
                            else null
                        )
                    }
                }
            } else {
                @Suppress("DEPRECATION")
                val first = geocoder.getFromLocationName(query, 1)?.firstOrNull()
                if (first != null)
                    PickedLocation(
                        first.latitude,
                        first.longitude,
                        first.countryCode ?: query,
                        cityName = first.locality
                    )
                else null
            }
        } catch (e: Exception) {
            null
        }
    }

suspend fun reverseGeocodeLatLng(
    context: Context,
    lat: Double,
    lon: Double,
): String? = withContext(Dispatchers.IO) {
    try {
        val geocoder = Geocoder(context)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            suspendCoroutine { cont ->
                geocoder.getFromLocation(lat, lon, 1) { addresses ->
                    cont.resume(addresses.firstOrNull()?.locality)
                }
            }
        } else {
            @Suppress("DEPRECATION")
            geocoder.getFromLocation(lat, lon, 1)?.firstOrNull()?.locality
        }
    } catch (e: Exception) {
        null
    }
}
