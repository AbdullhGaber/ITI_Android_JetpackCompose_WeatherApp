package com.iti.weatherapp.data.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.iti.weatherapp.data.repository.Repository
import com.iti.weatherapp.data.utils.ApiState
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import javax.inject.Inject

@HiltWorker
class SyncWeatherWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val repository: Repository
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val lang = inputData.getString("lang") ?: "en"
        val unit = inputData.getString("unit") ?: "metric"

        return try {
            val favorites = repository.getAllFavoriteLocations().first()

            favorites.forEach { location ->
                repository.getWeatherForecast(
                    lat = location.latitude,
                    lon = location.longitude,
                    units = unit,
                    lang = lang
                ).collect { state ->
                    if (state is ApiState.Success) {
                        val updatedLocation = location.copy(cachedWeather = state.data)
                        repository.insertFavoriteLocation(updatedLocation)
                    }
                }
            }
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}