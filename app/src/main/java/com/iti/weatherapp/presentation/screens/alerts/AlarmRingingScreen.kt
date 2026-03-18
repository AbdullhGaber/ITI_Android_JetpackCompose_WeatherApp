package com.iti.weatherapp.presentation.screens.alerts

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Snooze
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.iti.weatherapp.R
import com.iti.weatherapp.presentation.components.LottieIconTextView
import com.iti.weatherapp.presentation.ui.theme.WeatherAppTheme

@Composable
fun AlarmRingingScreen(
    alertId: Int,
    viewModel: AlarmRingingViewModel = hiltViewModel(),
    onFinishActivity: () -> Unit
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F172A))
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(Color(0xFF1E293B)),
            contentAlignment = Alignment.Center
        ) {
            LottieIconTextView(
                modifier = Modifier.size(64.dp),
                animationResId = R.raw.on_boarding_3,
                message = "Weather Alert!"
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Check the latest weather conditions.",
            fontSize = 18.sp,
            color = Color(0xFF94A3B8),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp)
        )

        Spacer(modifier = Modifier.height(64.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Column(
                modifier = Modifier.width(72.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                FloatingActionButton(
                    onClick = {
                        viewModel.dismissAlarm(context)
                        onFinishActivity()
                    },
                    containerColor = Color(0xFFF44336),
                    contentColor = Color.White,
                    modifier = Modifier.size(72.dp),
                    shape = CircleShape
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Dismiss", modifier = Modifier.size(32.dp))
                }
                Text(
                    text = "Dismiss",
                    color = Color.White,
                    modifier = Modifier
                        .padding(top = 12.dp)
                        .wrapContentWidth(unbounded = true) // Allow safe overflow
                )
            }

            Column(
                modifier = Modifier.width(72.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                FloatingActionButton(
                    onClick = {
                        viewModel.snoozeAlarm(context, alertId)
                        onFinishActivity()
                    },
                    containerColor = Color(0xFF4A90E2),
                    contentColor = Color.White,
                    modifier = Modifier.size(72.dp),
                    shape = CircleShape
                ) {
                    Icon(Icons.Default.Snooze, contentDescription = "Snooze", modifier = Modifier.size(32.dp))
                }
                Text(
                    text = "Snooze +10m",
                    color = Color.White,
                    modifier = Modifier
                        .padding(top = 12.dp)
                        .wrapContentWidth(unbounded = true)
                )
            }
        }
    }
}

@Preview
@Composable
private fun PreviewAlarmRingingScreen() {
    WeatherAppTheme {
        AlarmRingingScreen(
            alertId = 0,
            onFinishActivity = {}
        )
    }
}