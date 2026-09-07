package com.example.prayer

import com.example.model.NextPrayerInfo
import com.example.model.PrayerItem
import com.example.model.PrayerSchedule
import com.example.model.PrayerType
import com.example.model.PrayerZone
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import kotlin.math.*

object PrayerCalculator {

  private const val SUBUH_ANGLE = 18.0 // standard Malaysian / JAKIM angle
  private const val ISYAK_ANGLE = 18.0

  fun calculateSchedule(
    zone: PrayerZone,
    date: Date = Date(),
    timeZoneOffsetHours: Double = 8.0 // GMT+8 for Malaysia
  ): PrayerSchedule {
    val cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"), Locale("ms", "MY"))
    cal.time = date

    val year = cal.get(Calendar.YEAR)
    val month = cal.get(Calendar.MONTH) + 1
    val day = cal.get(Calendar.DAY_OF_MONTH)

    val jd = julianDate(year, month, day) - (zone.longitude / (360.0 * 24.0))

    // Sun position calculations
    val d = jd - 2451545.0
    val g = fixAngle(357.529 + 0.98560028 * d)
    val q = fixAngle(280.459 + 0.98564736 * d)
    val l = fixAngle(q + 1.915 * sin(Math.toRadians(g)) + 0.020 * sin(Math.toRadians(2 * g)))

    val e = 23.439 - 0.00000036 * d
    val dd = Math.toDegrees(asin(sin(Math.toRadians(e)) * sin(Math.toRadians(l))))
    var ra = Math.toDegrees(atan2(cos(Math.toRadians(e)) * sin(Math.toRadians(l)), cos(Math.toRadians(l)))) / 15.0
    ra = fixHour(ra)

    val eqT = q / 15.0 - ra

    // Midday (Zohor base)
    val noon = fixHour(12.0 + timeZoneOffsetHours - zone.longitude / 15.0 - eqT)

    // Sun altitude calculations
    val latRad = Math.toRadians(zone.latitude)
    val declRad = Math.toRadians(dd)

    fun sunHourAngle(angle: Double): Double {
      val angleRad = Math.toRadians(-angle)
      val cosHA = (sin(angleRad) - sin(latRad) * sin(declRad)) / (cos(latRad) * cos(declRad))
      val clamped = cosHA.coerceIn(-1.0, 1.0)
      return Math.toDegrees(acos(clamped)) / 15.0
    }

    fun asarHourAngle(): Double {
      val shadowFactor = 1.0 // Shafi'i
      val angleRad = atan(1.0 / (shadowFactor + tan(abs(latRad - declRad))))
      val cosHA = (sin(angleRad) - sin(latRad) * sin(declRad)) / (cos(latRad) * cos(declRad))
      val clamped = cosHA.coerceIn(-1.0, 1.0)
      return Math.toDegrees(acos(clamped)) / 15.0
    }

    val subuhHA = sunHourAngle(SUBUH_ANGLE)
    val syurukHA = sunHourAngle(0.833)
    val asarHA = asarHourAngle()
    val maghribHA = sunHourAngle(0.833)
    val isyakHA = sunHourAngle(ISYAK_ANGLE)

    val subuhHours = noon - subuhHA
    val syurukHours = noon - syurukHA
    val zohorHours = noon + (2.0 / 60.0) // 2 minutes ihtiyat (safety margin)
    val asarHours = noon + asarHA + (2.0 / 60.0)
    val maghribHours = noon + maghribHA + (2.0 / 60.0)
    val isyakHours = noon + isyakHA + (2.0 / 60.0)
    val imsakHours = subuhHours - (10.0 / 60.0) // 10 minutes before Subuh

    val nowMillis = System.currentTimeMillis()

    fun makePrayerItem(type: PrayerType, hoursDecimal: Double): PrayerItem {
      val totalMinutes = (hoursDecimal * 60).roundToInt()
      val h = (totalMinutes / 60) % 24
      val m = totalMinutes % 60

      val prayerCal = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"), Locale("ms", "MY"))
      prayerCal.time = date
      prayerCal.set(Calendar.HOUR_OF_DAY, h)
      prayerCal.set(Calendar.MINUTE, m)
      prayerCal.set(Calendar.SECOND, 0)
      prayerCal.set(Calendar.MILLISECOND, 0)

      val epoch = prayerCal.timeInMillis
      val isPassed = nowMillis > epoch

      val time24 = String.format(Locale.US, "%02d:%02d", h, m)
      val timeFormatted = formatTime12(h, m)

      return PrayerItem(
        type = type,
        timeFormatted = timeFormatted,
        time24 = time24,
        timestampEpoch = epoch,
        isPassed = isPassed
      )
    }

    val items = listOf(
      makePrayerItem(PrayerType.IMSAK, imsakHours),
      makePrayerItem(PrayerType.SUBUH, subuhHours),
      makePrayerItem(PrayerType.SYURUK, syurukHours),
      makePrayerItem(PrayerType.ZOHOR, zohorHours),
      makePrayerItem(PrayerType.ASAR, asarHours),
      makePrayerItem(PrayerType.MAGHRIB, maghribHours),
      makePrayerItem(PrayerType.ISYAK, isyakHours)
    )

    val sdfGreg = SimpleDateFormat("EEEE, d MMMM yyyy", Locale("ms", "MY"))
    sdfGreg.timeZone = TimeZone.getTimeZone("GMT+8")
    val gregStr = sdfGreg.format(date)

    val hijriStr = HijriCalendarHelper.getHijriDateString(cal)

    return PrayerSchedule(
      dateGregorian = gregStr,
      dateHijri = hijriStr,
      zoneCode = zone.code,
      zoneName = zone.name,
      state = zone.state,
      items = items
    )
  }

