package com.iti.weatherapp.presentation.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.iti.weatherapp.presentation.utils.AlarmSoundManager
import com.iti.weatherapp.presentation.utils.Constants.ACTION_START_ALARM
import com.iti.weatherapp.presentation.utils.Constants.ACTION_STOP_ALARM
import com.iti.weatherapp.presentation.utils.Constants.ALARM_MESSAGE
import com.iti.weatherapp.presentation.utils.Constants.ALARM_TITLE
import com.iti.weatherapp.presentation.utils.Constants.EXTRA_ALERT_ID
import com.iti.weatherapp.presentation.utils.Constants.EXTRA_SOUND_URI
import com.iti.weatherapp.presentation.utils.NotificationHelper

class WeatherAlarmService : Service() {

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.action

        if (action == ACTION_STOP_ALARM) {
            stopAlarm()
            return START_NOT_STICKY
        }

        if (action == ACTION_START_ALARM) {
            val soundUri = intent.getStringExtra(EXTRA_SOUND_URI)
            val alertId = intent.getIntExtra(EXTRA_ALERT_ID, -1)
            val title = intent.getStringExtra(ALARM_TITLE) ?: "Weather Alarm"
            val message = intent.getStringExtra(ALARM_MESSAGE) ?: "Check the weather!"

            AlarmSoundManager.playSound(this, soundUri, isAlarm = true)

            val notification = NotificationHelper.buildAlarmNotification(this, alertId, title, message)

            startForeground(alertId, notification)
        }

        return START_STICKY
    }

    private fun stopAlarm() {
        AlarmSoundManager.stopSound()
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        AlarmSoundManager.stopSound()
    }
}