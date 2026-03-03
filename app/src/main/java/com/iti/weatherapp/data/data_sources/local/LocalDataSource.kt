package com.iti.weatherapp.data.data_sources.local

import com.iti.weatherapp.data.db.entities.FavoriteLocation
import com.iti.weatherapp.data.db.entities.WeatherAlert
import kotlinx.coroutines.flow.Flow

interface LocalDataSource {
    fun getAllFavoriteLocations(): Flow<List<FavoriteLocation>>
    suspend fun insertFavoriteLocation(location: FavoriteLocation)
    suspend fun deleteFavoriteLocation(location: FavoriteLocation)

    fun getAllWeatherAlerts(): Flow<List<WeatherAlert>>
    suspend fun insertWeatherAlert(alert: WeatherAlert)
    suspend fun deleteWeatherAlert(alert: WeatherAlert)
    suspend fun updateWeatherAlert(alert: WeatherAlert)
}