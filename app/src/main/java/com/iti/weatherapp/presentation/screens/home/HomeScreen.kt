package com.iti.weatherapp.presentation.screens.home


import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Air
import androidx.compose.material.icons.outlined.Cloud
import androidx.compose.material.icons.outlined.Speed
import androidx.compose.material.icons.outlined.WaterDrop
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil.compose.AsyncImage
import com.google.android.gms.location.LocationServices
import com.iti.weatherapp.data.models.ForecastItem
import com.iti.weatherapp.data.models.ForecastResponse
import com.iti.weatherapp.presentation.utils.DailyForecastItem
import com.iti.weatherapp.presentation.utils.WeatherFormatters
import androidx.compose.runtime.*
import com.iti.weatherapp.presentation.utils.LocationUtils
import com.iti.weatherapp.presentation.utils.PermissionUtils
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    // UI States
    val isLoading = viewModel.isLoading.value
    val error = viewModel.error.value
    val weatherData = viewModel.weatherData.value
    val tempUnitSuffix = WeatherFormatters.getTempSuffix(viewModel.tempUnit.value)
    val windUnitSuffix = WeatherFormatters.getWindSuffix(viewModel.windUnit.value)
    val scope = rememberCoroutineScope()

    val fetchLocation = {
        scope.launch {
            val location = LocationUtils.getCurrentLocation(fusedLocationClient)
            if (location != null) {
                viewModel.getWeatherData(location.latitude, location.longitude)
            } else {
                viewModel.getWeatherData(31.2001, 29.9187)
                Toast.makeText(context, "Fallback called", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Permission Launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions.values.any { it }
        if (granted) {
            fetchLocation()
        } else {
            // Permission denied -> Fallback to Alexandria
            viewModel.getWeatherData(31.2001, 29.9187)
        }
    }

    // Check permissions on screen load
    LaunchedEffect(Unit) {
        if (PermissionUtils.hasLocationPermissions(context)) {
            fetchLocation()
        } else {
            permissionLauncher.launch(PermissionUtils.locationPermissions)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        if (isLoading && weatherData == null) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        } else if (error != null) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "Error: $error", color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(16.dp))
                Button(onClick = { fetchLocation() }) { Text("Retry") }
            }
        } else if (weatherData != null) {
            HomeContent(weatherData, tempUnitSuffix, windUnitSuffix)
        }
    }
}

@Composable
fun HomeContent(
    forecast: ForecastResponse,
    tempUnitSuffix: String,
    windUnitSuffix: String
) {
    val currentForecast = forecast.forecastList.first()
    val timezoneOffset = forecast.city.timezone
    val dailyForecast = WeatherFormatters.groupForecastByDay(forecast)
    val exactCurrentTimeSeconds = System.currentTimeMillis() / 1000

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            HeaderSection(forecast.city.name, exactCurrentTimeSeconds, timezoneOffset)
        }
        item {
            MainWeatherCard(currentForecast, tempUnitSuffix, windUnitSuffix, timezoneOffset)
        }
        item {
            HourlyForecastSection(forecast.forecastList.take(8), tempUnitSuffix, timezoneOffset)
        }
        item {
            DailyForecastSection(dailyForecast, tempUnitSuffix, windUnitSuffix, timezoneOffset)
        }
        item {
            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}

