package com.iti.weatherapp.presentation.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.iti.weatherapp.presentation.utils.Constants.EXTRA_ALERT_ID
import com.iti.weatherapp.presentation.utils.Constants.EXTRA_ALERT_TYPE
import com.iti.weatherapp.data.worker.WeatherAlarmWorker
import com.iti.weatherapp.presentation.utils.Constants.EXTRA_SOUND_URI

class WeatherAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val alertId = intent.getIntExtra(EXTRA_ALERT_ID, -1)
        val alertTypeStr = intent.getStringExtra(EXTRA_ALERT_TYPE) ?: return
        val soundUriStr = intent.getStringExtra(EXTRA_SOUND_URI)

        val inputData = Data.Builder()
            .putInt(EXTRA_ALERT_ID, alertId)
            .putString(EXTRA_ALERT_TYPE, alertTypeStr)
            .putString(EXTRA_SOUND_URI, soundUriStr)
            .build()

        val workRequest = OneTimeWorkRequestBuilder<WeatherAlarmWorker>()
            .setInputData(inputData)
            .build()

        WorkManager.getInstance(context).enqueue(workRequest)
    }
}