package com.iti.weatherapp.data.local.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.iti.weatherapp.data.models.ForecastResponse

@Entity(tableName = "favorite_locations")
data class FavoriteLocation(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val cityName: String,
    val latitude: Double,
    val longitude: Double,
    val cachedWeather: ForecastResponse? = null
)