package com.iti.weatherapp.presentation.screens.alerts.components


import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iti.weatherapp.presentation.ui.theme.WeatherAppTheme
import kotlin.math.cos
import kotlin.math.sin
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable

@Composable
fun WindSpeedGauge(
    modifier: Modifier = Modifier,
    currentValue: Float,
    maxValue: Float = 50f,
    onValueChange: (Float) -> Unit
) {
    val sweepGradient = Brush.horizontalGradient(
        colors = listOf(
            Color(0xFF4CAF50),
            Color(0xFFFFEB3B),
            Color(0xFFF44336)
        )
    )

    val surfaceVariant = MaterialTheme.colorScheme.surfaceVariant
    val onSurface = MaterialTheme.colorScheme.onSurface

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .aspectRatio(2f),
            contentAlignment = Alignment.BottomCenter
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val strokeWidth = 30.dp.toPx()
                val radius = size.width / 2 - strokeWidth / 2
                val center = Offset(size.width / 2, size.height) // Bottom center

                drawArc(
                    color = surfaceVariant,
                    startAngle = 180f,
                    sweepAngle = 180f,
                    useCenter = false,
                    topLeft = Offset(strokeWidth / 2, size.height - radius),
                    size = Size(radius * 2, radius * 2),
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )

                val filledAngle = (currentValue / maxValue).coerceIn(0f, 1f) * 180f
                drawArc(
                    brush = sweepGradient,
                    startAngle = 180f,
                    sweepAngle = filledAngle,
                    useCenter = false,
                    topLeft = Offset(strokeWidth / 2, size.height - radius),
                    size = Size(radius * 2, radius * 2),
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )

                val angleInRadians = Math.toRadians((180f + filledAngle).toDouble())
                val needleLength = radius - 10.dp.toPx()
                val indicatorX = center.x + needleLength * cos(angleInRadians).toFloat()
                val indicatorY = center.y + needleLength * sin(angleInRadians).toFloat()

                drawLine(
                    color = Color.Red,
                    start = center,
                    end = Offset(indicatorX, indicatorY),
                    strokeWidth = 8.dp.toPx(),
                    cap = StrokeCap.Round
                )

                drawCircle(
                    color = onSurface,
                    radius = 12.dp.toPx(),
                    center = center
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Text(
                    text = "${currentValue.toInt()}",
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "m/s",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(0.8f),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.Bottom
        ) {
            Surface(
                modifier = Modifier
                    .width(80.dp)
                    .height(60.dp)
                    .clickable {
                        val newValue = (currentValue - 1f).coerceAtLeast(0f)
                        onValueChange(newValue)
                    },
                shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp, bottomStart = 24.dp, bottomEnd = 24.dp),
                color = MaterialTheme.colorScheme.errorContainer
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Remove,
                        contentDescription = "Brake",
                        tint = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }

            Surface(
                modifier = Modifier
                    .width(60.dp)
                    .height(100.dp)
                    .clickable {
                        val newValue = (currentValue + 1f).coerceAtMost(maxValue)
                        onValueChange(newValue)
                    },
                shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp, bottomStart = 16.dp, bottomEnd = 16.dp),
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Accelerate",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }
    }
}
@Preview
@Composable
private fun PreviewWindSpeedGauge() {
    WeatherAppTheme {
        val cur = remember { mutableFloatStateOf(0f) }
        WindSpeedGauge(
            modifier = Modifier,
            currentValue = cur.floatValue,
            onValueChange = {cur.floatValue = it}
        )
    }
}