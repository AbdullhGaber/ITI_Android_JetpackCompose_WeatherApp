package com.iti.weatherapp.presentation.screens.map.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import com.iti.weatherapp.presentation.screens.map.MapConfig
import com.iti.weatherapp.presentation.screens.map.PickedLocation
import org.maplibre.compose.camera.CameraState
import org.maplibre.compose.expressions.dsl.const
import org.maplibre.compose.map.MapOptions
import org.maplibre.compose.map.MaplibreMap
import org.maplibre.compose.sources.GeoJsonData
import org.maplibre.compose.sources.rememberGeoJsonSource
import org.maplibre.compose.style.BaseStyle
import org.maplibre.compose.util.ClickResult
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import io.github.dellisd.spatialk.geojson.Feature
import io.github.dellisd.spatialk.geojson.FeatureCollection
import io.github.dellisd.spatialk.geojson.Point
import io.github.dellisd.spatialk.geojson.Position
import org.maplibre.compose.expressions.dsl.image
import org.maplibre.compose.layers.SymbolLayer

@Composable
internal fun LocationMap(
    modifier: Modifier,
    mapConfig: MapConfig,
    cameraState: CameraState,
    pickedLocation: PickedLocation?,
    markerPainter: Painter,
    markerIconSize: Float,
    markerColor: Color = Color.Unspecified,
    onMapClick: (lat: Double, lon: Double) -> Unit,
) {
    MaplibreMap(
        modifier = modifier,
        baseStyle = BaseStyle.Uri(mapConfig.styleUri),
        cameraState = cameraState,
        options = MapOptions(gestureOptions = mapConfig.gestureOptions),
        onMapClick = { point, _ ->
            onMapClick(point.latitude, point.longitude)
            ClickResult.Consume
        },
    ) {
        pickedLocation?.let { loc ->
            val markerSource = rememberGeoJsonSource(
                data = GeoJsonData.Features(
                    FeatureCollection(
                        Feature(geometry = Point(Position(loc.lon, loc.lat)))
                    )
                )
            )
            SymbolLayer(
                id = "picked-pin-layer",
                source = markerSource,
                iconImage = image(markerPainter, drawAsSdf = true),
                iconColor = const(if (markerColor == Color.Unspecified) MaterialTheme.colorScheme.primary else markerColor),
                iconSize = const(markerIconSize),
            )
        }
    }
}