@Composable
fun HeaderSection(cityName: String, timestamp: Long, timezoneOffset: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Column {
            Text(text = "$cityName, Egypt", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
            Text(text = WeatherFormatters.formatDateForHeader(timestamp, timezoneOffset), fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Text(text = WeatherFormatters.formatTime(timestamp, timezoneOffset), fontSize = 16.sp, color = MaterialTheme.colorScheme.tertiary, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun MainWeatherCard(forecast: ForecastItem, tempUnitSuffix: String, windUnitSuffix: String, timezoneOffset: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(vertical = 24.dp, horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                AsyncImage(
                    model = "https://openweathermap.org/img/wn/${forecast.weatherConditions.first().icon}@4x.png",
                    contentDescription = forecast.weatherConditions.first().description,
                    modifier = Modifier
                        .size(120.dp)
                        .offset(x = (-10).dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column(verticalArrangement = Arrangement.Center) {
                    Text(
                        text = "${forecast.mainMetrics.temp.toInt()}${tempUnitSuffix}",
                        fontSize = 64.sp,
                        fontWeight = FontWeight.Normal,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = forecast.weatherConditions.first().description.replaceFirstChar{it.uppercase()},
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    VerticalWeatherMetricItem(
                        label = "Humidity",
                        value = "${forecast.mainMetrics.humidity}%",
                        icon = { Icon(Icons.Outlined.WaterDrop, contentDescription = null, modifier = Modifier.size(24.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant) }
                    )
                    VerticalWeatherMetricItem(
                        label = "Wind Speed",
                        value = "${forecast.wind.speed.toInt()} $windUnitSuffix",
                        icon = { Icon(Icons.Outlined.Air, contentDescription = null, modifier = Modifier.size(24.dp), tint = MaterialTheme.colorScheme.secondary) },
                        valueColor = MaterialTheme.colorScheme.primary // Use primary blue for the wind value to match design
                    )
                }

                VerticalDivider(
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(vertical = 8.dp),
                    color = MaterialTheme.colorScheme.outlineVariant,
                    thickness = 1.dp
                )

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 16.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    HorizontalWeatherMetricItem(
                        label = "Pressure",
                        value = "${forecast.mainMetrics.pressure} hPa",
                        icon = { Icon(Icons.Outlined.Speed, contentDescription = null, modifier = Modifier.size(24.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant) }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalWeatherMetricItem(
                        label = "Clouds",
                        value = "${forecast.weatherConditions.first().description.replaceFirstChar{it.uppercase()}}, ${forecast.clouds.cloudiness}%",
                        icon = { Icon(Icons.Outlined.Cloud, contentDescription = null, modifier = Modifier.size(24.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant) }
                    )
                }
            }
        }
    }
}

@Composable
fun VerticalWeatherMetricItem(label: String, value: String, icon: @Composable () -> Unit, valueColor: Color = MaterialTheme.colorScheme.onSurface) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        icon()
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = label, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Medium)
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = value, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = valueColor)
    }
}

@Composable
fun HorizontalWeatherMetricItem(label: String, value: String, icon: @Composable () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        icon()
        Spacer(modifier = Modifier.width(8.dp))
        Column(horizontalAlignment = Alignment.Start) {
            Text(text = label, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold)
            Text(text = value, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun HourlyForecastSection(hourlyItems: List<ForecastItem>, tempUnitSuffix: String, timezoneOffset: Int) {
    val firstItem = hourlyItems.firstOrNull()

    Column {
        Text(text = "HOURLY FORECAST", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(modifier = Modifier.height(8.dp))
        LazyRow(
            contentPadding = PaddingValues(horizontal = 0.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(hourlyItems) { item ->
                HourlyItem(
                    item = item,
                    tempUnitSuffix = tempUnitSuffix,
                    timezoneOffset = timezoneOffset,
                    isFirstItem = (item == firstItem)
                )
            }
        }
    }
}

@Composable
fun HourlyItem(item: ForecastItem, tempUnitSuffix: String, timezoneOffset: Int, isFirstItem: Boolean) {
    Column(
        modifier = Modifier
            .width(65.dp)
            .height(110.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(if (isFirstItem) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surface)
            .border(
                width = 1.dp,
                color = if (isFirstItem) MaterialTheme.colorScheme.primary.copy(alpha = 0.3f) else Color.Transparent,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        Text(text = WeatherFormatters.formatHourlyTime(item.timestamp, timezoneOffset), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
        AsyncImage(
            model = "https://openweathermap.org/img/wn/${item.weatherConditions.first().icon}.png",
            contentDescription = null,
            modifier = Modifier.size(35.dp)
        )
        Text(text = "${item.mainMetrics.temp.toInt()}${tempUnitSuffix}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
    }
}

@Composable
fun DailyForecastSection(dailyItems: List<DailyForecastItem>, tempUnitSuffix: String, windUnitSuffix: String, timezoneOffset: Int) {
    Column {
        Text(text = "5-DAY FORECAST", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(modifier = Modifier.height(8.dp))
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            dailyItems.forEach { item ->
                DailyItem(item, tempUnitSuffix, windUnitSuffix, timezoneOffset)
            }
        }
    }
}

@Composable
fun DailyItem(item: DailyForecastItem, tempUnitSuffix: String, windUnitSuffix: String, timezoneOffset: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(text = WeatherFormatters.formatDailyDate(item.timestamp, timezoneOffset), fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                Text(text = "${item.maxTemp}${tempUnitSuffix} / ${item.minTemp}${tempUnitSuffix}", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start, verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(
                    model = "https://openweathermap.org/img/wn/${item.iconCode}.png",
                    contentDescription = null,
                    modifier = Modifier.size(25.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "${item.description}, Hum ${item.humidity}%, Wind ${item.windSpeed.toInt()}${windUnitSuffix}", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}