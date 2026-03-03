package com.iti.weatherapp.data.repository

import com.iti.weatherapp.data.db.entities.FavoriteLocation
import com.iti.weatherapp.data.db.entities.WeatherAlert
import com.iti.weatherapp.data.models.ForecastResponse
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

interface Repository {
    // Remote
    suspend fun getFiveDayForecast(lat: Double, lon: Double, units: String, lang: String): Response<ForecastResponse>

    // Local - Favorites
    fun getAllFavoriteLocations(): Flow<List<FavoriteLocation>>
    suspend fun insertFavoriteLocation(location: FavoriteLocation)
    suspend fun deleteFavoriteLocation(location: FavoriteLocation)

    // Local - Alerts
    fun getAllWeatherAlerts(): Flow<List<WeatherAlert>>
    suspend fun insertWeatherAlert(alert: WeatherAlert)
    suspend fun deleteWeatherAlert(alert: WeatherAlert)
    suspend fun updateWeatherAlert(alert: WeatherAlert)
}