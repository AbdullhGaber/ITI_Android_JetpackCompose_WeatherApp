package com.iti.weatherapp.data.local.db.type_converter

import androidx.room.TypeConverter
import com.iti.weatherapp.data.local.db.entities.AlertType

class AlertTypeConverter {
    @TypeConverter
    fun fromAlertType(alertType: AlertType): String {
        return alertType.name
    }

    @TypeConverter
    fun toAlertType(alertTypeString: String): AlertType {
        return try {
            AlertType.valueOf(alertTypeString)
        } catch (e: IllegalArgumentException) {
            AlertType.NOTIFICATION 
        }
    }
}