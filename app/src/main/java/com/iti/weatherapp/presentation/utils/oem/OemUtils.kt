package com.iti.weatherapp.presentation.utils.oem

import android.content.Context
import android.content.Intent
import android.os.Build
import android.text.TextUtils
import android.util.Log
import java.io.BufferedReader
import java.io.InputStreamReader

object OemUtils {

    fun isMiui(): Boolean {
        return !TextUtils.isEmpty(getSystemProperty("ro.miui.ui.version.name")) ||
               !TextUtils.isEmpty(getSystemProperty("ro.miui.ui.version.code")) ||
               Build.MANUFACTURER.equals("Xiaomi", ignoreCase = true)
    }

    private fun getSystemProperty(propName: String): String? {
        val line: String
        var input: BufferedReader? = null
        try {
            val p = Runtime.getRuntime().exec("getprop $propName")
            input = BufferedReader(InputStreamReader(p.inputStream), 1024)
            line = input.readLine()
            input.close()
        } catch (ex: Exception) {
            Log.e("OemUtils", "Unable to read sysprop $propName", ex)
            return null
        } finally {
            input?.close()
        }
        return line
    }

    fun getMiuiSpecialPermissionsIntent(context: Context): Intent {
        return Intent("miui.intent.action.APP_PERM_EDITOR").apply {
            setClassName(
                "com.miui.securitycenter",
                "com.miui.permcenter.permissions.PermissionsEditorActivity"
            )
            putExtra("extra_pkgname", context.packageName)
        }
    }

    fun getMiuiAutoStartIntent(context: Context): Intent {
        return Intent().apply {
            setClassName(
                "com.miui.securitycenter",
                "com.miui.permcenter.autostart.AutoStartManagementActivity"
            )
        }
    }
}