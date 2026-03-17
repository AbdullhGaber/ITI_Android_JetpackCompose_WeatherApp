package com.iti.weatherapp.data.local.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weather_alerts")
data class WeatherAlert(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val startDateTimestamp: Long,
    val endDateTimestamp: Long,
    val alertType: AlertType,
    val isEnabled: Boolean = true,
    val tempThreshold: Float = 0.0f,
    val windThreshold: Float = 0.0f,
    val conditions: List<String> = emptyList(),
    val customSoundUri: String? = null,
    val customSoundName: String? = null
)

enum class AlertType {
    NOTIFICATION,
    ALARM
}