package com.iti.weatherapp.presentation.utils

import com.iti.weatherapp.data.models.ForecastResponse
import java.text.NumberFormat
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Locale

object WeatherFormatters {
    private fun localizeDigits(text: String): String {
        val isArabic = Locale.getDefault().language == "ar"
        if (!isArabic) return text

        val arabicDigits = charArrayOf('٠', '١', '٢', '٣', '٤', '٥', '٦', '٧', '٨', '٩')
        val builder = StringBuilder()
        for (char in text) {
            if (char in '0'..'9') {
                builder.append(arabicDigits[char - '0'])
            } else {
                builder.append(char)
            }
        }
        return builder.toString()
    }

    fun formatLocalizedNumber(number: Number, maxDecimals: Int = 0): String {
        val formatter = NumberFormat.getInstance(Locale.getDefault())
        formatter.maximumFractionDigits = maxDecimals
        val formatted = formatter.format(number)

        // Pass the result through the digit swapper
        return localizeDigits(formatted)
    }

    private fun getFormattedString(timestamp: Long, timezoneOffset: Int, formatPattern: String): String {
        val instant = Instant.ofEpochSecond(timestamp)
        val zoneOffset = ZoneOffset.ofTotalSeconds(timezoneOffset)
        val formatter = DateTimeFormatter.ofPattern(formatPattern, Locale.getDefault())
        val formattedDate = instant.atZone(zoneOffset).format(formatter)

        return localizeDigits(formattedDate)
    }

    fun formatDateForHeader(timestamp: Long, timezoneOffset: Int): String {
        return getFormattedString(timestamp, timezoneOffset, "EEEE, MMM d, yyyy")
    }

    fun formatTime(timestamp: Long, timezoneOffset: Int): String {
        return getFormattedString(timestamp, timezoneOffset, "hh:mm a")
    }

    fun formatHourlyTime(timestamp: Long, timezoneOffset: Int): String {
        return getFormattedString(timestamp, timezoneOffset, "h a").uppercase(Locale.getDefault())
    }

    fun formatDailyDate(timestamp: Long, timezoneOffset: Int): String {
        return getFormattedString(timestamp, timezoneOffset, "EEEE, MMM d")
    }

    fun groupForecastByDay(response: ForecastResponse): List<DailyForecastItem> {
        val groupedList = response.forecastList.groupBy { item ->
            val instant = Instant.ofEpochSecond(item.timestamp)
            val zoneOffset = ZoneOffset.ofTotalSeconds(response.city?.timezone?: 0)
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.US)
            instant.atZone(zoneOffset).format(formatter)
        }

        return groupedList.map { (_, dayItems) ->
            val noonItem = dayItems.find { item ->
                val instant = Instant.ofEpochSecond(item.timestamp)
                val zoneOffset = ZoneOffset.ofTotalSeconds(response.city?.timezone ?: 0)
                val formatter = DateTimeFormatter.ofPattern("HH", Locale.US)
                instant.atZone(zoneOffset).format(formatter) == "12"
            } ?: dayItems.first()

            val minTemp = dayItems.minOf { it.mainMetrics.tempMin }.toInt()
            val maxTemp = dayItems.maxOf { it.mainMetrics.tempMax }.toInt()

            DailyForecastItem(
                timestamp = noonItem.timestamp,
                minTemp = minTemp,
                maxTemp = maxTemp,
                iconCode = noonItem.weatherConditions.first().icon,
                description = noonItem.weatherConditions.first().description,
                humidity = noonItem.mainMetrics.humidity,
                windSpeed = noonItem.wind.speed
            )
        }.take(5)
    }

    fun getTempSuffix(unit: String): String {
        return when (unit) {
            "metric" -> "°C"
            "imperial" -> "°F"
            "standard" -> "K"
            else -> ""
        }
    }

    fun getConvertedWindSpeed(apiSpeed: Double, tempUnitPreference: String, windUnitPreference: String): Double {
        val apiGaveMph = tempUnitPreference == "imperial"
        return when {
            apiGaveMph && windUnitPreference == "meter_sec" -> apiSpeed * 0.44704
            !apiGaveMph && windUnitPreference == "miles_hour" -> apiSpeed * 2.23694
            else -> apiSpeed
        }
    }

    fun formatDateTimePicker(timestamp: Long): String {
        val instant = Instant.ofEpochSecond(timestamp)
        val zoneOffset = ZoneOffset.systemDefault().rules.getOffset(instant)
        val formatter = DateTimeFormatter.ofPattern("MMM d, hh:mm a", Locale.getDefault())
        val formatted = instant.atZone(zoneOffset).format(formatter)
        return localizeDigits(formatted)
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