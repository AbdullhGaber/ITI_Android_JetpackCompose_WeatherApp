package com.iti.weatherapp.data.local.db.type_converter

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.google.gson.reflect.TypeToken
import com.iti.weatherapp.data.models.ForecastResponse
import com.iti.weatherapp.data.utils.JsonParser
import kotlin.jvm.java

@ProvidedTypeConverter
class WeatherForecastConverters(
    private var jsonParser : JsonParser
){
    @TypeConverter
    fun fromWeatherResponse(weather: ForecastResponse?): String {
        return jsonParser.toJson(
            weather ?: ForecastResponse(),
            object : TypeToken<ForecastResponse>(){}.type
        ) ?: "[]"
    }

    @TypeConverter
    fun toWeatherResponse(weatherString: String?): ForecastResponse {
        return jsonParser.fromJson(weatherString ?: "", ForecastResponse::class.java) ?: ForecastResponse()
    }
}