package com.iti.weatherapp.presentation.screens.home

import com.iti.weatherapp.data.FakeRepository
import com.iti.weatherapp.data.models.City
import com.iti.weatherapp.data.models.ForecastResponse
import com.iti.weatherapp.data.local.preferences.SettingsPreferences
import com.iti.weatherapp.utils.MainDispatcherRule
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class HomeViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var fakeRepository: FakeRepository
    private lateinit var mockSettingsPreferences: SettingsPreferences
    private lateinit var viewModel: HomeViewModel

    @Before
    fun setUp() {
        fakeRepository = FakeRepository()
        mockSettingsPreferences = mockk()

        // Existing stubs...
        every { mockSettingsPreferences.tempUnitFlow } returns flowOf("celsius")
        every { mockSettingsPreferences.windUnitFlow } returns flowOf("meter_sec")
        every { mockSettingsPreferences.locationMethodFlow } returns flowOf("gps")
        every { mockSettingsPreferences.customMapLocationFlow } returns flowOf(Pair(31.2001, 29.9187))

        every { mockSettingsPreferences.languageFlow } returns flowOf("en")
        coEvery { mockSettingsPreferences.saveCurrentLocation(any(), any()) } just Runs

        viewModel = HomeViewModel(fakeRepository, mockSettingsPreferences)
    }


    @Test
    fun `getWeatherData with successful response updates weatherData state`() = runTest {
        val dummyForecast = ForecastResponse(
            code = "200",
            message = 0,
            count = 1,
            forecastList = emptyList(),
            city = City(id = 1, name = "Alexandria", coordinates = mockk(relaxed = true), timezone = 7200, sunrise = 0L, sunset = 0L)
        )
        fakeRepository.fakeForecastResponse = dummyForecast
        fakeRepository.shouldReturnError = false

        // Act: Trigger the ViewModel function
        viewModel.getWeatherData(31.2001, 29.9187)

        // Assert: Verify the state holds the data and loading is false
        assertNotNull(viewModel.weatherData.value)
        assertEquals("Alexandria", viewModel.weatherData.value?.city?.name)
        assertEquals(false, viewModel.isLoading.value)
        assertEquals(null, viewModel.error.value)
    }

    @Test
    fun `getWeatherData with network failure updates error state`() = runTest {
        // Arrange: Tell the FakeRepository to simulate an exception/error
        fakeRepository.shouldReturnError = true

        // Act
        viewModel.getWeatherData(31.2001, 29.9187)

        // Assert: Verify the state caught the error
        assertEquals(null, viewModel.weatherData.value)
        assertEquals(false, viewModel.isLoading.value)
        assertEquals("Simulated network failure", viewModel.error.value)
    }
}