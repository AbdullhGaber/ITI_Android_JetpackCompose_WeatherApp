package com.iti.weatherapp.presentation.utils.oem

import android.content.Context
import android.content.Intent

class MiuiAutoStartStrategy(
    private val hasAskedAutoStart : Boolean
) : OemPermissionStrategy {

    override fun requiresPermission(context: Context): Boolean {
        return OemUtils.isMiui() && !hasAskedAutoStart
    }

    override fun getPermissionIntent(context: Context): Intent {
        return OemUtils.getMiuiAutoStartIntent(context)
    }

    override fun getToastMessage(context: Context): String {
        return "Please allow WeatherApp to Auto Start so alarms ring reliably."
    }

    override fun isPermissionGranted(context: Context): Boolean {
        return true
    }
}