package com.iti.weatherapp.data.local.db.type_converter

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.google.gson.reflect.TypeToken
import com.iti.weatherapp.data.local.db.entities.AlertType
import com.iti.weatherapp.data.utils.JsonParser
@ProvidedTypeConverter
class AlertsConverter(
    private var jsonParser : JsonParser
) {
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

    @TypeConverter
    fun fromStringList(list: List<String>?): String {
        return jsonParser.toJson(list, object : TypeToken<List<String>>(){}.type) ?: ""
    }

    @TypeConverter
    fun toStringList(data: String?): List<String> {
        if (data == null) return emptyList()
        val listType = object : TypeToken<List<String>>() {}.type
        return jsonParser.fromJson(data, listType) ?: emptyList()
    }
}