package com.iti.weatherapp.data

import com.iti.weatherapp.data.local.db.entities.FavoriteLocation
import com.iti.weatherapp.data.local.db.entities.WeatherAlert
import com.iti.weatherapp.data.models.ForecastResponse
import com.iti.weatherapp.data.repository.Repository
import com.iti.weatherapp.data.utils.ApiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeRepository : Repository {
    var shouldReturnError = false
    var fakeForecastResponse: ForecastResponse? = null
    private val alertsList = mutableListOf<WeatherAlert>()

    override fun getWeatherForecast(
        lat: Double,
        lon: Double,
        units: String,
        lang: String
    ): Flow<ApiState<ForecastResponse>> = flow {
        emit(ApiState.Loading)
        
        if (shouldReturnError) {
            emit(ApiState.Error("Simulated network failure"))
        } else {
            if (fakeForecastResponse != null) {
                emit(ApiState.Success(fakeForecastResponse!!))
            } else {
                emit(ApiState.Error("No fake data provided for test"))
            }
        }
    }

    override fun getAllFavoriteLocations(): Flow<List<FavoriteLocation>> {
        TODO("Not yet implemented")
    }

    override suspend fun insertFavoriteLocation(location: FavoriteLocation) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteFavoriteLocation(location: FavoriteLocation) {
        TODO("Not yet implemented")
    }

    override fun getAllWeatherAlerts(): Flow<List<WeatherAlert>> {
        TODO("Not yet implemented")
    }

    override suspend fun getAlertById(id: Int): WeatherAlert? {
        return alertsList.find { it.id == id }
    }

    override suspend fun updateWeatherAlert(alert: WeatherAlert) {
        TODO("Not yet implemented")
    }

    override suspend fun insertWeatherAlert(alert: WeatherAlert): Long {
        alertsList.add(alert)
        return alert.id.toLong()
    }

    override suspend fun deleteWeatherAlert(alert: WeatherAlert) {
        alertsList.remove(alert)
    }

}