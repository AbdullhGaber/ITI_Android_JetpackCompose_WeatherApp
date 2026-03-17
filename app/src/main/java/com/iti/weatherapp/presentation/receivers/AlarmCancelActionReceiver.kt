package com.iti.weatherapp.presentation.receivers

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.iti.weatherapp.data.repository.Repository
import com.iti.weatherapp.presentation.utils.AlarmSoundManager
import com.iti.weatherapp.presentation.utils.Constants
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class AlarmCancelActionReceiver : BroadcastReceiver() {
    @Inject lateinit var repository: Repository

    override fun onReceive(context: Context, intent: Intent) {
        val alertId = intent.getIntExtra(Constants.EXTRA_ALERT_ID, -1)
        if (alertId == -1) return

        AlarmSoundManager.stopSound()

        val notificationManager = context.getSystemService(NotificationManager::class.java)

        notificationManager.cancel(alertId)

        CoroutineScope(Dispatchers.IO).launch {
            val alert = repository.getAlertById(alertId)
            if (alert != null) {
                repository.deleteWeatherAlert(alert)
            }
        }
    }
}