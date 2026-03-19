package com.iti.weatherapp.presentation.screens.home.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iti.weatherapp.R
import com.iti.weatherapp.presentation.utils.DailyForecastItem

@Composable
fun DailyForecastSection(
    dailyItems: List<DailyForecastItem>,
    tempUnitPref: String,
    windUnitPref: String,
    tempUnitSuffix: String,
    windUnitSuffix: String,
    timezoneOffset: Int
) {
    val overallMin = dailyItems.minOfOrNull { it.minTemp } ?: 0.0
    val overallMax = dailyItems.maxOfOrNull { it.maxTemp } ?: 0.0

    Column {
        Text(text = stringResource(R.string.five_day_forecast), fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(modifier = Modifier.height(8.dp))
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            dailyItems.forEach { item ->
                DailyItem(item, overallMin, overallMax, tempUnitPref, windUnitPref, tempUnitSuffix, windUnitSuffix, timezoneOffset)
            }
        }
    }
}