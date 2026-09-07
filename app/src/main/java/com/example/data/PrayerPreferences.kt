package com.example.data

import android.content.Context
import android.content.SharedPreferences
import com.example.model.PrayerType

class PrayerPreferences(context: Context) {
  private val prefs: SharedPreferences =
    context.getSharedPreferences("waktu_solat_prefs", Context.MODE_PRIVATE)

  var selectedZoneCode: String
    get() = prefs.getString("selected_zone_code", "WLY01") ?: "WLY01"
    set(value) = prefs.edit().putString("selected_zone_code", value).apply()

  var isAutoGpsEnabled: Boolean
    get() = prefs.getBoolean("auto_gps_enabled", false)
    set(value) = prefs.edit().putBoolean("auto_gps_enabled", value).apply()

  var defaultAzanId: String
    get() = prefs.getString("default_azan_id", "makkah_ali_mulla") ?: "makkah_ali_mulla"
    set(value) = prefs.edit().putString("default_azan_id", value).apply()

  var subuhAzanId: String
    get() = prefs.getString("subuh_azan_id", "subuh_makkah") ?: "subuh_makkah"
    set(value) = prefs.edit().putString("subuh_azan_id", value).apply()

  var azanVolume: Float
    get() = prefs.getFloat("azan_volume", 0.9f)
    set(value) = prefs.edit().putFloat("azan_volume", value).apply()

  fun isPrayerNotificationEnabled(type: PrayerType): Boolean {
    return prefs.getBoolean("notif_${type.name}", true)
  }

  fun setPrayerNotificationEnabled(type: PrayerType, enabled: Boolean) {
    prefs.edit().putBoolean("notif_${type.name}", enabled).apply()
  }

  fun getEnabledPrayers(): Set<PrayerType> {
    return PrayerType.values()
      .filter { it.isFardhu && isPrayerNotificationEnabled(it) }
      .toSet()
  }
}
