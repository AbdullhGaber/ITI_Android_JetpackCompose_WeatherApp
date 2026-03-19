package com.iti.weatherapp.presentation.screens.home.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import kotlin.random.Random

@Composable
fun DynamicWeatherBackground(weatherCode: String?, modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    val isDarkTheme = isSystemInDarkTheme()

    val isThunderstorm = weatherCode?.startsWith("11") == true
    val isRain = weatherCode?.startsWith("09") == true || weatherCode?.startsWith("10") == true
    val isSnow = weatherCode?.startsWith("13") == true

    Box(modifier = modifier.fillMaxSize()) {
        if (weatherCode != null) {
            if (isRain || isThunderstorm) {
                RainParticles(isDarkTheme)
            } else if (isSnow) {
                SnowParticles(isDarkTheme)
            }
        }

        content()
    }
}

@Composable
fun RainParticles(isDarkTheme: Boolean) {
    val infiniteTransition = rememberInfiniteTransition(label = "rain_transition")
    val offset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rain_offset"
    )

    val randoms = remember { List(40) { Random(it) } }

    Canvas(modifier = Modifier.fillMaxSize()) {
        val dropHeight = 60f
        val dropColor = if (isDarkTheme) Color.White.copy(alpha = 0.4f) else Color(0xFF455A64).copy(alpha = 0.6f)
        randoms.forEach { r ->
            val x = r.nextFloat() * size.width
            val baseSpeed = r.nextFloat() * 0.5f + 0.5f
            val yMod = (offset * size.height * baseSpeed + r.nextFloat() * size.height) % size.height
            
            drawLine(
                color = dropColor,
                start = Offset(x, yMod),
                end = Offset(x, yMod + dropHeight),
                strokeWidth = 3f
            )
        }
    }
}

@Composable
fun SnowParticles(isDarkTheme: Boolean) {
    val infiniteTransition = rememberInfiniteTransition(label = "snow_transition")
    val offset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "snow_offset"
    )

    val randoms = remember { List(50) { Random(it) } }

    Canvas(modifier = Modifier.fillMaxSize()) {
        val snowColorBase = if (isDarkTheme) Color.White else Color.DarkGray
        randoms.forEachIndexed { index, r ->
            val x = r.nextFloat() * size.width
            val baseSpeed = r.nextFloat() * 0.5f + 0.5f
            val yMod = (offset * size.height * baseSpeed + r.nextFloat() * size.height) % size.height
            val sway = kotlin.math.sin((offset * Math.PI * 2f + r.nextFloat() * Math.PI).toFloat()) * 20f

            drawCircle(
                color = snowColorBase.copy(alpha = r.nextFloat() * 0.5f + if (isDarkTheme) 0.3f else 0.5f),
                radius = r.nextFloat() * 5f + 3f,
                center = Offset(x + sway.toFloat(), yMod)
            )
        }
    }
}
