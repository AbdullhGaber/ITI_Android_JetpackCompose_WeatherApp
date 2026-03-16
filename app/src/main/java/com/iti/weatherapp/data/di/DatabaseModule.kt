package com.iti.weatherapp.data.di

import android.content.Context
import androidx.room.Room
import com.google.gson.Gson
import com.iti.weatherapp.data.local.db.WeatherDatabase
import com.iti.weatherapp.data.local.db.daos.FavoriteLocationsDao
import com.iti.weatherapp.data.local.db.daos.WeatherAlertsDao
import com.iti.weatherapp.data.local.db.type_converter.WeatherForecastConverters
import com.iti.weatherapp.data.utils.GsonParser
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideWeatherDatabase(
        @ApplicationContext context: Context
    ): WeatherDatabase {
        return Room.databaseBuilder(
            context,
            WeatherDatabase::class.java,
            "weather_database"
        ).fallbackToDestructiveMigration(false).addTypeConverter(
                WeatherForecastConverters(GsonParser(Gson()))
            ).build()
    }

    @Provides
    @Singleton
    fun provideFavoriteLocationsDao(database: WeatherDatabase): FavoriteLocationsDao {
        return database.favoriteLocationsDao()
    }

    @Provides
    @Singleton
    fun provideWeatherAlertsDao(database: WeatherDatabase): WeatherAlertsDao {
        return database.weatherAlertsDao()
    }
}