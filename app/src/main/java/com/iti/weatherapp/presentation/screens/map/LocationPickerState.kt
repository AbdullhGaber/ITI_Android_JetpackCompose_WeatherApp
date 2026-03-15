package com.iti.weatherapp.presentation.screens.map

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

@Stable
class LocationPickerState(
    initialPickedLocation: PickedLocation? = null,
) {
    var pickedLocation by mutableStateOf(initialPickedLocation)
        internal set

    var searchQuery by mutableStateOf("")
        internal set

    var isSearching by mutableStateOf(false)
        internal set

    var searchError by mutableStateOf<String?>(null)
        internal set

    fun selectLocation(location: PickedLocation) {
        pickedLocation = location
    }

    fun reset() {
        pickedLocation = null
        searchQuery = ""
        searchError = null
    }
}

@Composable
fun rememberLocationPickerState(
    initialPickedLocation: PickedLocation? = null,
): LocationPickerState = remember { LocationPickerState(initialPickedLocation) }
