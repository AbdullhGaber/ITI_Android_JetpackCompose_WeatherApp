package com.iti.weatherapp.presentation.utils

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.iti.weatherapp.data.local.db.entities.WeatherAlert
import com.iti.weatherapp.presentation.receivers.WeatherAlarmReceiver
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class WeatherAlarmScheduler @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val alarmManager = context.getSystemService(AlarmManager::class.java)
    fun schedule(alert: WeatherAlert) {
        val intent = Intent(context, WeatherAlarmReceiver::class.java).apply {
            putExtra(Constants.EXTRA_ALERT_TYPE, alert.alertType.name)
            putExtra(Constants.EXTRA_ALERT_ID, alert.id)
            putExtra(Constants.EXTRA_SOUND_URI, alert.customSoundUri)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alert.startDateTimestamp.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val triggerTimeMillis = alert.startDateTimestamp * 1000

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTimeMillis, pendingIntent)
                }
            } else {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTimeMillis, pendingIntent)
            }
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }

    fun cancel(alert: WeatherAlert) {
        val intent = Intent(context, WeatherAlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alert.startDateTimestamp.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }
}