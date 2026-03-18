package com.iti.weatherapp.presentation.utils.oem

import android.content.Context
import android.content.Intent

interface OemPermissionStrategy {
    fun requiresPermission(context: Context): Boolean

    fun getPermissionIntent(context: Context): Intent
    
    fun getToastMessage(context: Context): String

    fun isPermissionGranted(context: Context): Boolean
}