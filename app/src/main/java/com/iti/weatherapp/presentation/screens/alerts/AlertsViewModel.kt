package com.iti.weatherapp.presentation.screens.alerts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iti.weatherapp.data.local.db.entities.WeatherAlert
import com.iti.weatherapp.data.repository.Repository
import com.iti.weatherapp.presentation.utils.WeatherAlarmScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlertsViewModel @Inject constructor(
    private val repository: Repository,
    private val alarmScheduler: WeatherAlarmScheduler
) : ViewModel() {

    val alertsList: StateFlow<List<WeatherAlert>> = repository.getAllWeatherAlerts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

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