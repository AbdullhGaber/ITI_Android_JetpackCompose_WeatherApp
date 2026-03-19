package com.iti.weatherapp.presentation.utils.validation

import com.iti.weatherapp.R
import com.iti.weatherapp.data.local.db.entities.AlertType
import com.iti.weatherapp.data.local.db.entities.WeatherAlert

data class ValidationResult(
    val isSuccessful: Boolean,
    val errorMessageResId: Int? = null
)

object AlertValidator {
    fun validateAlertTime(alert: WeatherAlert): ValidationResult {
        val currentTimeSeconds = System.currentTimeMillis() / 1000

        if (alert.startDateTimestamp < (currentTimeSeconds - 120)) {
            return ValidationResult(false, R.string.error_start_time_past)
        }

        if (alert.alertType == AlertType.NOTIFICATION) {
            if (alert.endDateTimestamp <= alert.startDateTimestamp) {
                return ValidationResult(false, R.string.error_end_time_before_start)
            }

            if (alert.endDateTimestamp < currentTimeSeconds) {
                return ValidationResult(false, R.string.error_end_time_past)
            }
        }

        return ValidationResult(true)
    }
}