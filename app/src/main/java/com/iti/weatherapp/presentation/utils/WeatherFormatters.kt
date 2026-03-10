package com.iti.weatherapp.presentation.utils

import com.iti.weatherapp.data.models.ForecastResponse
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Locale

object WeatherFormatters {

    // Modern, non-deprecated way using java.time
    private fun getFormattedString(timestamp: Long, timezoneOffset: Int, formatPattern: String): String {
        // 1. Convert the raw timestamp (seconds) into an Instant
        val instant = Instant.ofEpochSecond(timestamp)

        // 2. Safely create a ZoneOffset using the API's timezone integer
        val zoneOffset = ZoneOffset.ofTotalSeconds(timezoneOffset)

        // 3. Create a thread-safe formatter that strictly uses the device's current Locale
        val formatter = DateTimeFormatter.ofPattern(formatPattern, Locale.getDefault())

        // 4. Apply the offset to the instant and format it
        return instant.atZone(zoneOffset).format(formatter)
    }

    // Thursday, Mar 5, 2026
    fun formatDateForHeader(timestamp: Long, timezoneOffset: Int): String {
        return getFormattedString(timestamp, timezoneOffset, "EEEE, MMM d, yyyy")
    }

    // 01:52 AM (The "a" handles AM/PM translation based on Locale)
    fun formatTime(timestamp: Long, timezoneOffset: Int): String {
        return getFormattedString(timestamp, timezoneOffset, "hh:mm a")
    }

    // 1 AM
    fun formatHourlyTime(timestamp: Long, timezoneOffset: Int): String {
        return getFormattedString(timestamp, timezoneOffset, "h a").uppercase(Locale.getDefault())
    }

    // Friday, Mar 6
    fun formatDailyDate(timestamp: Long, timezoneOffset: Int): String {
        return getFormattedString(timestamp, timezoneOffset, "EEEE, MMM d")
    }

    // Helper to group forecast by day and create simplified daily data
    fun groupForecastByDay(response: ForecastResponse): List<DailyForecastItem> {
        val groupedList = response.forecastList.groupBy { item ->
            getFormattedString(item.timestamp, response.city.timezone, "yyyy-MM-dd")
        }

        return groupedList.map { (_, dayItems) ->
            // Use noon for icon/description, or first item if noon is not present
            val noonItem = dayItems.find { item ->
                getFormattedString(item.timestamp, response.city.timezone, "HH") == "12"
            } ?: dayItems.first()

            val minTemp = dayItems.minOf { it.mainMetrics.tempMin }.toInt()
            val maxTemp = dayItems.maxOf { it.mainMetrics.tempMax }.toInt()

            DailyForecastItem(
                timestamp = noonItem.timestamp,
                minTemp = minTemp,
                maxTemp = maxTemp,
                iconCode = noonItem.weatherConditions.first().icon,
                description = noonItem.weatherConditions.first().main,
                humidity = noonItem.mainMetrics.humidity,
                windSpeed = noonItem.wind.speed
            )
        }.take(5) // Limit to 5 days
    }

    // Unit suffix helper
    fun getTempSuffix(unit: String): String {
        return when (unit) {
            "metric" -> "°C"
            "imperial" -> "°F"
            "standard" -> "K"
            else -> ""
        }
    }

    fun getWindSuffix(unit: String): String {
        return when (unit) {
            "meter_sec" -> "m/s"
            "miles_hour" -> "mph"
            else -> ""
        }
    }

    fun getConvertedWindSpeed(apiSpeed: Double, tempUnit: String, windUnit: String): String {
        val apiGaveMph = tempUnit == "imperial"

        val finalSpeed = when {
            apiGaveMph && (windUnit == "meter_sec") -> apiSpeed * 0.44704
            !apiGaveMph && (windUnit == "miles_hour") -> apiSpeed * 2.23694
            else -> apiSpeed
        }

        return "%.2f".format(finalSpeed)
    }
}


data class DailyForecastItem(
    val timestamp: Long,
    val minTemp: Int,
    val maxTemp: Int,
    val iconCode: String,
    val description: String,
    val humidity: Int,
    val windSpeed: Double
)