package com.iti.weatherapp.data.data_sources.remote

import com.iti.weatherapp.data.models.ForecastResponse
import retrofit2.Response

interface RemoteDataSource {
    suspend fun getFiveDayForecast(
        lat: Double, lon: Double, units: String, lang: String
    ): Response<ForecastResponse>
}