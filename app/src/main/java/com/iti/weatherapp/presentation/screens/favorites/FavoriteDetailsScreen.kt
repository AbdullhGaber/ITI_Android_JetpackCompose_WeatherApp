package com.iti.weatherapp.presentation.screens.favorites

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.iti.weatherapp.R
import com.iti.weatherapp.presentation.LocalBottomPadding
import com.iti.weatherapp.presentation.screens.home.HomeContent
import com.iti.weatherapp.presentation.screens.home.components.HomeShimmerLoading
import com.iti.weatherapp.presentation.utils.WeatherFormatters

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoriteDetailsScreen(
    viewModel: FavoriteDetailsViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val isLoading = viewModel.isLoading.value
    val error = viewModel.error.value
    val weatherData = viewModel.weatherData.value

    val tempUnitPref by viewModel.tempUnit
    val windUnitPref by viewModel.windUnit

    val tempUnitSuffix = WeatherFormatters.getTempSuffix(tempUnitPref)
    val windUnitSuffix = if (windUnitPref == "miles_hour") stringResource(R.string.wind_mph) else stringResource(R.string.wind_ms)

    Scaffold(
        topBar = {
            TopAppBar(
                expandedHeight = 0.dp,
                title = { Text(viewModel.cityName) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
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
                    Button(onClick = { viewModel.fetchFavoriteWeather() }) {
                        Text(stringResource(R.string.retry))
                    }
                }
            } else if (weatherData != null) {
                CompositionLocalProvider(LocalBottomPadding provides 0.dp) {
                    HomeContent(
                        forecast = weatherData,
                        tempUnitPref = tempUnitPref,
                        windUnitPref = windUnitPref,
                        tempUnitSuffix = tempUnitSuffix,
                        windUnitSuffix = windUnitSuffix,
                        onRefresh = { viewModel.fetchFavoriteWeather() },
                        isRefreshing = isLoading
                    )
                }
            }
        }
    }
}