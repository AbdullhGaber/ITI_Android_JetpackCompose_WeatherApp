package com.iti.weatherapp.presentation.screens.alerts

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iti.weatherapp.data.local.db.entities.WeatherAlert
import com.iti.weatherapp.data.local.preferences.SettingsPreferences
import com.iti.weatherapp.data.repository.Repository
import com.iti.weatherapp.presentation.utils.alarm_notifications.WeatherAlarmScheduler
import com.iti.weatherapp.presentation.utils.oem.MiuiAutoStartStrategy
import com.iti.weatherapp.presentation.utils.oem.MiuiSpecialPermissionStrategy
import com.iti.weatherapp.presentation.utils.oem.OemPermissionManager
import com.iti.weatherapp.presentation.utils.oem.OemPermissionStrategy
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlertsViewModel @Inject constructor(
    private val repository: Repository,
    private val alarmScheduler: WeatherAlarmScheduler,
    private val settingsPreferences: SettingsPreferences,
    private val oemPermissionManager: OemPermissionManager
) : ViewModel() {
    val alertsList: StateFlow<List<WeatherAlert>> = repository.getAllWeatherAlerts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val hasAskedAutoStart : StateFlow<Boolean> = settingsPreferences.hasAskedAutoStartFlow
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    val hasAskedOemPermission : StateFlow<Boolean> = settingsPreferences.hasAskedAutoStartFlow
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    private fun markAutoStartAsAsked() {
        viewModelScope.launch {
            settingsPreferences.saveAutoStartAsked(true)
        }
    }

    private fun markOemPermissionAsAsked() {
        viewModelScope.launch {
            settingsPreferences.saveOemPermissionAsked(true)
        }
    }
    fun getRequiredPermissionStrategy(context: Context): OemPermissionStrategy? {
        return oemPermissionManager.getRequiredPermissionStrategy(
            context = context,
            hasAskedAutoStart = hasAskedAutoStart.value,
            hasAskedLockScreen = hasAskedOemPermission.value
        )
    }

    fun markStrategyAsAsked(strategy: OemPermissionStrategy) {
        oemPermissionManager.markAsAsked(strategy)
        when(strategy){
            is MiuiAutoStartStrategy -> {
                markAutoStartAsAsked()
            }
            is MiuiSpecialPermissionStrategy -> {
                markOemPermissionAsAsked()
            }
        }
    }

    fun saveAlert(alert: WeatherAlert) {
        viewModelScope.launch {
            val newlyGeneratedId = repository.insertWeatherAlert(alert)
            val savedAlertWithRealId = alert.copy(id = newlyGeneratedId.toInt())
            alarmScheduler.schedule(savedAlertWithRealId)
        }
    }

    fun deleteAlert(alert: WeatherAlert) {
        viewModelScope.launch {
            repository.deleteWeatherAlert(alert)
            alarmScheduler.cancel(alert)
        }
    }

    fun updateAlert(alert: WeatherAlert) {
        viewModelScope.launch {
            repository.updateWeatherAlert(alert)
            if (alert.isEnabled) {
                alarmScheduler.schedule(alert)
            } else {
                alarmScheduler.cancel(alert)
            }
        }
    }
}