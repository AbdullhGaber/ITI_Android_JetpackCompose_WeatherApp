package com.iti.weatherapp.data.repository

import com.iti.weatherapp.data.data_sources.local.LocalDataSource
import com.iti.weatherapp.data.data_sources.remote.RemoteDataSource
import com.iti.weatherapp.data.db.entities.FavoriteLocation
import com.iti.weatherapp.data.db.entities.WeatherAlert
import javax.inject.Inject

class RepositoryImpl @Inject constructor(
    private val remoteDataSource: RemoteDataSource,
    private val localDataSource: LocalDataSource
) : Repository {

    override suspend fun getFiveDayForecast(lat: Double, lon: Double, units: String, lang: String) =
        remoteDataSource.getFiveDayForecast(lat, lon, units, lang)

    override fun getAllFavoriteLocations() = localDataSource.getAllFavoriteLocations()
    override suspend fun insertFavoriteLocation(location: FavoriteLocation) = localDataSource.insertFavoriteLocation(location)
    override suspend fun deleteFavoriteLocation(location: FavoriteLocation) = localDataSource.deleteFavoriteLocation(location)

    override fun getAllWeatherAlerts() = localDataSource.getAllWeatherAlerts()
    override suspend fun insertWeatherAlert(alert: WeatherAlert) = localDataSource.insertWeatherAlert(alert)
    override suspend fun deleteWeatherAlert(alert: WeatherAlert) = localDataSource.deleteWeatherAlert(alert)
    override suspend fun updateWeatherAlert(alert: WeatherAlert) = localDataSource.updateWeatherAlert(alert)
}