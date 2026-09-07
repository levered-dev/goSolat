package com.example.model

enum class PrayerType(
  val displayName: String,
  val arabicName: String,
  val description: String,
  val isFardhu: Boolean
) {
  IMSAK("Imsak", "الإمساك", "Waktu menahan diri sebelum Subuh", false),
  SUBUH("Subuh", "الفجر", "Solat Fardhu 2 Rakaat", true),
  SYURUK("Syuruk", "الشروق", "Terbit fajar / matahari", false),
  ZOHOR("Zohor", "الظهر", "Solat Fardhu 4 Rakaat", true),
  ASAR("Asar", "العصر", "Solat Fardhu 4 Rakaat", true),
  MAGHRIB("Maghrib", "المغرب", "Solat Fardhu 3 Rakaat", true),
  ISYAK("Isyak", "العشاء", "Solat Fardhu 4 Rakaat", true)
}

data class PrayerItem(
  val type: PrayerType,
  val timeFormatted: String, // e.g. "05:54 AM" or "13:15"
  val time24: String, // e.g. "05:54"
  val timestampEpoch: Long,
  val isPassed: Boolean = false,
  val isCurrentOrNext: Boolean = false
)

data class PrayerSchedule(
  val dateGregorian: String,
  val dateHijri: String,
  val zoneCode: String,
  val zoneName: String,
  val state: String,
  val items: List<PrayerItem>
)

data class NextPrayerInfo(
  val nextPrayer: PrayerType,
  val nextPrayerTimeFormatted: String,
  val remainingHours: Long,
  val remainingMinutes: Long,
  val remainingSeconds: Long,
  val remainingMillis: Long,
  val progressToNext: Float // 0.0 to 1.0
)

data class PrayerZone(
  val code: String,
  val state: String,
  val name: String,
  val latitude: Double,
  val longitude: Double
)

data class WeatherData(
  val temperatureC: Double = 29.5,
  val conditionMs: String = "Cerah & Berawan",
  val weatherCode: Int = 1,
  val humidityPercent: Int = 78,
  val windSpeedKmh: Double = 12.0,
  val isDay: Boolean = true,
  val locationName: String = "Kuala Lumpur"
)

data class Hadith(
  val id: Int,
  val arabicText: String,
  val translationMs: String,
  val narrator: String,
  val sourceBook: String,
  val chapter: String
)

data class AzanSound(
  val id: String,
  val title: String,
  val muazzin: String,
  val origin: String,
  val youtubeId: String,
  val audioUrl: String,
  val duration: String,
  val isDownloaded: Boolean = false,
  val localFilePath: String? = null,
  val isSpecialSubuh: Boolean = false
)

data class IslamicEvent(
  val id: Int,
  val title: String,
  val hijriDay: Int,
  val hijriMonth: Int,
  val hijriDateStr: String,
  val gregorianDateStr: String,
  val description: String,
  val isMajorHoliday: Boolean = true
)

data class Mosque(
  val id: String,
  val name: String,
  val arabicName: String = "",
  val type: String = "Masjid", // "Masjid" or "Surau"
  val zoneCode: String,
  val state: String,
  val district: String,
  val address: String,
  val latitude: Double,
  val longitude: Double,
  val capacity: Int,
  val hasFridayPrayer: Boolean = true,
  val facilities: List<String> = emptyList(),
  val phone: String = "",
  val distanceKm: Double = 0.0
)
