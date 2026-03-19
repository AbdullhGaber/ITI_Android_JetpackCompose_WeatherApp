package com.iti.weatherapp.presentation.utils.alarm_notifications

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import androidx.core.app.NotificationCompat
import com.iti.weatherapp.R
import com.iti.weatherapp.presentation.MainActivity
import com.iti.weatherapp.presentation.receivers.AlarmCancelActionReceiver
import com.iti.weatherapp.presentation.receivers.AlarmSnoozeActionReceiver
import com.iti.weatherapp.presentation.utils.Constants.EXTRA_ALERT_ID
import com.iti.weatherapp.presentation.utils.Constants.IS_ALARM_FIRING
import com.iti.weatherapp.presentation.utils.Constants.NAVIGATE_TO_ALERTS
import kotlin.jvm.java

object NotificationHelper {
    const val ALARM_CHANNEL_ID = "weather_alarm_channel_v2"
    private const val NOTIFICATION_CHANNEL_ID = "weather_standard_channel"

    fun createNotificationChannels(context: Context) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val alarmAudioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_ALARM)
            .build()

        val alarmChannel = NotificationChannel(ALARM_CHANNEL_ID, "Weather Alarms", NotificationManager.IMPORTANCE_HIGH).apply {
            description = "High priority pinned alarms"
            setSound(null, alarmAudioAttributes)
            setBypassDnd(true)
        }
        val standardChannel = NotificationChannel(NOTIFICATION_CHANNEL_ID, "Weather Notifications", NotificationManager.IMPORTANCE_DEFAULT).apply {
            description = "Standard weather updates"
            setSound(null, null)
        }

        manager.createNotificationChannel(alarmChannel)
        manager.createNotificationChannel(standardChannel)
    }

    private fun getMainActivityPendingIntent(context: Context): PendingIntent {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra(NAVIGATE_TO_ALERTS, true)
        }
        return PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
    }

    private fun getAlertWakeupScreenPendingIntent(context: Context, alertId : Int): PendingIntent {
        val fullScreenIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra(IS_ALARM_FIRING, true)
            putExtra(EXTRA_ALERT_ID, alertId)
        }
        val fullScreenPendingIntent = PendingIntent.getActivity(
            context, alertId, fullScreenIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return fullScreenPendingIntent
    }

    fun buildAlarmNotification(context: Context, alertId: Int, title: String, message: String, isUpdate: Boolean = false) : Notification{
        createNotificationChannels(context)

        val cancelIntent = Intent(context, AlarmCancelActionReceiver::class.java).apply {
            putExtra(EXTRA_ALERT_ID, alertId)
        }
        val cancelPendingIntent = PendingIntent.getBroadcast(
            context, alertId, cancelIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val snoozeIntent = Intent(context, AlarmSnoozeActionReceiver::class.java).apply {
            putExtra(EXTRA_ALERT_ID, alertId)
        }

        val snoozePendingIntent = PendingIntent.getBroadcast(
            context, alertId + 10000, snoozeIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, ALARM_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setOngoing(true)
            .setAutoCancel(false)
            .setContentIntent(getMainActivityPendingIntent(context))
            .setFullScreenIntent(getAlertWakeupScreenPendingIntent(context, alertId), true)
            .addAction(android.R.drawable.ic_popup_sync, context.getString(R.string.snooze), snoozePendingIntent)
            .addAction(android.R.drawable.ic_menu_close_clear_cancel, context.getString(R.string.cancel_alarm), cancelPendingIntent)
            .setDeleteIntent(cancelPendingIntent)
            .setOnlyAlertOnce(isUpdate)
            .build()

        return notification
    }

    fun showStandardNotification(context: Context, alertId: Int, title: String, message: String, isUpdate: Boolean = false) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notification = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(getMainActivityPendingIntent(context))
            .setOnlyAlertOnce(isUpdate)
            .build()

        notificationManager.notify(alertId, notification)
    }
}