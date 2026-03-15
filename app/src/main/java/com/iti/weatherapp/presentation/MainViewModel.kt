package com.iti.weatherapp.presentation


import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iti.weatherapp.data.local.preferences.SettingsPreferences
import com.iti.weatherapp.presentation.navigations.Home
import com.iti.weatherapp.presentation.navigations.Onboarding
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    val settingsPreferences: SettingsPreferences
) : ViewModel() {
    val appTheme = settingsPreferences.themeFlow
    val appLanguage = settingsPreferences.languageFlow
    private val _startDestination = MutableStateFlow<Any?>(null)
    val startDestination: StateFlow<Any?> = _startDestination.asStateFlow()

    init{
        observeSystemLanguage()
        observeOnBoardingEntrance()
    }

    private fun observeOnBoardingEntrance() {
        viewModelScope.launch {
            settingsPreferences.hasSeenOnboardingFlow.collect { hasSeen ->
                if (hasSeen) {
                    _startDestination.value = Home
                } else {
                    _startDestination.value = Onboarding
                }
            }
        }
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

    fun completeOnboarding() {
        viewModelScope.launch {
            settingsPreferences.saveOnboardingState(true)
        }
    }
}