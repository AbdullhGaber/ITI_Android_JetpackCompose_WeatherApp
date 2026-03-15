package com.iti.weatherapp.presentation.screens.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iti.weatherapp.data.local.db.daos.FavoriteLocationsDao
import com.iti.weatherapp.data.local.db.entities.FavoriteLocation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val favoriteLocationsDao: FavoriteLocationsDao
) : ViewModel() {
    val favoritesList: StateFlow<List<FavoriteLocation>> = favoriteLocationsDao.getAllFavoriteLocations()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addFavorite(cityName: String, lat: Double, lng: Double) {
        viewModelScope.launch {
            val newFav = FavoriteLocation(cityName = cityName, latitude = lat, longitude = lng)
            favoriteLocationsDao.insertFavoriteLocation(newFav)
        }
    }

    fun removeFavorite(location: FavoriteLocation) {
        viewModelScope.launch {
            favoriteLocationsDao.deleteFavoriteLocation(location)
        }
    }
}