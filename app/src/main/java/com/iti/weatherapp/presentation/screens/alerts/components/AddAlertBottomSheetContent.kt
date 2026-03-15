package com.iti.weatherapp.presentation.screens.alerts.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Update
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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

@Composable
fun AddAlertBottomSheetContent(
    onSave: (WeatherAlert) -> Unit,
    onCancel: () -> Unit
) {
    val context = LocalContext.current

    val currentTimeSeconds = System.currentTimeMillis() / 1000
    var startTime by remember { mutableLongStateOf(currentTimeSeconds) }
    var endTime by remember { mutableLongStateOf(currentTimeSeconds + 86400) }
    var selectedType by remember { mutableStateOf(AlertType.NOTIFICATION) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text(
            text = stringResource(R.string.add_alert),
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        OutlinedTextField(
            value = WeatherFormatters.formatDateTimePicker(startTime),
            onValueChange = { },
            label = { Text(stringResource(R.string.start_time)) },
            readOnly = true,
            leadingIcon = { Icon(Icons.Default.Schedule, contentDescription = null) },
            modifier = Modifier.fillMaxWidth().clickable{
                showNativeDateTimePicker(context) { selectedTimestamp -> startTime = selectedTimestamp }
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
                modifier = Modifier.fillMaxWidth().clickable{
                    showNativeDateTimePicker(context) { selectedTimestamp -> endTime = selectedTimestamp }
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

        Text(text = "Notify me by:", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable { selectedType = AlertType.ALARM }) {
                RadioButton(selected = selectedType == AlertType.ALARM, onClick = { selectedType = AlertType.ALARM })
                Text(text = stringResource(R.string.alarm), color = MaterialTheme.colorScheme.onSurface)
            }
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable { selectedType = AlertType.NOTIFICATION }) {
                RadioButton(selected = selectedType == AlertType.NOTIFICATION, onClick = { selectedType = AlertType.NOTIFICATION })
                Text(text = stringResource(R.string.notification), color = MaterialTheme.colorScheme.onSurface)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = onCancel,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.errorContainer, contentColor = MaterialTheme.colorScheme.onErrorContainer)
            ) {
                Text(stringResource(R.string.cancel))
            }
            Button(
                onClick = {
                    onSave(
                        WeatherAlert(
                            startDateTimestamp = startTime,
                            endDateTimestamp = endTime,
                            alertType = selectedType
                        )
                    )
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text(stringResource(R.string.save))
            }
        }
    }
}
