package com.iti.weatherapp.presentation.screens.home.components


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.iti.weatherapp.R
import com.iti.weatherapp.presentation.utils.DailyForecastItem
import com.iti.weatherapp.presentation.utils.WeatherFormatters


@Composable
fun DailyItem(
    item: DailyForecastItem,
    tempUnitPref: String,
    windUnitPref: String,
    tempUnitSuffix: String,
    windUnitSuffix: String,
    timezoneOffset: Int
) {
    val accurateWindSpeedRaw = WeatherFormatters.getConvertedWindSpeed(item.windSpeed, windUnitPref).toLong()

    val formattedWindSpeed = WeatherFormatters.formatLocalizedNumber(accurateWindSpeedRaw)
    val convertedMaxTemp = WeatherFormatters.getConvertedTemperature(item.maxTemp, tempUnitPref)
    val convertedMinTemp = WeatherFormatters.getConvertedTemperature(item.minTemp, tempUnitPref)
    val formattedMaxTemp = WeatherFormatters.formatLocalizedNumber(convertedMaxTemp.toInt())
    val formattedMinTemp = WeatherFormatters.formatLocalizedNumber(convertedMinTemp.toInt())
    val formattedHumidity = WeatherFormatters.formatLocalizedNumber(item.humidity)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(text = WeatherFormatters.formatDailyDate(item.timestamp, timezoneOffset), fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                Text(text = "$formattedMaxTemp$tempUnitSuffix / $formattedMinTemp$tempUnitSuffix", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start, verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(
                    model = "https://openweathermap.org/img/wn/${item.iconCode}.png",
                    contentDescription = null,
                    modifier = Modifier.size(25.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "${item.description}, ${stringResource(R.string.hum_short)} $formattedHumidity%, ${stringResource(R.string.wind_short)} $formattedWindSpeed $windUnitSuffix",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}