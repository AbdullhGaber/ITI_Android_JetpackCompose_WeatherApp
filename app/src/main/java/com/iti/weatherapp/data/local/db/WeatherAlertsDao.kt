package com.iti.weatherapp.data.local.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.iti.weatherapp.data.local.db.entities.WeatherAlert
import kotlinx.coroutines.flow.Flow

@Dao
interface WeatherAlertsDao {
    @Query("SELECT * FROM weather_alerts")
    fun getAllWeatherAlerts(): Flow<List<WeatherAlert>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeatherAlert(alert: WeatherAlert)

    @Delete
    suspend fun deleteWeatherAlert(alert: WeatherAlert)

    @Update
    suspend fun updateWeatherAlert(alert: WeatherAlert)
}