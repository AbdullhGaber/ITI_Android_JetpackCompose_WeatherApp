package com.iti.weatherapp.presentation.screens.settings

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.iti.weatherapp.R
import com.iti.weatherapp.presentation.ui.theme.AppTheme
import androidx.compose.foundation.lazy.rememberLazyListState

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel(),
    onNavigateToMap: () -> Unit = {}
) {
    val locationMethod by viewModel.locationMethodState.collectAsState()
    val tempUnit by viewModel.tempUnitState.collectAsState()
    val windUnit by viewModel.windUnitState.collectAsState()
    val appTheme by viewModel.themeState.collectAsState()
    val language by viewModel.languageState.collectAsState()

    val listState = rememberLazyListState()

    AnimatedContent(
        targetState = appTheme,
        transitionSpec = {
            fadeIn(animationSpec = tween(2500)) togetherWith fadeOut(animationSpec = tween(2500))
        },
        label = "ThemeTransition"
    ) { currentTheme ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 100.dp)
            ) {
                item {
                    Text(
                        text = stringResource(R.string.settings_title),
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                item {
                    ThemedSettingsCard(title = stringResource(R.string.location_settings)) {
                        Text(
                            text = stringResource(R.string.location_desc),
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = stringResource(R.string.use_gps), fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurface)
                            Switch(
                                checked = locationMethod == "gps",
                                onCheckedChange = { viewModel.setLocationMethod(if (it) "gps" else "map") },
                                colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = MaterialTheme.colorScheme.primary)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        val infiniteTransition = rememberInfiniteTransition(label = "pulse")
                        val pulseScale by infiniteTransition.animateFloat(
                            initialValue = 1f,
                            targetValue = 1.05f,
                            animationSpec = infiniteRepeatable(
                                animation = tween(1200, easing = LinearEasing),
                                repeatMode = RepeatMode.Reverse
                            ),
                            label = "pulseScale"
                        )

                        OutlinedButton(
                            onClick = { onNavigateToMap() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .graphicsLayer(scaleX = pulseScale, scaleY = pulseScale)
                                .padding(top = 8.dp),
                            shape = RoundedCornerShape(20.dp),
                            border = BorderStroke(width = 1.dp, color = MaterialTheme.colorScheme.outline)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.Place, contentDescription = null, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(text = stringResource(R.string.select_map), fontSize = 16.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }
                }

                item {
                    ThemedSettingsCard(title = stringResource(R.string.unit_preferences)) {
                        UnitPreferenceSegmentedRow(
                            title = stringResource(R.string.temperature),
                            options = listOf(stringResource(R.string.temp_c), stringResource(R.string.temp_f), stringResource(R.string.temp_k)),
                            selectedOptionIndex = when (tempUnit) {
                                "imperial" -> 1
                                "standard" -> 2
                                else -> 0
                            },
                            onOptionSelected = { index ->
                                viewModel.setTempUnit(when (index) {
                                    1 -> "imperial"
                                    2 -> "standard"
                                    else -> "metric"
                                })
                            }
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        UnitPreferenceSegmentedRow(
                            title = stringResource(R.string.wind_speed),
                            options = listOf(stringResource(R.string.wind_ms), stringResource(R.string.wind_mph)),
                            selectedOptionIndex = if (windUnit == "miles_hour") 1 else 0,
                            onOptionSelected = { index ->
                                viewModel.setWindUnit(if (index == 1) "miles_hour" else "meter_sec")
                            }
                        )
                    }
                }

                item {
                    ThemedSettingsCard(title = stringResource(R.string.theme_language)) {
                        Text(
                            text = stringResource(R.string.app_appearance),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )

                        val themeOptions = listOf(
                            SettingOption(AppTheme.LIGHT, stringResource(R.string.light_mode)),
                            SettingOption(AppTheme.DARK, stringResource(R.string.dark_mode)),
                            SettingOption(AppTheme.SYSTEM_DEFAULT, stringResource(R.string.device_settings))
                        )

                        themeOptions.forEach { option ->
                            SettingsRadioButton(
                                label = option.label,
                                selected = currentTheme == option.value
                            ) { viewModel.setTheme(option.value) }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), thickness = 1.dp)
                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = stringResource(R.string.language),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )

                        val languageOptions = listOf(
                            SettingOption("en", stringResource(R.string.english)),
                            SettingOption("ar", stringResource(R.string.arabic)),
                            SettingOption("System Language", stringResource(R.string.device_language))
                        )

                        languageOptions.forEach { option ->
                            SettingsRadioButton(
                                label = option.label,
                                selected = language == option.value
                            ) { viewModel.setLanguage(option.value) }
                        }
                    }
                }
            }
        }
    }
}

data class SettingOption<T>(val value: T, val label: String)

@Composable
fun ThemedSettingsCard(title: String, content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = title, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(bottom = 16.dp))
            content()
        }
    }
}

@Composable
fun ColumnScope.SettingsRadioButton(label: String, selected: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = onClick,
            colors = RadioButtonDefaults.colors(selectedColor = MaterialTheme.colorScheme.primary)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(text = label, fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurface)
    }
}

@Composable
fun UnitPreferenceSegmentedRow(
    title: String,
    options: List<String>,
    selectedOptionIndex: Int,
    onOptionSelected: (Int) -> Unit
) {
    Column {
        Text(
            text = title,
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            options.forEachIndexed { index, option ->
                val isSelected = index == selectedOptionIndex
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent)
                        .clickable { onOptionSelected(index) }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = option,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}