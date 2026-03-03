package com.iti.weatherapp.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weather_alerts")
data class WeatherAlert(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val startDateTimestamp: Long, // Start of the active duration
    val endDateTimestamp: Long,   // End of the active duration
    val alertType: AlertType,     // Enum: NOTIFICATION or ALARM
    val isEnabled: Boolean = true // Option to stop notification or turn off the alarm [cite: 45]
)

enum class AlertType {
    NOTIFICATION,
    ALARM
}