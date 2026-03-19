package com.iti.weatherapp.presentation.screens.favorites

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iti.weatherapp.data.local.db.entities.FavoriteLocation
import com.iti.weatherapp.data.local.preferences.SettingsPreferences
import com.iti.weatherapp.data.repository.Repository
import com.iti.weatherapp.data.utils.ApiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
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

    var showDeleteDialog by mutableStateOf(false)
        private set

    var locationToDelete by mutableStateOf<FavoriteLocation?>(null)
        private set

    fun triggerDeleteDialog(location: FavoriteLocation) {
        locationToDelete = location
        showDeleteDialog = true
    }

    fun dismissDeleteDialog() {
        showDeleteDialog = false
        locationToDelete = null
    }

    fun confirmDelete() {
        locationToDelete?.let { removeFavorite(it) }
        dismissDeleteDialog()
    }

    fun addFavorite(cityName: String, lat: Double, lng: Double) {
        viewModelScope.launch {
            val currentLang = settingsPreferences.languageFlow.first()
            val currentUnit = settingsPreferences.tempUnitFlow.first()

            val cacheResponseFlow = repository.getWeatherForecast(
                lat = lat,
                lon = lng,
                units = currentUnit,
                lang = currentLang
            )

            cacheResponseFlow.collect { cacheResponse ->
                if (cacheResponse is ApiState.Success) {
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

    private fun removeFavorite(location: FavoriteLocation) {
        viewModelScope.launch {
            repository.deleteFavoriteLocation(location)
        }
    }
}