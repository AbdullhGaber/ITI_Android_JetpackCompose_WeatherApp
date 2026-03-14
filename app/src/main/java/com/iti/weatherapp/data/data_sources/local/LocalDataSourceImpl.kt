package com.iti.weatherapp.data.data_sources.local

import com.iti.weatherapp.data.local.db.daos.FavoriteLocationsDao
import com.iti.weatherapp.data.local.db.daos.WeatherAlertsDao
import com.iti.weatherapp.data.local.db.entities.FavoriteLocation
import com.iti.weatherapp.data.local.db.entities.WeatherAlert
import javax.inject.Inject

class LocalDataSourceImpl @Inject constructor(
    private val favoriteLocationsDao: FavoriteLocationsDao,
    private val weatherAlertsDao: WeatherAlertsDao
) : LocalDataSource {

    override fun getAllFavoriteLocations() = favoriteLocationsDao.getAllFavoriteLocations()
    override suspend fun insertFavoriteLocation(location: FavoriteLocation) = favoriteLocationsDao.insertFavoriteLocation(location)
    override suspend fun deleteFavoriteLocation(location: FavoriteLocation) = favoriteLocationsDao.deleteFavoriteLocation(location)

    override fun getAllWeatherAlerts() = weatherAlertsDao.getAllWeatherAlerts()
    override suspend fun insertWeatherAlert(alert: WeatherAlert) : Long = weatherAlertsDao.insertWeatherAlert(alert)
    override suspend fun deleteWeatherAlert(alert: WeatherAlert) = weatherAlertsDao.deleteWeatherAlert(alert)
    override suspend fun getAlertById(id: Int): WeatherAlert? = weatherAlertsDao.getAlertById(id)
    override suspend fun updateWeatherAlert(alert: WeatherAlert) = weatherAlertsDao.updateWeatherAlert(alert)
}