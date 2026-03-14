package com.iti.weatherapp.presentation.screens.favorites

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.iti.weatherapp.data.local.preferences.SettingsPreferences
import com.iti.weatherapp.data.models.ForecastResponse
import com.iti.weatherapp.data.repository.Repository
import com.iti.weatherapp.data.utils.ApiState
import com.iti.weatherapp.presentation.navigations.FavoriteDetails
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class FavoriteDetailsViewModel @Inject constructor(
    private val settingsPreferences: SettingsPreferences,
    private val repository: Repository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val args = savedStateHandle.toRoute<FavoriteDetails>()
    val cityName = args.cityName

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    private val _error = mutableStateOf<String?>(null)
    val error: State<String?> = _error

    private val _weatherData = mutableStateOf<ForecastResponse?>(null)
    val weatherData: State<ForecastResponse?> = _weatherData

    private val _tempUnit = mutableStateOf("metric")
    val tempUnit: State<String> = _tempUnit

    private val _windUnit = mutableStateOf("meter_sec")
    val windUnit: State<String> = _windUnit


    init {
        fetchFavoriteWeather()
    }

    fun fetchFavoriteWeather() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            _tempUnit.value = settingsPreferences.tempUnitFlow.first()
            _windUnit.value = settingsPreferences.windUnitFlow.first()
            val units = _tempUnit.value
            val language = settingsPreferences.languageFlow.first()
            try {
                repository.getWeatherForecast(
                    lat = args.lat,
                    lon = args.lon,
                    units = units,
                    lang = language
                ).collect { state ->
                    when (state) {
                        is ApiState.Loading -> {
                            _isLoading.value = true
                            _error.value = null
                            _weatherData.value = null
                        }
                        is ApiState.Error -> {
                            _isLoading.value = false
                            _error.value = state.message
                            _weatherData.value = null
                        }
                        is ApiState.Success -> {
                            _isLoading.value = false
                            _error.value = null
                            _weatherData.value = state.data
                        }
                    }
                }
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }
}