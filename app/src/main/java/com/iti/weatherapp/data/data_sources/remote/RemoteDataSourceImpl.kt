package com.iti.weatherapp.data.data_sources.remote

import com.iti.weatherapp.data.models.ForecastResponse
import com.iti.weatherapp.data.network.WeatherApiService
import retrofit2.Response
import javax.inject.Inject

class RemoteDataSourceImpl @Inject constructor(
    private val apiService: WeatherApiService
) : RemoteDataSource {

    override suspend fun getFiveDayForecast(
        lat: Double, lon: Double, units: String, lang: String
    ): Response<ForecastResponse> {
        return apiService.getFiveDayForecast(lat, lon, units, lang)
    }
}