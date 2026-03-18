package com.iti.weatherapp.presentation.utils.oem

import android.content.Context
import android.content.Intent

class MiuiSpecialPermissionStrategy(
    private val hasAskedLockScreen : Boolean
) : OemPermissionStrategy {
    override fun requiresPermission(context: Context): Boolean{
        return OemUtils.isMiui() && !hasAskedLockScreen
    }

    override fun getPermissionIntent(context: Context): Intent {
        return OemUtils.getMiuiSpecialPermissionsIntent(context)
    }

    override fun getToastMessage(context: Context): String {
        return "Please enable 'Show on Lock screen' in the settings."
    }

    override fun isPermissionGranted(context: Context): Boolean {
        return true
    }
}