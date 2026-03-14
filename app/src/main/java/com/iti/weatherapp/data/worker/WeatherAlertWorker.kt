package com.iti.weatherapp.data.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.iti.weatherapp.data.local.db.daos.WeatherAlertsDao
import com.iti.weatherapp.data.local.db.entities.AlertType
import com.iti.weatherapp.data.local.preferences.SettingsPreferences
import com.iti.weatherapp.data.repository.Repository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first

@HiltWorker
class WeatherAlertWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val weatherAlertsDao: WeatherAlertsDao,
    private val settingsPreferences: SettingsPreferences,
    private val repository: Repository
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        try {
            val currentTime = System.currentTimeMillis() / 1000

            val alerts = weatherAlertsDao.getAllWeatherAlerts().first()

            val activeAlerts = alerts.filter { 
                it.isEnabled && currentTime in it.startDateTimestamp..it.endDateTimestamp 
            }

            if (activeAlerts.isEmpty()) {
                return Result.success()
            }

             val currentCoordinates = settingsPreferences.customLocationFlow.first()
             val unit = settingsPreferences.tempUnitFlow.first()
             val language = settingsPreferences.languageFlow.first()
             val weatherData = repository.getWeatherForecast(
                 currentCoordinates.first,
                 currentCoordinates.second,
                 unit,
                 language
             )

            val alertMessage = "Heavy rain expected in your area!" 

            activeAlerts.forEach { alert ->
                if (alert.alertType == AlertType.NOTIFICATION) {
//                    NotificationHelper.showNotification(context, "Weather Alert", alertMessage)
                } else if (alert.alertType == AlertType.ALARM) {
                    triggerAlarmActivity(alertMessage)
                }
            }

            return Result.success()
        } catch (e: Exception) {
            return Result.retry()
        }
    }

    private fun triggerAlarmActivity(message: String) {
//        val intent = Intent(context, AlarmActivity::class.java).apply {
//            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
//            putExtra("ALARM_MESSAGE", message)
//        }
//        context.startActivity(intent)
    }
}