package com.iti.weatherapp.data.network

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService{
    @GET("data/2.5/weather")
    suspend fun testResponse(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double
    ) : Response<WeatherResponse>
}