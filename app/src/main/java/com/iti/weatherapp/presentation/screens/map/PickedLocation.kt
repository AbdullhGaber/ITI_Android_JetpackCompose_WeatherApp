package com.iti.weatherapp.presentation.screens.map

data class PickedLocation(
    val lat: Double,
    val lon: Double,
    val countryCode: String,
    val cityName: String?
)
data class AddressDetails(
    val city: String,
    val countryCode: String
)
