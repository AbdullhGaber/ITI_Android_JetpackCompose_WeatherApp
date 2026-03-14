package com.iti.weatherapp.presentation.screens.home

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iti.weatherapp.data.local.preferences.SettingsPreferences
import com.iti.weatherapp.data.models.ForecastResponse
import com.iti.weatherapp.data.repository.Repository
import com.iti.weatherapp.data.utils.ApiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: Repository,
    private val settingsPreferences: SettingsPreferences
) : ViewModel() {

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    private val _error = mutableStateOf<String?>(null)
    val error: State<String?> = _error

    private val _weatherData = mutableStateOf<ForecastResponse?>(null)
    val weatherData: State<ForecastResponse?> = _weatherData

    val locationMethod = settingsPreferences.locationMethodFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "gps")

    val customLocation = settingsPreferences.customLocationFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Pair(31.2001, 29.9187))

    private val _tempUnit = mutableStateOf("metric")
    val tempUnit: State<String> = _tempUnit
    
    private val _windUnit = mutableStateOf("meter_sec")
    val windUnit: State<String> = _windUnit

    fun getWeatherData(lat: Double, lon: Double) {
        _isLoading.value = true
        viewModelScope.launch {
            _tempUnit.value = settingsPreferences.tempUnitFlow.first()
            _windUnit.value = settingsPreferences.windUnitFlow.first()
            val units = _tempUnit.value
            val language = settingsPreferences.languageFlow.first()

            repository.getWeatherForecast(lat, lon, units, language).collect { state ->
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
                        settingsPreferences.saveCustomLocation(lat,lon)
                    }
                }
            }
        }
    }
}