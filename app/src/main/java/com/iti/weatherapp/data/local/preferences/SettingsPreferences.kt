package com.iti.weatherapp.data.local.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
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
    }
    val locationMethodFlow: Flow<String> = context.dataStore.data.map { prefs -> prefs[LOCATION_METHOD] ?: "gps" }
    val tempUnitFlow: Flow<String> = context.dataStore.data.map { prefs -> prefs[UNIT_TEMP] ?: "metric" }
    val windUnitFlow: Flow<String> = context.dataStore.data.map { prefs -> prefs[UNIT_WIND_SPEED] ?: "meter_sec" }
    val languageFlow: Flow<String> = context.dataStore.data.map { it[APP_LANGUAGE] ?: "System Language" }

    val themeFlow: Flow<AppTheme> = context.dataStore.data.map {
        val storedValue = it[APP_THEME] ?: AppTheme.SYSTEM_DEFAULT.name
        AppTheme.valueOf(storedValue)
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
}