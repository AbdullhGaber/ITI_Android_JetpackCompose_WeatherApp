package com.iti.weatherapp.presentation.screens.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iti.weatherapp.data.local.db.daos.FavoriteLocationsDao
import com.iti.weatherapp.data.local.db.entities.FavoriteLocation
import com.iti.weatherapp.data.local.preferences.SettingsPreferences
import com.iti.weatherapp.data.repository.Repository
import com.iti.weatherapp.data.utils.ApiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val repository: Repository,
    private val settingsPreferences: SettingsPreferences
) : ViewModel() {
    val favoritesList: StateFlow<List<FavoriteLocation>> = repository.getAllFavoriteLocations()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val currentLang = settingsPreferences.languageFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000),"en").value

    val currentUnit = settingsPreferences.tempUnitFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000),"metric").value
    fun addFavorite(cityName: String, lat: Double, lng: Double) {
        val cacheResponseFlow = repository.getWeatherForecast(
            lat = lat,
            lon = lng,
            units = currentUnit,
            lang = currentLang
        )

        viewModelScope.launch {
            cacheResponseFlow.collect { cacheResponse ->
                if(cacheResponse is ApiState.Success){
                    val newFav = FavoriteLocation(
                        cityName = cityName,
                        latitude = lat,
                        longitude = lng,
                        cachedWeather = cacheResponse.data
                    )
                    repository.insertFavoriteLocation(newFav)
                }
            }
        }
    }

    fun removeFavorite(location: FavoriteLocation) {
        viewModelScope.launch {
            repository.deleteFavoriteLocation(location)
        }
    }
}