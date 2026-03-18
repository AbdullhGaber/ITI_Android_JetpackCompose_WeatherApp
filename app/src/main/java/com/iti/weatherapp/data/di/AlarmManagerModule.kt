package com.iti.weatherapp.data.di


import android.content.Context
import com.iti.weatherapp.presentation.utils.alarm_notifications.WeatherAlarmScheduler
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AlarmManagerModule {
    @Provides
    @Singleton
    fun provideWeatherAlarmScheduler(@ApplicationContext context: Context): WeatherAlarmScheduler {
        return WeatherAlarmScheduler(context)
    }
}