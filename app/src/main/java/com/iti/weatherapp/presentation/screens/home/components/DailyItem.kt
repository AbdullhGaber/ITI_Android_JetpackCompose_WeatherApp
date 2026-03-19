package com.iti.weatherapp.presentation.screens.home.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iti.weatherapp.R
import com.iti.weatherapp.presentation.utils.DailyForecastItem
import com.iti.weatherapp.presentation.utils.WeatherFormatters

@Composable
fun DailyItem(
    item: DailyForecastItem,
    overallMin: Double,
    overallMax: Double,
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
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = WeatherFormatters.formatDailyDate(item.timestamp, timezoneOffset), 
                    fontSize = 16.sp, 
                    fontWeight = FontWeight.Bold, 
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.widthIn(min = 70.dp)
                )
                
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f).padding(start = 12.dp)) {
                    Text(text = "$formattedMinTemp$tempUnitSuffix", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Canvas(modifier = Modifier.weight(1f).height(6.dp)) {
                        drawLine(
                            color = Color.LightGray.copy(alpha = 0.3f),
                            start = Offset(0f, size.height/2),
                            end = Offset(size.width, size.height/2),
                            strokeWidth = size.height,
                            cap = StrokeCap.Round
                        )
                        
                        val range = (overallMax - overallMin).coerceAtLeast(1.0)
                        val startFraction = ((item.minTemp - overallMin) / range).toFloat().coerceIn(0f, 1f)
                        val endFraction = ((item.maxTemp - overallMin) / range).toFloat().coerceIn(0f, 1f)
                        
                        val activeGradient = Brush.horizontalGradient(
                            colors = listOf(Color(0xFF4CAF50), Color(0xFFFF9800), Color(0xFFF44336)),
                            startX = 0f,
                            endX = size.width
                        )
                        
                        drawLine(
                            brush = activeGradient,
                            start = Offset(size.width * startFraction, size.height/2),
                            end = Offset(size.width * endFraction, size.height/2),
                            strokeWidth = size.height,
                            cap = StrokeCap.Round
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "$formattedMaxTemp$tempUnitSuffix", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start, verticalAlignment = Alignment.CenterVertically) {
                AnimatedWeatherIcon(
                    weatherCode = item.iconCode,
                    iconSize = 25.dp
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