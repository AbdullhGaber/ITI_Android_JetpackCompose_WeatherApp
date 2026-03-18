package com.iti.weatherapp.presentation.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.iti.weatherapp.data.repository.Repository
import com.iti.weatherapp.presentation.services.WeatherAlarmService
import com.iti.weatherapp.presentation.utils.Constants.ACTION_STOP_ALARM
import com.iti.weatherapp.presentation.utils.Constants.EXTRA_ALERT_ID
import com.iti.weatherapp.presentation.utils.alarm_notifications.WeatherAlarmScheduler
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class AlarmSnoozeActionReceiver : BroadcastReceiver() {

    @Inject
    lateinit var repository: Repository

    @Inject
    lateinit var alarmScheduler: WeatherAlarmScheduler

    override fun onReceive(context: Context, intent: Intent) {
        val alertId = intent.getIntExtra(EXTRA_ALERT_ID, -1)
        if (alertId == -1) return

        val stopIntent = Intent(context, WeatherAlarmService::class.java).apply {
            action = ACTION_STOP_ALARM
        }
        context.startService(stopIntent)

        CoroutineScope(Dispatchers.IO).launch {
            val alert = repository.getAlertById(alertId)

            if (alert != null) {
                val newStartTime = (System.currentTimeMillis() / 1000) + (10 * 60)

                val snoozedAlert = alert.copy(startDateTimestamp = newStartTime)

                repository.updateWeatherAlert(snoozedAlert)

                alarmScheduler.schedule(snoozedAlert)
            }
        }
    }
}