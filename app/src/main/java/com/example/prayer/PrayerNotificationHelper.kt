package com.example.prayer

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.R
import com.example.model.PrayerSchedule
import com.example.model.PrayerType

object PrayerNotificationHelper {

  const val CHANNEL_ID = "channel_waktu_solat_azan"
  const val CHANNEL_NAME = "Pemberitahuan Waktu Solat & Azan"

  fun createNotificationChannel(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
      val existingChannel = notificationManager.getNotificationChannel(CHANNEL_ID)
      if (existingChannel == null) {
        val channel = NotificationChannel(
          CHANNEL_ID,
          CHANNEL_NAME,
          NotificationManager.IMPORTANCE_HIGH
        ).apply {
          description = "Pemberitahuan masuknya waktu solat dan seruan azan"
          enableLights(true)
          lightColor = Color.GREEN
          enableVibration(true)
          vibrationPattern = longArrayOf(0, 500, 250, 500)
          val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
          setSound(
            defaultSoundUri,
            AudioAttributes.Builder()
              .setUsage(AudioAttributes.USAGE_NOTIFICATION_EVENT)
              .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
              .build()
          )
        }
        notificationManager.createNotificationChannel(channel)
      }
    }
  }

  fun showPrayerNotification(
    context: Context,
    prayerType: PrayerType,
    zoneName: String,
    timeFormatted: String
  ) {
    createNotificationChannel(context)
    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    val openAppIntent = Intent(context, MainActivity::class.java).apply {
      flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    }
    val pendingIntent = PendingIntent.getActivity(
      context,
      prayerType.ordinal,
      openAppIntent,
      PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val subuhMessage = if (prayerType == PrayerType.SUBUH) " (الصَّلَاةُ خَيْرٌ مِنَ النَّوْمِ)" else ""
    val title = "Masuk Waktu ${prayerType.displayName} ($timeFormatted)"
    val content = "Telah masuk waktu solat ${prayerType.displayName} bagi zon ${zoneName}.$subuhMessage Marilah mendirikan solat."

    val notification = NotificationCompat.Builder(context, CHANNEL_ID)
      .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
      .setContentTitle(title)
      .setContentText(content)
      .setStyle(NotificationCompat.BigTextStyle().bigText(content))
      .setPriority(NotificationCompat.PRIORITY_HIGH)
      .setCategory(NotificationCompat.CATEGORY_ALARM)
      .setAutoCancel(true)
      .setContentIntent(pendingIntent)
      .setColor(0xFF047857.toInt()) // Emerald color
      .build()

    notificationManager.notify(prayerType.ordinal + 100, notification)
  }

  fun scheduleAlarms(context: Context, schedule: PrayerSchedule, enabledPrayers: Set<PrayerType>) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val now = System.currentTimeMillis()

    for (item in schedule.items) {
      if (!item.type.isFardhu) continue
      if (!enabledPrayers.contains(item.type)) continue

      if (item.timestampEpoch > now) {
        val intent = Intent(context, PrayerAlarmReceiver::class.java).apply {
          putExtra(PrayerAlarmReceiver.EXTRA_PRAYER_TYPE, item.type.name)
          putExtra(PrayerAlarmReceiver.EXTRA_ZONE_NAME, schedule.zoneName)
          putExtra(PrayerAlarmReceiver.EXTRA_TIME_FORMATTED, item.timeFormatted)
        }

        val pendingIntent = PendingIntent.getBroadcast(
          context,
          item.type.ordinal,
          intent,
          PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
          if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(
              AlarmManager.RTC_WAKEUP,
              item.timestampEpoch,
              pendingIntent
            )
          } else {
            alarmManager.setExact(
              AlarmManager.RTC_WAKEUP,
              item.timestampEpoch,
              pendingIntent
            )
          }
        } catch (e: SecurityException) {
          // SCHEDULE_EXACT_ALARM permission fallback
          alarmManager.set(
            AlarmManager.RTC_WAKEUP,
            item.timestampEpoch,
            pendingIntent
          )
        }
      }
    }
  }
}
