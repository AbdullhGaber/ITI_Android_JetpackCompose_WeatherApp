package com.iti.weatherapp.presentation.utils.oem

import android.content.Context
import javax.inject.Inject

class OemPermissionManager @Inject constructor() {
    private var sessionAutoStartAsked = false
    private var sessionLockScreenAsked = false

    fun markAsAsked(strategy: OemPermissionStrategy) {
        if (strategy is MiuiAutoStartStrategy) sessionAutoStartAsked = true
        if (strategy is MiuiSpecialPermissionStrategy) sessionLockScreenAsked = true
    }

    fun getRequiredPermissionStrategy(
        context: Context,
        hasAskedAutoStart: Boolean,
        hasAskedLockScreen: Boolean
    ): OemPermissionStrategy? {
        val autoStartAsked = sessionAutoStartAsked || hasAskedAutoStart
        val lockScreenAsked = sessionLockScreenAsked || hasAskedLockScreen

        val strategies = listOf(
            MiuiAutoStartStrategy(autoStartAsked),
            MiuiSpecialPermissionStrategy(lockScreenAsked),
        )

        return strategies.firstOrNull { it.requiresPermission(context) }
    }
}