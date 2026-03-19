package com.iti.weatherapp.presentation.screens.map.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.iti.weatherapp.presentation.screens.map.ConfirmationCardColors
import com.iti.weatherapp.presentation.screens.map.PickedLocation
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import com.iti.weatherapp.presentation.screens.map.MapPreviewViewModel
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.iti.weatherapp.R
import com.iti.weatherapp.presentation.screens.home.components.AnimatedWeatherIcon
import com.iti.weatherapp.presentation.utils.WeatherFormatters

@Composable
internal fun DefaultConfirmationCard(
    pickedLocation: PickedLocation,
    colors: ConfirmationCardColors,
    shape: Shape,
    elevation: Dp,
    onConfirm: () -> Unit,
    viewModel: MapPreviewViewModel = hiltViewModel()
) {
    val labelColor = if (colors.labelColor == Color.Unspecified)
        MaterialTheme.colorScheme.primary else colors.labelColor
    val titleColor = if (colors.titleColor == Color.Unspecified)
        MaterialTheme.colorScheme.onSurface else colors.titleColor

    val isLoading by viewModel.isLoading
    val weatherData by viewModel.weatherData
    val error by viewModel.error

    val tempUnitPref by viewModel.tempUnit.collectAsState()
    val windUnitPref by viewModel.windUnit.collectAsState()

    val context = LocalContext.current
    val tempUnitSuffix = WeatherFormatters.getTempSuffix(tempUnitPref)
    val windUnitSuffix = WeatherFormatters.getWindSuffix(context, windUnitPref)

    LaunchedEffect(pickedLocation) {
        viewModel.fetchPreviewWeather(pickedLocation.lat, pickedLocation.lon)
    }

    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = elevation),
        shape = shape,
        colors = CardDefaults.cardColors(
            containerColor = if (colors.containerColor == Color.Unspecified)
                MaterialTheme.colorScheme.surface
            else colors.containerColor
        ),
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
        ) {
            Text(
                text = "Selected Location",
                style = MaterialTheme.typography.labelMedium,
                color = labelColor,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = pickedLocation.cityName ?: "Custom Coordinates",
                style = MaterialTheme.typography.titleMedium,
                color = titleColor,
            )
            Spacer(Modifier.height(16.dp))

            if (isLoading && weatherData == null) {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (weatherData != null) {
                val currentForecast = weatherData!!.forecastList.first()
                val convertedTemp = WeatherFormatters.getConvertedTemperature(currentForecast.mainMetrics.temp, tempUnitPref)
                val formattedTemp = WeatherFormatters.formatLocalizedNumber(convertedTemp.toInt())
                val accurateWindSpeedRaw = WeatherFormatters.getConvertedWindSpeed(currentForecast.wind.speed, windUnitPref).toLong()
                val formattedWindSpeed = WeatherFormatters.formatLocalizedNumber(accurateWindSpeedRaw)
                val formattedHumidity = WeatherFormatters.formatLocalizedNumber(currentForecast.mainMetrics.humidity)
                val formattedPressure = WeatherFormatters.formatLocalizedNumber(currentForecast.mainMetrics.pressure)

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Column {
                        Text(text = "$formattedTemp$tempUnitSuffix", style = MaterialTheme.typography.headlineMedium, color = titleColor)
                        Text(text = currentForecast.weatherConditions.first().description.replaceFirstChar{it.uppercase()}, style = MaterialTheme.typography.bodyMedium, color = labelColor)
                    }
                    AnimatedWeatherIcon(
                        weatherCode = currentForecast.weatherConditions.first().icon,
                        iconSize = 50.dp
                    )
                }
                Spacer(Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = "${stringResource(R.string.humidity)}: $formattedHumidity%", style = MaterialTheme.typography.bodySmall, color = titleColor)
                    Text(text = "${stringResource(R.string.wind_speed)}: $formattedWindSpeed $windUnitSuffix", style = MaterialTheme.typography.bodySmall, color = titleColor)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = "${stringResource(R.string.pressure)}: $formattedPressure ${stringResource(R.string.pressure_unit)}", style = MaterialTheme.typography.bodySmall, color = titleColor)
                }
            } else if (error != null) {
                Text("Error loading preview", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }

            Spacer(Modifier.height(16.dp))
            Button(
                onClick = onConfirm,
                modifier = Modifier.fillMaxWidth(),
                colors = if (colors.buttonContainerColor != Color.Unspecified)
                    ButtonDefaults.buttonColors(containerColor = colors.buttonContainerColor)
                else ButtonDefaults.buttonColors(),
            ) {
                Text("Confirm Location")
            }
        }
    }
}