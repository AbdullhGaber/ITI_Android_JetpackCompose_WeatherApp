package com.iti.weatherapp.presentation.screens.home.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iti.weatherapp.R
import com.iti.weatherapp.data.models.ForecastItem


@Composable
fun HourlyForecastSection(hourlyItems: List<ForecastItem>, tempUnitPref: String, tempUnitSuffix: String, timezoneOffset: Int) {
    val firstItem = hourlyItems.firstOrNull()

    Column {
        Text(text = stringResource(R.string.hourly_forecast), fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(modifier = Modifier.height(8.dp))
        LazyRow(
            contentPadding = PaddingValues(horizontal = 0.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(hourlyItems) { item ->
                HourlyItem(
                    item = item,
                    tempUnitPref = tempUnitPref,
                    tempUnitSuffix = tempUnitSuffix,
                    timezoneOffset = timezoneOffset,
                    isFirstItem = (item == firstItem)
                )
            }
        }
    }
}