  fun calculateNextPrayer(schedule: PrayerSchedule, nowMillis: Long = System.currentTimeMillis()): NextPrayerInfo {
    val fardhuItems = schedule.items.filter { it.type.isFardhu }

    // Find the first prayer today whose time is in the future
    val upcoming = fardhuItems.firstOrNull { it.timestampEpoch > nowMillis }

    if (upcoming != null) {
      val diff = upcoming.timestampEpoch - nowMillis
      val hours = diff / (1000 * 60 * 60)
      val minutes = (diff / (1000 * 60)) % 60
      val seconds = (diff / 1000) % 60

      // Calculate progress from previous prayer to this upcoming prayer
      val currentIndex = fardhuItems.indexOf(upcoming)
      val prevEpoch = if (currentIndex > 0) {
        fardhuItems[currentIndex - 1].timestampEpoch
      } else {
        upcoming.timestampEpoch - (6 * 3600 * 1000L) // approximate 6 hours before
      }
      val totalWindow = (upcoming.timestampEpoch - prevEpoch).coerceAtLeast(1L)
      val elapsed = (nowMillis - prevEpoch).coerceAtLeast(0L)
      val progress = (elapsed.toFloat() / totalWindow.toFloat()).coerceIn(0f, 1f)

      return NextPrayerInfo(
        nextPrayer = upcoming.type,
        nextPrayerTimeFormatted = upcoming.timeFormatted,
        remainingHours = hours,
        remainingMinutes = minutes,
        remainingSeconds = seconds,
        remainingMillis = diff,
        progressToNext = progress
      )
    } else {
      // All prayers today have passed -> Next is tomorrow's Subuh!
      // Add 24 hours approximately to Subuh of today
      val subuhToday = fardhuItems.first { it.type == PrayerType.SUBUH }
      val tomorrowSubuhEpoch = subuhToday.timestampEpoch + (24 * 3600 * 1000L)
      val diff = (tomorrowSubuhEpoch - nowMillis).coerceAtLeast(0L)
      val hours = diff / (1000 * 60 * 60)
      val minutes = (diff / (1000 * 60)) % 60
      val seconds = (diff / 1000) % 60

      return NextPrayerInfo(
        nextPrayer = PrayerType.SUBUH,
        nextPrayerTimeFormatted = subuhToday.timeFormatted,
        remainingHours = hours,
        remainingMinutes = minutes,
        remainingSeconds = seconds,
        remainingMillis = diff,
        progressToNext = 0.95f
      )
    }
  }

  private fun formatTime12(hour24: Int, minute: Int): String {
    val ampm = if (hour24 >= 12) "PM" else "AM"
    val h12 = when (hour24) {
      0 -> 12
      in 1..12 -> hour24
      else -> hour24 - 12
    }
    return String.format(Locale.US, "%d:%02d %s", h12, minute, ampm)
  }

  private fun julianDate(year: Int, month: Int, day: Int): Double {
    var y = year
    var m = month
    if (m <= 2) {
      y -= 1
      m += 12
    }
    val a = floor(y / 100.0)
    val b = 2 - a + floor(a / 4.0)
    return floor(365.25 * (y + 4716)) + floor(30.6001 * (m + 1)) + day + b - 1524.5
  }

  private fun fixAngle(a: Double): Double {
    var angle = a - 360.0 * floor(a / 360.0)
    if (angle < 0) angle += 360.0
    return angle
  }

  private fun fixHour(h: Double): Double {
    var hour = h - 24.0 * floor(h / 24.0)
    if (hour < 0) hour += 24.0
    return hour
  }
}
