package com.iti.weatherapp.data.local.db

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.iti.weatherapp.data.local.db.daos.WeatherAlertsDao
import com.iti.weatherapp.data.local.db.entities.AlertType
import com.iti.weatherapp.data.local.db.entities.WeatherAlert
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class WeatherDaoTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()
    private lateinit var database: WeatherDatabase
    private lateinit var dao: WeatherAlertsDao

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            WeatherDatabase::class.java
        ).allowMainThreadQueries().build()
        
        dao = database.weatherAlertsDao()
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun insertAndRetrieveAlert() = runTest {
        // Arrange
        val alert = WeatherAlert(
            id = 1,
            startDateTimestamp = 1672531200000L,
            endDateTimestamp = 1822931200000L,
            alertType = AlertType.ALARM
        )

        // Act
        dao.insertWeatherAlert(alert)
        val alertsList = dao.getAllWeatherAlerts().first()

        // Assert
        assertTrue(alertsList.contains(alert))
        assertEquals(1, alertsList.size)
    }

    @Test
    fun deleteAlert_removesItFromDatabase() = runTest {
        // Arrange
        val alert = WeatherAlert(id = 1, startDateTimestamp = 1000L,endDateTimestamp = 2000L , alertType = AlertType.NOTIFICATION)

        dao.insertWeatherAlert(alert)
        // Act
        dao.deleteWeatherAlert(alert)
        val alertsList = dao.getAllWeatherAlerts().first()

        // Assert
        assertTrue(alertsList.isEmpty())
    }
}