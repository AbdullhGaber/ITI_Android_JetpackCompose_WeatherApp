package com.iti.weatherapp.data.repository

import com.iti.weatherapp.data.data_sources.local.LocalDataSource
import com.iti.weatherapp.data.data_sources.remote.RemoteDataSource
import com.iti.weatherapp.data.local.db.entities.FavoriteLocation
import com.iti.weatherapp.data.local.db.entities.WeatherAlert
import com.iti.weatherapp.data.models.ForecastResponse
import com.iti.weatherapp.data.utils.ApiState
import com.iti.weatherapp.data.utils.safeApiCall
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class RepositoryImpl @Inject constructor(
    private val remoteDataSource: RemoteDataSource,
    private val localDataSource: LocalDataSource
) : Repository {

    override fun getWeatherForecast(
        lat: Double, lon: Double, units: String, lang: String
    ): Flow<ApiState<ForecastResponse>> = flow {
        emit(ApiState.Loading)

        val result = safeApiCall {
            remoteDataSource.getWeatherForecast(lat, lon, units, lang)
        }

        emit(result)
    }

    override fun getAllFavoriteLocations() = localDataSource.getAllFavoriteLocations()
    override suspend fun insertFavoriteLocation(location: FavoriteLocation) = localDataSource.insertFavoriteLocation(location)
    override suspend fun deleteFavoriteLocation(location: FavoriteLocation) = localDataSource.deleteFavoriteLocation(location)

    override fun getAllWeatherAlerts() = localDataSource.getAllWeatherAlerts()
    override suspend fun insertWeatherAlert(alert: WeatherAlert): Long = localDataSource.insertWeatherAlert(alert)
    override suspend fun deleteWeatherAlert(alert: WeatherAlert) = localDataSource.deleteWeatherAlert(alert)
    override suspend fun getAlertById(id: Int): WeatherAlert? = localDataSource.getAlertById(id)
    override suspend fun updateWeatherAlert(alert: WeatherAlert) = localDataSource.updateWeatherAlert(alert)
}