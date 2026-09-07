package com.example.prayer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Vibrator
import android.os.VibratorManager
import android.os.VibrationEffect
import android.os.Build
import android.util.Log
import com.example.audio.AzanAudioManager
import com.example.audio.AzanRepository
import com.example.data.PrayerPreferences
import com.example.model.PrayerType

class PrayerAlarmReceiver : BroadcastReceiver() {

  override fun onReceive(context: Context, intent: Intent) {
    val prayerTypeName = intent.getStringExtra(EXTRA_PRAYER_TYPE) ?: return
    val zoneName = intent.getStringExtra(EXTRA_ZONE_NAME) ?: "Kawasan Anda"
    val timeFormatted = intent.getStringExtra(EXTRA_TIME_FORMATTED) ?: ""

    val prayerType = try {
      PrayerType.valueOf(prayerTypeName)
    } catch (e: Exception) {
      PrayerType.SUBUH
    }

    Log.d("PrayerAlarmReceiver", "Received prayer alarm for $prayerTypeName at $timeFormatted")

    // Show Notification
    PrayerNotificationHelper.showPrayerNotification(context, prayerType, zoneName, timeFormatted)

    // Trigger Haptic Vibration
    triggerVibration(context)

    // Check preferences and play Azan sound if enabled
    val prefs = PrayerPreferences(context)
    val isNotifEnabled = prefs.isPrayerNotificationEnabled(prayerType)
    if (isNotifEnabled) {
      val azanId = if (prayerType == PrayerType.SUBUH) prefs.subuhAzanId else prefs.defaultAzanId
      val azan = AzanRepository.PRESET_AZANS.find { it.id == azanId } ?: AzanRepository.PRESET_AZANS.first()
      val audioManager = AzanAudioManager(context)
      audioManager.playAzan(azan, volume = prefs.azanVolume)
    }
  }

  private fun triggerVibration(context: Context) {
    try {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
        vibratorManager?.defaultVibrator?.vibrate(
          VibrationEffect.createWaveform(longArrayOf(0, 500, 200, 500), -1)
        )
      } else {
        @Suppress("DEPRECATION")
        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
          vibrator?.vibrate(VibrationEffect.createWaveform(longArrayOf(0, 500, 200, 500), -1))
        } else {
          @Suppress("DEPRECATION")
          vibrator?.vibrate(longArrayOf(0, 500, 200, 500), -1)
        }
      }
    } catch (e: Exception) {
      Log.e("PrayerAlarmReceiver", "Vibration failed: ${e.message}")
    }
  }

  companion object {
    const val EXTRA_PRAYER_TYPE = "extra_prayer_type"
    const val EXTRA_ZONE_NAME = "extra_zone_name"
    const val EXTRA_TIME_FORMATTED = "extra_time_formatted"
  }
}
