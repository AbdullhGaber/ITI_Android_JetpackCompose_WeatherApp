package com.iti.weatherapp.data.local.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.iti.weatherapp.data.utils.Constants.DATASTORE_NAME
import com.iti.weatherapp.presentation.ui.theme.AppTheme
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton



@Singleton
class SettingsPreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {
    val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = DATASTORE_NAME)
    companion object Keys{
        val LOCATION_METHOD = stringPreferencesKey("location_method")
        val UNIT_TEMP = stringPreferencesKey("unit_temp")
        val UNIT_WIND_SPEED = stringPreferencesKey("unit_wind")
        val APP_THEME = stringPreferencesKey("app_theme")
        val APP_LANGUAGE = stringPreferencesKey("app_language")

        val CUSTOM_LAT = doublePreferencesKey("custom_lat")
        val CUSTOM_LNG = doublePreferencesKey("custom_lng")

        val CURRENT_LAT = doublePreferencesKey("current_lat")
        val CURRENT_LNG = doublePreferencesKey("current_lng")
    }
    val locationMethodFlow: Flow<String> = context.dataStore.data.map { prefs -> prefs[LOCATION_METHOD] ?: "gps" }
    val tempUnitFlow: Flow<String> = context.dataStore.data.map { prefs -> prefs[UNIT_TEMP] ?: "metric" }
    val windUnitFlow: Flow<String> = context.dataStore.data.map { prefs -> prefs[UNIT_WIND_SPEED] ?: "meter_sec" }
    val languageFlow: Flow<String> = context.dataStore.data.map { it[APP_LANGUAGE] ?: "System Language" }

    val themeFlow: Flow<AppTheme> = context.dataStore.data.map {
        val storedValue = it[APP_THEME] ?: AppTheme.SYSTEM_DEFAULT.name
        AppTheme.valueOf(storedValue)
    }

    val customMapLocationFlow: Flow<Pair<Double, Double>> = context.dataStore.data.map { prefs ->
        val lat = prefs[CUSTOM_LAT] ?: 31.2001
        val lng = prefs[CUSTOM_LNG] ?: 29.9187
        Pair(lat, lng)
    }

    val currentLocationFlow: Flow<Pair<Double, Double>> = context.dataStore.data.map { prefs ->
        val lat = prefs[CURRENT_LAT] ?: 31.2001
        val lng = prefs[CURRENT_LNG] ?: 29.9187
        Pair(lat, lng)
    }

    suspend fun saveLocationMethod(method: String) {
        context.dataStore.edit { prefs -> prefs[LOCATION_METHOD] = method }
    }

    suspend fun saveTempUnit(unit: String) {
        context.dataStore.edit { prefs -> prefs[UNIT_TEMP] = unit }
    }

    suspend fun saveWindUnit(unit: String) {
        context.dataStore.edit { prefs -> prefs[UNIT_WIND_SPEED] = unit }
    }
    suspend fun saveLanguage(lang: String) = context.dataStore.edit { it[APP_LANGUAGE] = lang }
    suspend fun saveTheme(theme: AppTheme) = context.dataStore.edit { it[APP_THEME] = theme.name }

    suspend fun saveCustomMapLocation(lat : Double, lng : Double){
        context.dataStore.edit { prefs ->
            prefs[CUSTOM_LAT] = lat
            prefs[CUSTOM_LNG] = lng
        }
    }

    suspend fun saveCurrentLocation(lat : Double, lng : Double){
        context.dataStore.edit { prefs ->
            prefs[CURRENT_LAT] = lat
            prefs[CURRENT_LNG] = lng
        }
    }
}