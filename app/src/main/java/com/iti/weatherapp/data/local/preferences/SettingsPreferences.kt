package com.iti.weatherapp.data.local.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.iti.weatherapp.data.utils.Constants.DATASTORE_NAME
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = DATASTORE_NAME)

@Singleton
class SettingsPreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        val LOCATION_METHOD = stringPreferencesKey("location_method")
        val TEMP_UNIT = stringPreferencesKey("temp_unit")
        val WIND_UNIT = stringPreferencesKey("wind_unit")
        val LANGUAGE = stringPreferencesKey("language")
    }

    val locationMethodFlow: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[LOCATION_METHOD] ?: "gps"
    }

    val tempUnitFlow: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[TEMP_UNIT] ?: "metric"
    }

    val windUnitFlow: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[WIND_UNIT] ?: "metric"
    }

    val languageFlow: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[LANGUAGE] ?: "en"
    }

    suspend fun saveLocationMethod(method: String) {
        context.dataStore.edit { prefs -> prefs[LOCATION_METHOD] = method }
    }

    suspend fun saveTempUnit(unit: String) {
        context.dataStore.edit { prefs -> prefs[TEMP_UNIT] = unit }
    }

    suspend fun saveWindUnit(unit: String) {
        context.dataStore.edit { prefs -> prefs[WIND_UNIT] = unit }
    }

    suspend fun saveLanguage(lang: String) {
        context.dataStore.edit { prefs -> prefs[LANGUAGE] = lang }
    }
}