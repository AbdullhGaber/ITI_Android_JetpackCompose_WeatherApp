package com.iti.weatherapp.presentation.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.iti.weatherapp.data.local.db.entities.AlertType
import com.iti.weatherapp.presentation.utils.AlarmSoundManager
import com.iti.weatherapp.presentation.utils.Constants.EXTRA_ALERT_ID
import com.iti.weatherapp.presentation.utils.Constants.EXTRA_ALERT_TYPE
import com.iti.weatherapp.presentation.utils.NotificationHelper
import com.iti.weatherapp.data.worker.WeatherAlarmWorker

class WeatherAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val alertId = intent.getIntExtra(EXTRA_ALERT_ID, -1)
        val alertTypeStr = intent.getStringExtra(EXTRA_ALERT_TYPE) ?: return

        // --- STEP 1: INSTANT UI TRIGGER ---
        // This runs instantly while Android still gives us permission to pin the notification!
        NotificationHelper.createNotificationChannels(context)
        val loadingMessage = "Fetching latest weather data..."

        if (alertTypeStr == AlertType.ALARM.name) {
            AlarmSoundManager.playAlarm(context) // Here is the sound!
            NotificationHelper.showAlarmNotification(context, alertId, "Weather Alarm", loadingMessage)
        } else {
            NotificationHelper.showStandardNotification(context, alertId, "Weather Update", loadingMessage)
        }
        // ----------------------------------

        // --- STEP 2: WORKER HANDOFF ---
        // Now we let the Worker fetch the network data and update the text silently.
        val inputData = Data.Builder()
            .putInt(EXTRA_ALERT_ID, alertId)
            .putString(EXTRA_ALERT_TYPE, alertTypeStr)
            .build()

        val workRequest = OneTimeWorkRequestBuilder<WeatherAlarmWorker>()
            .setInputData(inputData)
            .build()

        WorkManager.getInstance(context).enqueue(workRequest)
    }
}