package com.iti.weatherapp.presentation.utils

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.net.Uri
import android.util.Log
import androidx.core.net.toUri

object AlarmSoundManager {
    private var mediaPlayer: MediaPlayer? = null

    /**
     * Plays the selected sound URI.
     * @param context The application context.
     * @param soundUriStr The custom URI string saved in the database.
     * @param isAlarm If true, plays on the ALARM stream (loud, loops).
     * If false, plays on the NOTIFICATION stream (plays once).
     */
    fun playSound(context: Context, soundUriStr: String?, isAlarm: Boolean) {
        stopSound() // Ensure any existing sound is stopped before starting a new one

        try {
            val uriToPlay: Uri = if (!soundUriStr.isNullOrEmpty()) {
                soundUriStr.toUri()
            } else {
                // Fallback to default system sound if none selected
                val defaultType = if (isAlarm) RingtoneManager.TYPE_ALARM else RingtoneManager.TYPE_NOTIFICATION
                RingtoneManager.getDefaultUri(defaultType)
            }

            mediaPlayer = MediaPlayer().apply {
                setDataSource(context, uriToPlay)

                val audioAttributes = AudioAttributes.Builder().apply {
                    if (isAlarm) {
                        setUsage(AudioAttributes.USAGE_ALARM)
                        setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    } else {
                        setUsage(AudioAttributes.USAGE_NOTIFICATION)
                        setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    }
                }.build()

                setAudioAttributes(audioAttributes)

                // Alarms should loop until dismissed, notifications play once
                isLooping = isAlarm

                prepare()
                start()
            }
        } catch (e: Exception) {
            Log.e("AlarmSoundManager", "Error playing sound: ${e.message}")
            playDefaultSound(context, isAlarm)
        }
    }

    private fun playDefaultSound(context: Context, isAlarm: Boolean) {
        try {
            val defaultType = if (isAlarm) RingtoneManager.TYPE_ALARM else RingtoneManager.TYPE_NOTIFICATION
            val defaultUri = RingtoneManager.getDefaultUri(defaultType)

            mediaPlayer = MediaPlayer().apply {
                setDataSource(context, defaultUri)
                val audioAttributes = AudioAttributes.Builder()
                    .setUsage(if (isAlarm) AudioAttributes.USAGE_ALARM else AudioAttributes.USAGE_NOTIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
                setAudioAttributes(audioAttributes)
                isLooping = isAlarm
                prepare()
                start()
            }
        } catch (e: Exception) {
            Log.e("AlarmSoundManager", "Fatal error playing default sound: ${e.message}")
        }
    }

    fun stopSound() {
        try {
            mediaPlayer?.let {
                if (it.isPlaying) {
                    it.stop()
                }
                it.release()
            }
        } catch (e: Exception) {
            Log.e("AlarmSoundManager", "Error stopping sound: ${e.message}")
        } finally {
            mediaPlayer = null
        }
    }
}