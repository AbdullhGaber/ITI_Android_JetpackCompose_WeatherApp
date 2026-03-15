package com.iti.weatherapp.presentation.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable


// Define new AppTheme enum for ViewModel interaction
enum class AppTheme { LIGHT, DARK, SYSTEM_DEFAULT }

private val LightColorScheme = lightColorScheme(
    primary = Primary_Blue,
    background = Background_Light,
    surface = Surface_Light,
    onSurface = Text_Primary_Light,
    onSurfaceVariant = Text_Secondary_Light,
    tertiary = Accent_Amber,
    outline = Border_Light,
    secondaryContainer = Primary_Blue.copy(alpha = 0.1f), // For selected bubbles
)

private val DarkColorScheme = darkColorScheme(
    primary = Primary_Blue,
    background = Background_Dark,
    surface = Surface_Dark,
    onSurface = Text_Primary_Dark,
    onSurfaceVariant = Text_Secondary_Dark,
    tertiary = Accent_Amber,
    outline = Border_Dark,
    secondaryContainer = Primary_Blue.copy(alpha = 0.2f), // For selected bubbles
)

@Composable
fun WeatherAppTheme(
    appTheme: AppTheme = AppTheme.SYSTEM_DEFAULT,
    content: @Composable () -> Unit
) {
    // Determine the color scheme based on the selected setting
    val colorScheme = when (appTheme) {
        AppTheme.SYSTEM_DEFAULT -> if (isSystemInDarkTheme()) DarkColorScheme else LightColorScheme
        AppTheme.LIGHT -> LightColorScheme
        AppTheme.DARK -> DarkColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}