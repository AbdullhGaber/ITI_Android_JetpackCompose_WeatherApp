package com.iti.weatherapp.presentation.screens.map

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.maplibre.compose.map.GestureOptions


data class MapConfig(
    val initialLat: Double = 31.0822,
    val initialLon: Double = 29.7408,
    val initialZoom: Double = 12.0,
    val searchResultZoom: Double = 13.0,
    val styleUri: String = "https://tiles.openfreemap.org/styles/liberty",
    val gestureOptions: GestureOptions = GestureOptions(
        isScrollEnabled = true,
        isZoomEnabled = true,
        isTiltEnabled = true,
        isRotateEnabled = true,
    ),
)

data class SearchConfig(
    val debounceMillis: Long = 800L,
    val minQueryLength: Int = 3,
    val searchPlaceholder: String = "Search location...",
    val notFoundMessage: String = "Location not found.",
)

data class SearchBarColors(
    val containerColor: Color = Color.Unspecified,
    val focusedBorderColor: Color = Color.Transparent,
    val unfocusedBorderColor: Color = Color.Transparent,
)


data class ConfirmationCardColors(
    val containerColor: Color = Color.Unspecified,
    val labelColor: Color = Color.Unspecified,
    val titleColor: Color = Color.Unspecified,
    val buttonContainerColor: Color = Color.Unspecified,
)


data class LocationPickerShapes(
    val searchBarShape: Shape = RoundedCornerShape(32.dp),
    val confirmationCardShape: Shape = RoundedCornerShape(16.dp),
    val searchBarElevation: Dp = 8.dp,
    val confirmationCardElevation: Dp = 12.dp,
    val horizontalPadding: Dp = 16.dp,
    val topPadding: Dp = 16.dp,
    val bottomPadding: Dp = 16.dp,
)
