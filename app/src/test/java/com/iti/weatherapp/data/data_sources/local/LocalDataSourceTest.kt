package com.iti.weatherapp.data.data_sources.local

import com.iti.weatherapp.data.local.db.daos.FavoriteLocationsDao
import com.iti.weatherapp.data.local.db.daos.WeatherAlertsDao
import com.iti.weatherapp.data.local.db.entities.FavoriteLocation
import com.iti.weatherapp.data.local.db.entities.WeatherAlert
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class LocalDataSourceTest {
    private lateinit var localDataSource: LocalDataSourceImpl
    private val favoriteLocationsDao: FavoriteLocationsDao = mockk(relaxed = true)
    private val weatherAlertsDao: WeatherAlertsDao = mockk(relaxed = true)

    @Before
    fun setup() {
        localDataSource = LocalDataSourceImpl(favoriteLocationsDao, weatherAlertsDao)
    }

    @Test
    fun `getAllFavoriteLocations calls dao`() {
        val mockFlow = flowOf(listOf<FavoriteLocation>())
        every { favoriteLocationsDao.getAllFavoriteLocations() } returns mockFlow

        val result = localDataSource.getAllFavoriteLocations()

        assertEquals(mockFlow, result)
        coVerify(exactly = 1) { favoriteLocationsDao.getAllFavoriteLocations() }
    }

    @Test
    fun `insertFavoriteLocation calls dao`() = runTest {
        val location = mockk<FavoriteLocation>()
        localDataSource.insertFavoriteLocation(location)
        coVerify(exactly = 1) { favoriteLocationsDao.insertFavoriteLocation(location) }
    }

    @Test
    fun `deleteFavoriteLocation calls dao`() = runTest {
        val location = mockk<FavoriteLocation>()
        localDataSource.deleteFavoriteLocation(location)
        coVerify(exactly = 1) { favoriteLocationsDao.deleteFavoriteLocation(location) }
    }

    @Test
    fun `getAllWeatherAlerts calls dao`() {
        val mockFlow = flowOf(listOf<WeatherAlert>())
        every { weatherAlertsDao.getAllWeatherAlerts() } returns mockFlow

        val result = localDataSource.getAllWeatherAlerts()

        assertEquals(mockFlow, result)
        coVerify(exactly = 1) { weatherAlertsDao.getAllWeatherAlerts() }
    }

    @Test
    fun `insertWeatherAlert calls dao and returns id`() = runTest {
        val alert = mockk<WeatherAlert>()
        val expectedId = 1L
        coEvery { weatherAlertsDao.insertWeatherAlert(alert) } returns expectedId

        val result = localDataSource.insertWeatherAlert(alert)

        assertEquals(expectedId, result)
        coVerify(exactly = 1) { weatherAlertsDao.insertWeatherAlert(alert) }
    }

    @Test
    fun `deleteWeatherAlert calls dao`() = runTest {
        val alert = mockk<WeatherAlert>()
        localDataSource.deleteWeatherAlert(alert)
        coVerify(exactly = 1) { weatherAlertsDao.deleteWeatherAlert(alert) }
    }

    @Test
    fun `getAlertById calls dao and returns alert`() = runTest {
        val alert = mockk<WeatherAlert>()
        coEvery { weatherAlertsDao.getAlertById(1) } returns alert

        val result = localDataSource.getAlertById(1)

        assertEquals(alert, result)
        coVerify(exactly = 1) { weatherAlertsDao.getAlertById(1) }
    }

    @Test
    fun `updateWeatherAlert calls dao`() = runTest {
        val alert = mockk<WeatherAlert>()
        localDataSource.updateWeatherAlert(alert)
        coVerify(exactly = 1) { weatherAlertsDao.updateWeatherAlert(alert) }
    }
}