package com.iti.weatherapp.presentation.screens.alerts.components

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Update
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iti.weatherapp.R
import com.iti.weatherapp.data.local.db.entities.AlertType
import com.iti.weatherapp.data.local.db.entities.WeatherAlert
import com.iti.weatherapp.presentation.screens.alerts.showNativeDateTimePicker
import com.iti.weatherapp.presentation.utils.WeatherFormatters
import com.iti.weatherapp.presentation.utils.validation.AlertValidator


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AddAlertBottomSheetContent(
    onSave: (WeatherAlert) -> Unit,
    onCancel: () -> Unit
) {
    val context = LocalContext.current

    var selectedSoundUri by remember { mutableStateOf<String?>(null) }
    var selectedSoundName by remember { mutableStateOf("Default Sound") }
    var windThreshold by remember { mutableFloatStateOf(10f) }

    val ringtonePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val uri: Uri? = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
                result.data?.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI, Uri::class.java)
            }else{
                @Suppress("DEPRECATION")
                result.data?.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI)
            }

            if (uri != null) {
                val ringtone = RingtoneManager.getRingtone(context, uri)
                selectedSoundName = ringtone.getTitle(context)
                selectedSoundUri = uri.toString()
            } else {
                selectedSoundName = "Silent"
                selectedSoundUri = ""
            }
        }
    }

    // Function to trigger the intent
    fun pickRingtone() {
        val intent = Intent(RingtoneManager.ACTION_RINGTONE_PICKER).apply {
            putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_NOTIFICATION)
            putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true)
            putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, true)
        }
        ringtonePickerLauncher.launch(intent)
    }

    val scrollState = rememberScrollState()

    val currentTimeSeconds = System.currentTimeMillis() / 1000
    var startTime by remember { mutableLongStateOf(currentTimeSeconds) }
    var endTime by remember { mutableLongStateOf(currentTimeSeconds + 86400) }


    var selectedType by remember { mutableStateOf(AlertType.NOTIFICATION) }

    var selectedConditions by remember { mutableStateOf(setOf<String>()) }
    var tempThreshold by remember { mutableFloatStateOf(35f) }

    val conditionsList = listOf(
        stringResource(R.string.high_temperature) to "🌡️",
        stringResource(R.string.low_temperature) to "🥶",
        stringResource(R.string.strong_wind) to "💨",
        stringResource(R.string.expected_rain) to "🌧️",
        stringResource(R.string.very_cloudy) to "☁️"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text(
            text = stringResource(R.string.add_alert),
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = stringResource(R.string.weather_condition),
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                conditionsList.forEach { (condition, icon) ->
                    SelectableChip(
                        text = condition,
                        iconText = icon,
                        isSelected = selectedConditions.contains(condition),
                        onClick = {
                            val newSet = selectedConditions.toMutableSet()
                            if (newSet.contains(condition)) newSet.remove(condition)
                            else newSet.add(condition)
                            selectedConditions = newSet
                        }
                    )
                }
            }
        }

        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.threshold_limit),
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "${tempThreshold.toInt()}°C",
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Slider(
                value = tempThreshold,
                onValueChange = { tempThreshold = it },
                valueRange = -10f..50f,
                colors = SliderDefaults.colors(
                    thumbColor = MaterialTheme.colorScheme.primary,
                    activeTrackColor = MaterialTheme.colorScheme.primary,
                    inactiveTrackColor = MaterialTheme.colorScheme.surfaceVariant
                )
            )

            Text(
                text = stringResource(R.string.wind_speed_threshold),
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            WindSpeedGauge(
                currentValue = windThreshold,
                maxValue = 50f,
                onValueChange = { windThreshold = it },
                modifier = Modifier.padding(horizontal = 32.dp)
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedTextField(
                value = WeatherFormatters.formatDateTimePicker(startTime),
                onValueChange = { },
                label = { Text(stringResource(R.string.start_time)) },
                readOnly = true,
                leadingIcon = { Icon(Icons.Default.Schedule, contentDescription = null) },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        showNativeDateTimePicker(context) { selectedTimestamp ->
                            startTime = selectedTimestamp
                        }
                    },
                colors = OutlinedTextFieldDefaults.colors(
                    disabledTextColor = MaterialTheme.colorScheme.onSurface,
                    disabledBorderColor = MaterialTheme.colorScheme.outline,
                    disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                enabled = false,
                interactionSource = remember { MutableInteractionSource() }
            )

            AnimatedVisibility(visible = selectedType == AlertType.NOTIFICATION) {
                OutlinedTextField(
                    value = WeatherFormatters.formatDateTimePicker(endTime),
                    onValueChange = { },
                    label = { Text(stringResource(R.string.end_time)) },
                    readOnly = true,
                    leadingIcon = { Icon(Icons.Default.Update, contentDescription = null) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            showNativeDateTimePicker(context) { selectedTimestamp ->
                                endTime = selectedTimestamp
                            }
                        },
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledTextColor = MaterialTheme.colorScheme.onSurface,
                        disabledBorderColor = MaterialTheme.colorScheme.outline,
                        disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    enabled = false,
                    interactionSource = remember { MutableInteractionSource() }
                )
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = stringResource(R.string.alert_type),
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SelectableChip(
                    text = stringResource(R.string.notification),
                    iconText = "🔔",
                    isSelected = selectedType == AlertType.NOTIFICATION,
                    modifier = Modifier.weight(1f),
                    onClick = { selectedType = AlertType.NOTIFICATION }
                )
                SelectableChip(
                    text = stringResource(R.string.alarm),
                    iconText = "⏰",
                    isSelected = selectedType == AlertType.ALARM,
                    modifier = Modifier.weight(1f),
                    onClick = { selectedType = AlertType.ALARM }
                )
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = stringResource(R.string.notification_sound),
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { pickRingtone() },
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = selectedSoundName, color = MaterialTheme.colorScheme.onSurface)
                    Icon(Icons.Default.MusicNote, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = onCancel,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer
                )
            ) {
                Text(stringResource(R.string.cancel))
            }

            Button(
                onClick = {
                    val alertToSave = WeatherAlert(
                        startDateTimestamp = startTime,
                        endDateTimestamp = endTime,
                        alertType = selectedType,
                        tempThreshold = tempThreshold,
                        windThreshold = windThreshold,
                        conditions = selectedConditions.toList(),
                        customSoundName = selectedSoundName,
                        customSoundUri = selectedSoundUri
                    )

                    val validationResult = AlertValidator.validateAlertTime(alertToSave)

                    if (validationResult.isSuccessful) {
                        onSave(alertToSave)
                    } else {
                        validationResult.errorMessageResId?.let { errorId ->
                            @SuppressLint("LocalContextGetResourceValueCall")
                            Toast.makeText(context, context.getString(errorId), Toast.LENGTH_SHORT).show()
                        }
                    }
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(stringResource(R.string.save))
            }
        }
    }
}