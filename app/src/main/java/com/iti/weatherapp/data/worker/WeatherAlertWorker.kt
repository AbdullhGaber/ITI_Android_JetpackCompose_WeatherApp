package com.iti.weatherapp.data.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.iti.weatherapp.data.local.db.entities.AlertType
import com.iti.weatherapp.data.local.preferences.SettingsPreferences
import com.iti.weatherapp.data.repository.Repository
import com.iti.weatherapp.data.utils.ApiState
import com.iti.weatherapp.presentation.utils.Constants.EXTRA_ALERT_ID
import com.iti.weatherapp.presentation.utils.Constants.EXTRA_ALERT_TYPE
import com.iti.weatherapp.presentation.utils.NotificationHelper
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first

@HiltWorker
class WeatherAlarmWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val repository: Repository,
    private val settingsPreferences: SettingsPreferences
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val alertId = inputData.getInt(EXTRA_ALERT_ID, -1)
        val alertTypeStr = inputData.getString(EXTRA_ALERT_TYPE) ?: return Result.failure()

        if (alertId == -1) return Result.failure()

        val alert = repository.getAlertById(alertId) ?: return Result.success()

        var finalMessage: String

        try {
            val currentCoordinates = settingsPreferences.currentLocationFlow.first()
            val unit = settingsPreferences.tempUnitFlow.first()
            val language = settingsPreferences.languageFlow.first()

            val weatherDataFlow = repository.getWeatherForecast(
                currentCoordinates.first,
                currentCoordinates.second,
                unit,
                language
            )

            val result = weatherDataFlow.first { it !is ApiState.Loading }

            if (result is ApiState.Success) {
                val temp = result.data.forecastList.first().mainMetrics.temp
                val status = result.data.forecastList.first().weatherConditions.first().description
                finalMessage = "Weather Update! $status with Temp: $temp"
            } else {
                throw Exception("API Error")
            }

        } catch (e: Exception) {
            e.printStackTrace()
            finalMessage = "Cannot reach weather service. Please check outside."
        }

        if (alertTypeStr == AlertType.ALARM.name) {
            NotificationHelper.showAlarmNotification(
                context = context,
                alertId = alertId,
                title = "Weather Alarm",
                message = finalMessage,
                isUpdate = true
            )
        } else {
            NotificationHelper.showStandardNotification(
                context = context,
                alertId = alertId,
                title = "Weather Update",
                message = finalMessage,
                isUpdate = true
            )
            repository.deleteWeatherAlert(alert)
        }

        return Result.success()
    }
}