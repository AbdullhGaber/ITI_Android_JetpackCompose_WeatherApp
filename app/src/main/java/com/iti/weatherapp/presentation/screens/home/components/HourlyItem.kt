package com.iti.weatherapp.presentation.screens.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.iti.weatherapp.data.models.ForecastItem
import com.iti.weatherapp.presentation.utils.WeatherFormatters


@Composable
fun HourlyItem(item: ForecastItem, tempUnitPref: String, tempUnitSuffix: String, timezoneOffset: Int, isFirstItem: Boolean) {
    val convertedTemp = WeatherFormatters.getConvertedTemperature(item.mainMetrics.temp, tempUnitPref)
    val formattedTemp = WeatherFormatters.formatLocalizedNumber(convertedTemp.toInt())

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
        Text(text = "$formattedTemp$tempUnitSuffix", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
    }
}