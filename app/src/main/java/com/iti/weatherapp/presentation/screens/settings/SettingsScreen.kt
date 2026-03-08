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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.iti.weatherapp.presentation.ui.theme.AppTheme

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

    AnimatedVisibility(
        visible = true,
        enter = fadeIn(animationSpec = tween(durationMillis = 600, delayMillis = 100)),
        exit = fadeOut(tween(durationMillis = 300))
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 100.dp)
        ) {
            item {
                Text(
                    text = "Settings",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            item {
                ThemedSettingsCard(title = "Location Settings") {
                    Text(
                        text = "Settings settings for your location settings on, use GPS, notify to an near location settings.",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Use GPS", fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurface)
                        Switch(
                            checked = locationMethod == "gps",
                            onCheckedChange = { viewModel.setLocationMethod(if (it) "gps" else "map") },
                            colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = MaterialTheme.colorScheme.primary)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Embedded Map Button (Creative pulse animation requested)
                    val infiniteTransition = rememberInfiniteTransition()
                    val pulseScale by infiniteTransition.animateFloat(
                        initialValue = 1f,
                        targetValue = 1.05f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(1200, easing = LinearEasing),
                            repeatMode = RepeatMode.Reverse
                        )
                    )

                    OutlinedButton(
                        onClick = { onNavigateToMap() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .graphicsLayer(scaleX = pulseScale, scaleY = pulseScale)
                            .padding(top = 8.dp),
                        shape = RoundedCornerShape(20.dp),
                        border = BorderStroke(width = 1.dp, color =  MaterialTheme.colorScheme.outline)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Place, contentDescription = null, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(text = "Select from Map", fontSize = 16.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }

            // --- Card 2: Unit Preferences (Ref image 6 and image 1 rounded look) ---
            item {
                ThemedSettingsCard(title = "Unit Preferences") {
                    // Temperature Segment Row (Matches curved look in image_1.png cards)
                    UnitPreferenceSegmentedRow(
                        title = "Temperature",
                        options = listOf("°C", "°F", "K"),
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

                    // Wind Speed Segment Row (Matches curved look in image_1.png cards)
                    UnitPreferenceSegmentedRow(
                        title = "Wind Speed",
                        options = listOf("m/s", "mph"),
                        selectedOptionIndex = if (windUnit == "miles_hour") 1 else 0,
                        onOptionSelected = { index ->
                            viewModel.setWindUnit(if (index == 1) "miles_hour" else "meter_sec")
                        }
                    )
                }
            }

            // --- Card 3: Language & Theme (Ref requirements: 3 options with creative UX) ---
            item {
                ThemedSettingsCard(title = "Theme & Language") {
                    Text(text = "App Appearance", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(bottom = 12.dp))

                    // App Theme with the third "System Default" option
                    val themeOptions = listOf(
                        SettingOption(AppTheme.LIGHT, "Light Mode"),
                        SettingOption(AppTheme.DARK, "Dark Mode"),
                        SettingOption(AppTheme.SYSTEM_DEFAULT, "Use Device Settings")
                    )

                    themeOptions.forEach { option ->
                        SettingsRadioButton(
                            label = option.label,
                            selected = appTheme == option.value
                        ) { viewModel.setTheme(option.value) }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), thickness = 1.dp)
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(text = "Language", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(bottom = 12.dp))

                    // Language with the third "System Language" option
                    val languageOptions = listOf(
                        SettingOption("en", "English"),
                        SettingOption("ar", "Arabic"),
                        SettingOption("System Language", "Use Device Language")
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

// Internal data structure for Setting Options
data class SettingOption<T>(val value: T, val label: String)

// Shared UI Helper for Cards (Ref image_1.png rounded, large shape)
@Composable
fun ThemedSettingsCard(title: String, content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp), // LARGE curved look matching image_1.png cards
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

// Curved, segmented UI component for Unit Preferences (Matches image_6.png bubble look)
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
                .clip(RoundedCornerShape(20.dp)) // Large curves replicate image_6.png bubbles
                .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)) // Background of the segmented row
                .padding(4.dp), // Internal padding of the row
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            options.forEachIndexed { index, option ->
                val isSelected = index == selectedOptionIndex
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(16.dp)) // Internal bubble curve replicate image_6.png look
                        .background(if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent)
                        // Handled by Row's weight, just applying background here
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