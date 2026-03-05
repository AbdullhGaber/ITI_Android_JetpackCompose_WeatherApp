package com.iti.weatherapp.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.iti.weatherapp.data.local.db.daos.FavoriteLocationsDao
import com.iti.weatherapp.data.local.db.daos.WeatherAlertsDao
import com.iti.weatherapp.data.local.db.entities.FavoriteLocation
import com.iti.weatherapp.data.local.db.entities.WeatherAlert
import com.iti.weatherapp.data.local.db.type_converter.AlertTypeConverter

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