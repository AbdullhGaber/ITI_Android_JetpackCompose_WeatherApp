package com.iti.weatherapp.data.models

import com.google.gson.annotations.SerializedName

data class ForecastResponse(
    @SerializedName("cod") val code: String = "",
    @SerializedName("message") val message: Int = 0,
    @SerializedName("cnt") val count: Int = 0,
    @SerializedName("list") val forecastList: List<ForecastItem> = emptyList(),
    @SerializedName("city") val city: City? = null
)