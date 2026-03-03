package com.iti.weatherapp.data.models

import com.google.gson.annotations.SerializedName

data class ForecastItem(
    @SerializedName("dt") val timestamp: Long,
    @SerializedName("main") val mainMetrics: MainMetrics,
    @SerializedName("weather") val weatherConditions: List<WeatherCondition>,
    @SerializedName("clouds") val clouds: Clouds,
    @SerializedName("wind") val wind: Wind,
    @SerializedName("dt_txt") val dateText: String // e.g., "2026-03-03 12:00:00"
)