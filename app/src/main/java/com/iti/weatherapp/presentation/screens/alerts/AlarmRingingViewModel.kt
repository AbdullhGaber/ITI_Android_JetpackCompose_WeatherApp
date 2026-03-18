package com.iti.weatherapp.presentation.screens.alerts

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iti.weatherapp.data.repository.Repository
import com.iti.weatherapp.presentation.services.WeatherAlarmService
import com.iti.weatherapp.presentation.utils.Constants.ACTION_STOP_ALARM
import com.iti.weatherapp.presentation.utils.alarm_notifications.WeatherAlarmScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlarmRingingViewModel @Inject constructor(
    private val repository: Repository,
    private val alarmScheduler: WeatherAlarmScheduler,
) : ViewModel() {

    fun dismissAlarm(context: Context) {
        stopService(context)
    }

    fun snoozeAlarm(context: Context, alertId: Int) {
        stopService(context)
        viewModelScope.launch {
            val alert = repository.getAlertById(alertId)
            if (alert != null) {
                val newStartTime = (System.currentTimeMillis() / 1000) + (10 * 60)
                val snoozedAlert = alert.copy(startDateTimestamp = newStartTime)
                
                repository.updateWeatherAlert(snoozedAlert)
                alarmScheduler.schedule(snoozedAlert)
            }
        }
    }

    private fun stopService(context : Context) {
        val stopIntent = Intent(context, WeatherAlarmService::class.java).apply {
            action = ACTION_STOP_ALARM
        }
        context.startService(stopIntent)
    }
}