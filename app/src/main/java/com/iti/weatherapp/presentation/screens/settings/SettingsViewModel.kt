package com.iti.weatherapp.presentation.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iti.weatherapp.data.local.preferences.SettingsPreferences
import com.iti.weatherapp.presentation.ui.theme.AppTheme
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsPreferences: SettingsPreferences
) : ViewModel() {

    val locationMethodState: StateFlow<String> = settingsPreferences.locationMethodFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "gps")

    val tempUnitState: StateFlow<String> = settingsPreferences.tempUnitFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "metric")

    val windUnitState: StateFlow<String> = settingsPreferences.windUnitFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "meter_sec")

    val themeState: StateFlow<AppTheme> = settingsPreferences.themeFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AppTheme.SYSTEM_DEFAULT)

    val languageState: StateFlow<String> = settingsPreferences.languageFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "System Language")


    val customLocationState: StateFlow<Pair<Double, Double>> = settingsPreferences.customLocationFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Pair(31.2001, 29.9187))


    fun saveCustomLocation(lat: Double, lng: Double) = viewModelScope.launch {
        settingsPreferences.saveCustomLocation(lat, lng)
    }
    fun setLocationMethod(method: String) = viewModelScope.launch { settingsPreferences.saveLocationMethod(method) }
    fun setTempUnit(unit: String) = viewModelScope.launch { settingsPreferences.saveTempUnit(unit) }
    fun setWindUnit(unit: String) = viewModelScope.launch { settingsPreferences.saveWindUnit(unit) }
    fun setTheme(theme: AppTheme) = viewModelScope.launch { settingsPreferences.saveTheme(theme) }
    fun setLanguage(lang: String) = viewModelScope.launch { settingsPreferences.saveLanguage(lang) }
}