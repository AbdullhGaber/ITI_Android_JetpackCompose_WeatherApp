package com.iti.weatherapp.presentation.navigations

import kotlinx.serialization.Serializable

@Serializable
object Home
@Serializable
object Splash
@Serializable
object Favorites
@Serializable
object Alerts
@Serializable
object Settings
@Serializable
data class MapPick(val isForFavorites: Boolean = false)
@Serializable
data class FavoriteDetails(val lat: Double, val lon: Double, val cityName: String)

@Serializable
object Onboarding