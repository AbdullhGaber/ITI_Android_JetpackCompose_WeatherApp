package com.iti.weatherapp.data.worker

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.iti.weatherapp.R
import com.iti.weatherapp.data.local.db.entities.AlertType
import com.iti.weatherapp.data.local.preferences.SettingsPreferences
import com.iti.weatherapp.data.repository.Repository
import com.iti.weatherapp.data.utils.ApiState
import com.iti.weatherapp.presentation.services.WeatherAlarmService
import com.iti.weatherapp.presentation.utils.AlarmSoundManager
import com.iti.weatherapp.presentation.utils.Constants.ACTION_START_ALARM
import com.iti.weatherapp.presentation.utils.Constants.ALARM_MESSAGE
import com.iti.weatherapp.presentation.utils.Constants.ALARM_TITLE
import com.iti.weatherapp.presentation.utils.Constants.EXTRA_ALERT_ID
import com.iti.weatherapp.presentation.utils.Constants.EXTRA_ALERT_TYPE
import com.iti.weatherapp.presentation.utils.Constants.EXTRA_SOUND_URI
import com.iti.weatherapp.presentation.utils.NotificationHelper
import com.iti.weatherapp.presentation.utils.WeatherFormatters
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
        val soundUriStr = inputData.getString(EXTRA_SOUND_URI)

        if (alertId == -1) return Result.failure()

        val alert = repository.getAlertById(alertId) ?: return Result.failure()

        try {
            val currentCoordinates = settingsPreferences.currentLocationFlow.first()
            val unit = settingsPreferences.tempUnitFlow.first()
            val language = settingsPreferences.languageFlow.first()

            val weatherState = repository.getWeatherForecast(
                currentCoordinates.first,
                currentCoordinates.second,
                unit,
                language
            )

            val result = weatherState.first { it !is ApiState.Loading }

            if (result is ApiState.Success) {
                val forecast = result.data
                val city = forecast.city
                val firstForecast = forecast.forecastList.firstOrNull()
                val currentTemp = firstForecast?.mainMetrics?.temp ?: 0.0
                val currentWindSpeed = firstForecast?.wind?.speed ?: 0.0

                val tempSuffix = WeatherFormatters.getTempSuffix(unit)
                val windSuffix = WeatherFormatters.getWindSuffix(context, unit)
                val weatherMain = firstForecast?.weatherConditions?.firstOrNull()?.main ?: "Clear"
                val weatherDesc = firstForecast?.weatherConditions?.firstOrNull()?.description ?: "Unknown"

                val highTempStr = context.getString(R.string.high_temperature)
                val lowTempStr = context.getString(R.string.low_temperature)
                val windStr = context.getString(R.string.strong_wind)
                val rainStr = context.getString(R.string.expected_rain)
                val cloudsStr = context.getString(R.string.very_cloudy)

                val hasRain = alert.conditions.contains(rainStr) && weatherMain in listOf("Rain", "Drizzle", "Thunderstorm")
                val hasClouds = alert.conditions.contains(cloudsStr) && weatherMain == "Clouds"
                val hasStrongWind = alert.conditions.contains(windStr)
                val hasHighTemp = alert.conditions.contains(highTempStr)
                val hasLowTemp = alert.conditions.contains(lowTempStr)


                val isConditionMet = alert.conditions.isEmpty() ||
                        hasRain || hasClouds || hasStrongWind || hasHighTemp || hasLowTemp

                val isTempMet = (!hasLowTemp && !hasHighTemp) || (hasLowTemp && currentTemp <= alert.tempThreshold) || (hasHighTemp && currentTemp >= alert.tempThreshold)
                println("hasLowTemp = $hasLowTemp ")
                println("currentTemp = $currentTemp ")
                println("threshold = ${alert.tempThreshold} ")
                val isWindMet = !hasStrongWind || currentWindSpeed >= alert.windThreshold

                if (isConditionMet && isWindMet && isTempMet) {
                    val title = "Weather In ${city?.name}: $currentTemp$tempSuffix, ${currentWindSpeed}$windSuffix"
                    val message = "Condition: $weatherDesc"

                    if (alertTypeStr == AlertType.ALARM.name) {
                        val serviceIntent = Intent(context, WeatherAlarmService::class.java).apply {
                            action = ACTION_START_ALARM
                            putExtra(EXTRA_ALERT_ID, alertId)
                            putExtra(EXTRA_SOUND_URI, soundUriStr)
                            putExtra(ALARM_TITLE, title)
                            putExtra(ALARM_MESSAGE, message)
                        }
                        ContextCompat.startForegroundService(context, serviceIntent)
                    } else {
                        AlarmSoundManager.playSound(context, soundUriStr, isAlarm = false)
                        NotificationHelper.showStandardNotification(context, alertId, title, message)
                    }
                }
            } else {
                throw Exception("API Error")
            }

        } catch (e: Exception) {
            e.printStackTrace()
            return Result.failure()
        }
        return Result.success()
    }
}