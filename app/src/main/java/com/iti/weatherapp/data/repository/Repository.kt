package com.iti.weatherapp.data.repository

import com.iti.weatherapp.data.local.db.entities.FavoriteLocation
import com.iti.weatherapp.data.local.db.entities.WeatherAlert
import com.iti.weatherapp.data.models.ForecastResponse
import com.iti.weatherapp.data.utils.ApiState
import kotlinx.coroutines.flow.Flow

interface Repository {
    // Remote
    fun getFiveDayForecast(
        lat: Double, lon: Double, units: String, lang: String
    ): Flow<ApiState<ForecastResponse>>

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