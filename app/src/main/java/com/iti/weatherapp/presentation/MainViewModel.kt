package com.iti.weatherapp.presentation


import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iti.weatherapp.data.local.preferences.SettingsPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    val settingsPreferences: SettingsPreferences
) : ViewModel() {
    val appTheme = settingsPreferences.themeFlow
    val appLanguage = settingsPreferences.languageFlow

    init{
        observeSystemLanguage()
    }

    fun observeSystemLanguage(){
        viewModelScope.launch {
            appLanguage.collect {
                val localeList = if (it == "System Language") {
                    LocaleListCompat.getEmptyLocaleList()
                } else {
                    LocaleListCompat.forLanguageTags(it)
                }
                AppCompatDelegate.setApplicationLocales(localeList)
            }
        }
    }
}