package com.iti.weatherapp.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.iti.weatherapp.data.db.entities.FavoriteLocation
import com.iti.weatherapp.data.db.entities.WeatherAlert
import com.iti.weatherapp.data.db.type_converter.AlertTypeConverter

@Database(
    entities = [FavoriteLocation::class, WeatherAlert::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(AlertTypeConverter::class)
abstract class WeatherDatabase : RoomDatabase() {
    abstract fun favoriteLocationsDao(): FavoriteLocationsDao
    abstract fun weatherAlertsDao(): WeatherAlertsDao
}