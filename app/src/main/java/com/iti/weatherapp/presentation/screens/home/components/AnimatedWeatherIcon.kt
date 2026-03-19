package com.iti.weatherapp.presentation.screens.home.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun AnimatedWeatherIcon(weatherCode: String, modifier: Modifier = Modifier, iconSize: Dp = 64.dp) {
    val isDay = weatherCode.endsWith("d")
    val id = weatherCode.take(2)

    Box(modifier = modifier.size(iconSize)) {
        when (id) {
            "01" -> if (isDay) SunIcon() else MoonIcon()
            "02", "03", "04" -> {
                if (id == "02") {
                    if (isDay) {
                        Box(Modifier.fillMaxSize(0.6f)) { SunIcon() }
                    } else {
                        Box(Modifier.fillMaxSize(0.6f)) { MoonIcon() }
                    }
                }
                CloudIcon(color = if (id == "04") Color.Gray else Color.White)
            }
            "09", "10" -> {
                CloudIcon(Color.Gray)
                RainDropsIcon()
            }
            "11" -> {
                CloudIcon(Color.DarkGray)
                LightningIcon()
            }
            "13" -> {
                CloudIcon(Color.LightGray)
                SnowFlakesIcon()
            }
            "50" -> MistIcon()
            else -> if (isDay) SunIcon() else MoonIcon()
        }
    }
}

@Composable
fun SunIcon() {
    val infiniteTransition = rememberInfiniteTransition(label = "sun")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "sun_rotation"
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        val center = Offset(size.width / 2, size.height / 2)
        val radius = size.width / 4

        drawCircle(
            color = Color(0xFFFFB300),
            radius = radius,
            center = center
        )

        rotate(rotation, center) {
            for (i in 0 until 8) {
                rotate(i * 45f, center) {
                    drawLine(
                        color = Color(0xFFFFB300),
                        start = Offset(center.x, center.y - radius - size.width * 0.05f),
                        end = Offset(center.x, center.y - radius - size.width * 0.15f),
                        strokeWidth = size.width * 0.05f,
                        cap = StrokeCap.Round
                    )
                }
            }
        }
    }
}

@Composable
fun MoonIcon() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val center = Offset(size.width / 2, size.height / 2)
        val radius = size.width / 3

        drawCircle(
            color = Color(0xFFE0E0E0),
            radius = radius,
            center = center
        )
        // Draw inner circle to make crescent
        drawCircle(
            color = Color.Transparent, // We would normally use path subtraction, but overdrawing background color is tricky here
            // For simplicity, we just draw a simpler solid Moon since we have a dynamic background.
            // A quick crescent path:
            radius = radius * 0.8f,
            center = Offset(center.x + radius*0.3f, center.y - radius*0.3f)
        )
    }
}

@Composable
fun CloudIcon(color: Color = Color.White) {
    val infiniteTransition = rememberInfiniteTransition(label = "cloud")
    val bounce by infiniteTransition.animateFloat(
        initialValue = -0.02f,
        targetValue = 0.02f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "cloud_bounce"
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        val yOffset = size.height * bounce
        translate(top = yOffset) {
            val width = size.width
            val height = size.height
            // Cloud puffs
            drawCircle(color, radius = width * 0.2f, center = Offset(width * 0.3f, height * 0.6f))
            drawCircle(color, radius = width * 0.25f, center = Offset(width * 0.5f, height * 0.45f))
            drawCircle(color, radius = width * 0.18f, center = Offset(width * 0.7f, height * 0.6f))
            drawRoundRect(
                color = color,
                topLeft = Offset(width * 0.3f, height * 0.45f + width * 0.05f),
                size = Size(width * 0.4f, width * 0.28f),
                cornerRadius = CornerRadius(width * 0.14f)
            )
        }
    }
}

@Composable
fun RainDropsIcon() {
    val infiniteTransition = rememberInfiniteTransition(label = "rain")
    val offset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rain_drop"
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height
        val startY = height * 0.6f
        val dropHeight = height * 0.15f

        val drops = listOf(0.35f, 0.5f, 0.65f)
        drops.forEachIndexed { index, xPos ->
            val yMod = ((offset + index * 0.3f) % 1f) * height * 0.4f
            drawLine(
                color = Color(0xFF64B5F6),
                start = Offset(width * xPos, startY + yMod),
                end = Offset(width * xPos, startY + yMod + dropHeight),
                strokeWidth = width * 0.04f,
                cap = StrokeCap.Round
            )
        }
    }
}

@Composable
fun LightningIcon() {
    val infiniteTransition = rememberInfiniteTransition(label = "lightning")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "lightning_alpha"
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        val path = Path().apply {
            moveTo(size.width * 0.55f, size.height * 0.45f)
            lineTo(size.width * 0.35f, size.height * 0.7f)
            lineTo(size.width * 0.5f, size.height * 0.7f)
            lineTo(size.width * 0.45f, size.height * 0.95f)
            lineTo(size.width * 0.65f, size.height * 0.65f)
            lineTo(size.width * 0.5f, size.height * 0.65f)
            close()
        }
        drawPath(path, color = Color(0xFFFFEB3B).copy(alpha = if (alpha > 0.5f) 1f else 0f))
    }
}

@Composable
fun SnowFlakesIcon() {
    val infiniteTransition = rememberInfiniteTransition(label = "snow")
    val offset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "snow_drop"
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height
        val startY = height * 0.5f

        val drops = listOf(0.35f, 0.5f, 0.65f)
        drops.forEachIndexed { index, xPos ->
            val yMod = ((offset + index * 0.3f) % 1f) * height * 0.5f
            drawCircle(
                color = Color.White,
                radius = width * 0.03f,
                center = Offset(width * xPos, startY + yMod)
            )
        }
    }
}

@Composable
fun MistIcon() {
    val infiniteTransition = rememberInfiniteTransition(label = "mist")
    val offset by infiniteTransition.animateFloat(
        initialValue = -0.05f,
        targetValue = 0.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "mist_sway"
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height
        
        translate(left = offset * width) {
            drawLine(Color.LightGray, Offset(width * 0.2f, height * 0.4f), Offset(width * 0.8f, height * 0.4f), strokeWidth = width * 0.06f, cap = StrokeCap.Round)
            drawLine(Color.LightGray, Offset(width * 0.3f, height * 0.55f), Offset(width * 0.7f, height * 0.55f), strokeWidth = width * 0.06f, cap = StrokeCap.Round)
            drawLine(Color.LightGray, Offset(width * 0.1f, height * 0.7f), Offset(width * 0.9f, height * 0.7f), strokeWidth = width * 0.06f, cap = StrokeCap.Round)
        }
    }
}
