package com.iti.weatherapp.presentation.screens.map

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.weather_app.presentation.components.map.geocodeCity
import com.example.weather_app.presentation.components.map.reverseGeocodeLatLng
import com.iti.weatherapp.R
import com.iti.weatherapp.presentation.screens.map.components.DefaultConfirmationCard
import com.iti.weatherapp.presentation.screens.map.components.LocationMap
import com.iti.weatherapp.presentation.screens.map.components.LocationSearchBar
import io.github.dellisd.spatialk.geojson.Position
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import org.maplibre.compose.camera.CameraPosition
import org.maplibre.compose.camera.rememberCameraState

@OptIn(ExperimentalMaterial3Api::class, FlowPreview::class)
@Composable
fun LocationPickerScreen(
    onLocationSelected: (PickedLocation) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    state: LocationPickerState = rememberLocationPickerState(),
    mapConfig: MapConfig = MapConfig(),
    searchConfig: SearchConfig = SearchConfig(),
    shapes: LocationPickerShapes = LocationPickerShapes(),
    searchBarColors: SearchBarColors = SearchBarColors(),
    confirmationColors: ConfirmationCardColors = ConfirmationCardColors(),
    markerPainter: Painter? = null,
    markerIconSize: Float = 2.5f,
    markerColor: Color = Color.Unspecified,
    leadingSearchIcon: @Composable (() -> Unit)? = null,
    confirmationContent: (@Composable (PickedLocation, onConfirm: () -> Unit) -> Unit)? = null,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val keyboard = LocalSoftwareKeyboardController.current

    val cameraState = rememberCameraState(
        CameraPosition(
            target = Position(mapConfig.initialLon, mapConfig.initialLat),
            zoom = mapConfig.initialZoom,
        )
    )

    // ── Search debounce ───────────────────────────────────────────
    LaunchedEffect(state.searchQuery) {
        snapshotFlow { state.searchQuery }
            .debounce(searchConfig.debounceMillis)
            .distinctUntilChanged()
            .filter { it.isNotBlank() && it.length >= searchConfig.minQueryLength }
            .collectLatest { query ->
                state.isSearching = true
                state.searchError = null
                val result = geocodeCity(context, query)
                if (result != null) {
                    state.pickedLocation = result
                    cameraState.animateTo(
                        CameraPosition(
                            target = Position(result.lon, result.lat),
                            zoom = mapConfig.searchResultZoom,
                        )
                    )
                } else {
                    state.searchError = searchConfig.notFoundMessage
                }
                state.isSearching = false
            }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false,
        )
    ) {
        Box(modifier = modifier.fillMaxSize()) {

            LocationMap(
                modifier = Modifier.fillMaxSize(),
                mapConfig = mapConfig,
                cameraState = cameraState,
                pickedLocation = state.pickedLocation,
                markerPainter = markerPainter
                    ?: painterResource(R.drawable.ic_launcher_foreground),
                markerIconSize = markerIconSize,
                onMapClick = { lat, lon ->
                    keyboard?.hide()
                    scope.launch {
                        val city = reverseGeocodeLatLng(context, lat, lon)
                        state.pickedLocation = PickedLocation(lat, lon, "", city)
                    }
                },
            )

            LocationSearchBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        top = WindowInsets.statusBars
                            .asPaddingValues()
                            .calculateTopPadding() + shapes.topPadding
                    )
                    .padding(horizontal = shapes.horizontalPadding)
                    .align(Alignment.TopCenter),
                query = state.searchQuery,
                onQueryChange = {
                    state.searchQuery = it
                    if (it.isBlank()) state.searchError = null
                },
                onClearQuery = {
                    state.searchQuery = ""
                    state.searchError = null
                },
                onDismiss = onDismiss,
                isSearching = state.isSearching,
                searchError = state.searchError,
                placeholder = searchConfig.searchPlaceholder,
                shape = shapes.searchBarShape,
                elevation = shapes.searchBarElevation,
                colors = searchBarColors,
                leadingIcon = leadingSearchIcon,
            )

            AnimatedVisibility(
                visible = state.pickedLocation != null,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(
                        bottom = WindowInsets.navigationBars
                            .asPaddingValues()
                            .calculateBottomPadding() + shapes.bottomPadding
                    )
                    .padding(horizontal = shapes.horizontalPadding),
                enter = slideInVertically(initialOffsetY = { it }),
                exit = slideOutVertically(targetOffsetY = { it }),
            ) {
                state.pickedLocation?.let { loc ->
                    if (confirmationContent != null) {
                        confirmationContent(loc) { onLocationSelected(loc) }
                    } else {
                        DefaultConfirmationCard(
                            pickedLocation = loc,
                            colors = confirmationColors,
                            shape = shapes.confirmationCardShape,
                            elevation = shapes.confirmationCardElevation,
                            onConfirm = { onLocationSelected(loc) },
                        )
                    }
                }
            }
        }
    }
}
