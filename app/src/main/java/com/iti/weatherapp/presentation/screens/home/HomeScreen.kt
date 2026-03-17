package com.iti.weatherapp.presentation.screens.home

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Air
import androidx.compose.material.icons.outlined.Cloud
import androidx.compose.material.icons.outlined.Speed
import androidx.compose.material.icons.outlined.WaterDrop
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil.compose.AsyncImage
import com.google.android.gms.location.LocationServices
import com.iti.weatherapp.R
import com.iti.weatherapp.data.models.ForecastItem
import com.iti.weatherapp.data.models.ForecastResponse
import com.iti.weatherapp.presentation.utils.WeatherFormatters
import androidx.compose.runtime.*
import com.iti.weatherapp.presentation.LocalBottomPadding
import com.iti.weatherapp.presentation.screens.home.components.DailyForecastSection
import com.iti.weatherapp.presentation.screens.home.components.HomeShimmerLoading
import com.iti.weatherapp.presentation.screens.home.components.HorizontalWeatherMetricItem
import com.iti.weatherapp.presentation.screens.home.components.HourlyForecastSection
import com.iti.weatherapp.presentation.screens.home.components.VerticalWeatherMetricItem
import com.iti.weatherapp.presentation.utils.LocationUtils
import com.iti.weatherapp.presentation.utils.PermissionUtils
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    val locationMethod by viewModel.locationMethod.collectAsState()
    val customLocation by viewModel.customMapLocationFlow.collectAsState()

    val isLoading = viewModel.isLoading.value
    val error = viewModel.error.value
    val weatherData = viewModel.weatherData.value
    val tempUnitSuffix = WeatherFormatters.getTempSuffix(viewModel.tempUnit.value)
    val windUnitSuffix =  WeatherFormatters.getWindSuffix(context, viewModel.windUnit.value)

    val tempUnitPref = viewModel.tempUnit.value
    val windUnitPref = viewModel.windUnit.value
    val scope = rememberCoroutineScope()

    val fallBackMessage = stringResource(R.string.fallback_called)

    val fetchGpsLocation = {
        scope.launch {
            val location = LocationUtils.getCurrentLocation(fusedLocationClient)
            if (location != null) {
                viewModel.getWeatherData(location.latitude, location.longitude)
            } else {
                viewModel.getWeatherData(31.2001, 29.9187)
                Toast.makeText(context, fallBackMessage, Toast.LENGTH_SHORT).show()
            }
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions.values.any { it }
        if (granted) {
            fetchGpsLocation()
        } else {
            viewModel.getWeatherData(31.2001, 29.9187)
        }
    }

    val triggerWeatherUpdate = {
        if (locationMethod == "map") {
            viewModel.getWeatherData(customLocation.first, customLocation.second)
        } else {
            if (PermissionUtils.hasLocationPermissions(context)) {
                fetchGpsLocation()
            } else {
                permissionLauncher.launch(PermissionUtils.locationPermissions)
            }
        }
    }

    LaunchedEffect(locationMethod,customLocation) {
        triggerWeatherUpdate()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        if (isLoading && weatherData == null) {
            HomeShimmerLoading()
        } else if (error != null) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "${stringResource(R.string.error)}: $error",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(16.dp)
                )
                Button(onClick = { triggerWeatherUpdate() }) {
                    Text(stringResource(R.string.retry))
                }
            }
        } else if (weatherData != null) {
            HomeContent(
                forecast = weatherData,
                tempUnitPref = tempUnitPref,
                windUnitPref = windUnitPref,
                tempUnitSuffix = tempUnitSuffix,
                windUnitSuffix = windUnitSuffix,
                onRefresh = { triggerWeatherUpdate() },
                isRefreshing = isLoading
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeContent(
    forecast: ForecastResponse,
    tempUnitPref: String,
    windUnitPref: String,
    tempUnitSuffix: String,
    windUnitSuffix: String,
    onRefresh: () -> Unit,
    isRefreshing: Boolean
) {
    val currentForecast = forecast.forecastList.first()
    val timezoneOffset = forecast.city?.timezone ?: 0
    val dailyForecast = WeatherFormatters.groupForecastByDay(forecast)
    val exactCurrentTimeSeconds = System.currentTimeMillis() / 1000

    val dynamicBottomPadding = LocalBottomPadding.current

    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
        modifier = Modifier.fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = 16.dp,
                top = 16.dp,
                end = 16.dp,
                bottom = 16.dp + dynamicBottomPadding
            ),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { HeaderSection(forecast.city?.name ?: "City", exactCurrentTimeSeconds, timezoneOffset) }
            item { MainWeatherCard(currentForecast, tempUnitPref, windUnitPref, tempUnitSuffix, windUnitSuffix, timezoneOffset) }
            item { HourlyForecastSection(forecast.forecastList.take(8), tempUnitSuffix, timezoneOffset) }
            item { DailyForecastSection(dailyForecast, tempUnitPref, windUnitPref, tempUnitSuffix, windUnitSuffix, timezoneOffset) }
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
            Text(
                text = cityName,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(text = WeatherFormatters.formatDateForHeader(timestamp, timezoneOffset), fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Text(text = WeatherFormatters.formatTime(timestamp, timezoneOffset), fontSize = 16.sp, color = MaterialTheme.colorScheme.tertiary, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun MainWeatherCard(
    forecast: ForecastItem,
    tempUnitPref: String,
    windUnitPref: String,
    tempUnitSuffix: String,
    windUnitSuffix: String,
    timezoneOffset: Int
){
    val accurateWindSpeedRaw = WeatherFormatters.getConvertedWindSpeed(forecast.wind.speed, tempUnitPref, windUnitPref).toLong()
    val formattedWindSpeed = WeatherFormatters.formatLocalizedNumber(accurateWindSpeedRaw)

    val formattedTemp = WeatherFormatters.formatLocalizedNumber(forecast.mainMetrics.temp.toInt())
    val formattedHumidity = WeatherFormatters.formatLocalizedNumber(forecast.mainMetrics.humidity)
    val formattedPressure = WeatherFormatters.formatLocalizedNumber(forecast.mainMetrics.pressure)
    val formattedClouds = WeatherFormatters.formatLocalizedNumber(forecast.clouds.cloudiness)

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
                        text = "$formattedTemp$tempUnitSuffix",
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
                        label = stringResource(R.string.humidity),
                        value = "$formattedHumidity%",
                        icon = { Icon(Icons.Outlined.WaterDrop, contentDescription = null, modifier = Modifier.size(24.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant) }
                    )
                    VerticalWeatherMetricItem(
                        label = stringResource(R.string.wind_speed),
                        value = "$formattedWindSpeed $windUnitSuffix",
                        icon = { Icon(Icons.Outlined.Air, contentDescription = null, modifier = Modifier.size(24.dp), tint = MaterialTheme.colorScheme.secondary) },
                        valueColor = MaterialTheme.colorScheme.primary
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
                        label = stringResource(R.string.pressure),
                        value = "$formattedPressure ${stringResource(R.string.pressure_unit)}",
                        icon = { Icon(Icons.Outlined.Speed, contentDescription = null, modifier = Modifier.size(24.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant) }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalWeatherMetricItem(
                        label = stringResource(R.string.clouds),
                        value = "${forecast.weatherConditions.first().description.replaceFirstChar{it.uppercase()}}, $formattedClouds%",
                        icon = { Icon(Icons.Outlined.Cloud, contentDescription = null, modifier = Modifier.size(24.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant) }
                    )
                }
            }
        }
    }
}
