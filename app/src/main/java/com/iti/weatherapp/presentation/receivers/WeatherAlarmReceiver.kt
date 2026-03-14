package com.iti.weatherapp.presentation.receivers

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import com.iti.weatherapp.data.local.db.entities.AlertType
import com.iti.weatherapp.data.local.preferences.SettingsPreferences
import com.iti.weatherapp.data.repository.Repository
import com.iti.weatherapp.presentation.utils.AlarmSoundManager
import com.iti.weatherapp.presentation.utils.Constants.EXTRA_ALERT_ID
import com.iti.weatherapp.presentation.utils.Constants.EXTRA_ALERT_TYPE
import com.iti.weatherapp.presentation.utils.NotificationHelper
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class WeatherAlarmReceiver : BroadcastReceiver() {

    @Inject lateinit var repository: Repository
    @Inject lateinit var settingsPreferences: SettingsPreferences

    override fun onReceive(context: Context, intent: Intent) {
        val pendingResult = goAsync()
        val alertId = intent.getIntExtra(EXTRA_ALERT_ID, -1)
        val alertTypeStr = intent.getStringExtra(EXTRA_ALERT_TYPE) ?: return

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val alert = repository.getAlertById(alertId) ?: return@launch

                val currentCoordinates = settingsPreferences.currentLocationFlow.first()
                val unit = settingsPreferences.tempUnitFlow.first()
                val language = settingsPreferences.languageFlow.first()

                val weatherData = repository.getWeatherForecast(
                    currentCoordinates.first,
                    currentCoordinates.second,
                    unit,
                    language
                )

                val message = "Heavy rain expected in your area!"
                NotificationHelper.createNotificationChannels(context)

                if (alertTypeStr == AlertType.ALARM.name) {
                    AlarmSoundManager.playAlarm(context)
                    NotificationHelper.showAlarmNotification(context, alertId, "Weather Alarm", message)
                } else {
                    NotificationHelper.showStandardNotification(context, alertId, "Weather Update", message)

                    val currentTimeMillis = System.currentTimeMillis()
                    val nextTriggerMillis = currentTimeMillis + (12 * 60 * 60 * 1000)
                    if (nextTriggerMillis <= (alert.endDateTimestamp * 1000)) {
                        rescheduleNotification(context, alertId, alertTypeStr, nextTriggerMillis)
                    }

                    repository.deleteWeatherAlert(alert)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                pendingResult.finish()
            }
        }
    }

    private fun rescheduleNotification(context: Context, alertId: Int, alertTypeStr: String, triggerTimeMillis: Long) {
        val alarmManager = context.getSystemService(AlarmManager::class.java)

        val nextIntent = Intent(context, WeatherAlarmReceiver::class.java).apply {
            putExtra(EXTRA_ALERT_ID, alertId)
            putExtra(EXTRA_ALERT_TYPE, alertTypeStr)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alertId,
            nextIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && alarmManager.canScheduleExactAlarms()) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTimeMillis, pendingIntent)
            } else {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTimeMillis, pendingIntent)
            }
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }
}