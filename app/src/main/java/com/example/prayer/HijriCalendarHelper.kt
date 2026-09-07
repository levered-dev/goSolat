package com.example.prayer

import com.example.model.IslamicEvent
import java.util.Calendar
import kotlin.math.floor

data class HijriDate(
  val day: Int,
  val month: Int, // 1..12
  val year: Int,
  val monthNameMs: String,
  val monthNameArabic: String
)

data class CalendarDayItem(
  val gregorianDay: Int,
  val gregorianMonth: Int,
  val gregorianYear: Int,
  val hijriDay: Int,
  val hijriMonth: Int,
  val hijriMonthName: String,
  val isToday: Boolean,
  val isSelectedMonth: Boolean,
  val eventTitle: String? = null
)

object HijriCalendarHelper {

  val HIJRI_MONTHS_MS = listOf(
    "Muharram", "Safar", "Rabi'ul Awwal", "Rabi'ul Akhir",
    "Jamadil Awwal", "Jamadil Akhir", "Rejab", "Syaaban",
    "Ramadan", "Syawal", "Zulkaedah", "Zulhijjah"
  )

  val HIJRI_MONTHS_AR = listOf(
    "محرم", "صفر", "ربيع الأول", "ربيع الآخر",
    "جمادى الأولى", "جمادى الآخرة", "رجب", "شعبان",
    "رمضان", "شوال", "ذو القعدة", "ذو الحجة"
  )

  val ISLAMIC_EVENTS: List<IslamicEvent> = listOf(
    IslamicEvent(
      id = 1,
      title = "Awal Muharram (Tahun Baru Hijrah 1448H)",
      hijriDay = 1,
      hijriMonth = 1,
      hijriDateStr = "1 Muharram",
      gregorianDateStr = "17 Jun 2026",
      description = "Permulaan tahun baru dalam kalendar Islam memperingati peristiwa hijrah Nabi Muhammad SAW dari Makkah ke Madinah.",
      isMajorHoliday = true
    ),
    IslamicEvent(
      id = 2,
      title = "Hari Asyura",
      hijriDay = 10,
      hijriMonth = 1,
      hijriDateStr = "10 Muharram",
      gregorianDateStr = "26 Jun 2026",
      description = "Hari ke-10 Muharram disunatkan berpuasa Asyura yang menghapuskan dosa setahun yang lalu.",
      isMajorHoliday = false
    ),
    IslamicEvent(
      id = 3,
      title = "Maulidur Rasul SAW",
      hijriDay = 12,
      hijriMonth = 3,
      hijriDateStr = "12 Rabi'ul Awwal",
      gregorianDateStr = "26 Ogos 2026",
      description = "Hari keputeraan Junjungan Besar Nabi Muhammad SAW, penuh selawat dan mengingati sirah baginda.",
      isMajorHoliday = true
    ),
    IslamicEvent(
      id = 4,
      title = "Israk dan Mikraj",
      hijriDay = 27,
      hijriMonth = 7,
      hijriDateStr = "27 Rejab",
      gregorianDateStr = "6 Jan 2027",
      description = "Perjalanan agung malam Nabi SAW dari Masjidil Haram ke Masjidil Aqsa dan naik ke Sidratul Muntaha menerima perintah solat 5 waktu.",
      isMajorHoliday = true
    ),
    IslamicEvent(
      id = 5,
      title = "Nisfu Syaaban",
      hijriDay = 15,
      hijriMonth = 8,
      hijriDateStr = "15 Syaaban",
      gregorianDateStr = "23 Jan 2027",
      description = "Malam pertengahan bulan Syaaban di mana catatan amalan diangkat dan digalakkan memperbanyak doa dan solat malam.",
      isMajorHoliday = false
    ),
    IslamicEvent(
      id = 6,
      title = "Awal Ramadan (Hari Pertama Berpuasa)",
      hijriDay = 1,
      hijriMonth = 9,
      hijriDateStr = "1 Ramadan",
      gregorianDateStr = "8 Feb 2027",
      description = "Bulan keberkatan di mana umat Islam diwajibkan berpuasa dan pintu syurga dibuka seluas-luasnya.",
      isMajorHoliday = true
    ),
    IslamicEvent(
      id = 7,
      title = "Nuzul Al-Quran",
      hijriDay = 17,
      hijriMonth = 9,
      hijriDateStr = "17 Ramadan",
      gregorianDateStr = "24 Feb 2027",
      description = "Peringatan peristiwa turunnya ayat Al-Quran pertama (Surah Al-Alaq) kepada Rasulullah SAW di Gua Hira'.",
      isMajorHoliday = true
    ),
    IslamicEvent(
      id = 8,
      title = "Malam Lailatul Qadar (10 Terakhir Ramadan)",
      hijriDay = 27,
      hijriMonth = 9,
      hijriDateStr = "27 Ramadan",
      gregorianDateStr = "6 Mac 2027",
      description = "Malam yang lebih baik daripada 1,000 bulan di mana para malaikat turun membawa keberkatan dan kedamaian.",
      isMajorHoliday = false
    ),
    IslamicEvent(
      id = 9,
      title = "Hari Raya Aidilfitri (1 Syawal)",
      hijriDay = 1,
      hijriMonth = 10,
      hijriDateStr = "1 Syawal",
      gregorianDateStr = "10 Mac 2027",
      description = "Hari kemenangan umat Islam setelah sebulan menunaikan ibadah puasa Ramadan, hari kemaafan dan silaturrahim.",
      isMajorHoliday = true
    ),
    IslamicEvent(
      id = 10,
      title = "Hari Wukuf di Arafah",
      hijriDay = 9,
      hijriMonth = 12,
      hijriDateStr = "9 Zulhijjah",
      gregorianDateStr = "16 Mei 2027",
      description = "Kemuncak ibadah Haji di Padang Arafah. Disunatkan berpuasa Hari Arafah bagi mereka yang tidak mengerjakan haji.",
      isMajorHoliday = false
    ),
    IslamicEvent(
      id = 11,
      title = "Hari Raya Aidiladha (Hari Raya Korban)",
      hijriDay = 10,
      hijriMonth = 12,
      hijriDateStr = "10 Zulhijjah",
      gregorianDateStr = "17 Mei 2027",
      description = "Peringatan pengorbanan Nabi Ibrahim AS dan Nabi Ismail AS, diiringi ibadah sembelihan korban dan takbir Aidiladha.",
      isMajorHoliday = true
    ),
    IslamicEvent(
      id = 12,
      title = "Hari Tasyrik",
      hijriDay = 11,
      hijriMonth = 12,
      hijriDateStr = "11 - 13 Zulhijjah",
      gregorianDateStr = "18 - 20 Mei 2027",
      description = "Hari-hari makan, minum dan berzikir kepada Allah. Diharamkan berpuasa pada hari tasyrik.",
      isMajorHoliday = false
    )
  )

  /**
   * Convert Gregorian Calendar to HijriDate using Kuweit / Tabular astronomical algorithm with regional calibration.
   */
  fun getHijriDate(cal: Calendar, dayAdjustment: Int = 0): HijriDate {
    val day = cal.get(Calendar.DAY_OF_MONTH)
    val month = cal.get(Calendar.MONTH) + 1
    val year = cal.get(Calendar.YEAR)

    // Julian Day calculation
    var y = year
    var m = month
    if (m <= 2) {
      y -= 1
      m += 12
    }
    val a = floor(y / 100.0)
    val b = 2 - a + floor(a / 4.0)
    val jd = floor(365.25 * (y + 4716)) + floor(30.6001 * (m + 1)) + day + b - 1524.5 + dayAdjustment

    // Convert JD to Hijri
    val l = jd - 1948440.0 + 10632.0
    val n = floor((l - 1.0) / 10631.0)
    val l2 = l - 10631.0 * n + 354.0
    val j = (floor((10985.0 - l2) / 5316.0)) * (floor((50.0 * l2) / 17719.0)) +
        (floor(l2 / 5670.0)) * (floor((43.0 * l2) / 15238.0))
    val l3 = l2 - (floor((30.0 - j) / 15.0)) * (floor((17719.0 * j) / 50.0)) -
        (floor(j / 16.0)) * (floor((15238.0 * j) / 43.0)) + 29.0
    val hijriMonth = floor((24.0 * l3) / 709.0).toInt()
    val hijriDay = (l3 - floor((709.0 * hijriMonth) / 24.0)).toInt()
    val hijriYear = (30.0 * n + j - 30.0).toInt()

    val monthIndex = (hijriMonth - 1).coerceIn(0, 11)
    return HijriDate(
      day = hijriDay.coerceIn(1, 30),
      month = hijriMonth.coerceIn(1, 12),
      year = hijriYear,
      monthNameMs = HIJRI_MONTHS_MS[monthIndex],
      monthNameArabic = HIJRI_MONTHS_AR[monthIndex]
    )
  }

  fun getHijriDateString(cal: Calendar, dayAdjustment: Int = 0): String {
    val h = getHijriDate(cal, dayAdjustment)
    return "${h.day} ${h.monthNameMs} ${h.year}H"
  }

  /**
   * Generates calendar days for a Gregorian month view with corresponding Hijri dates.
   */
  fun getMonthCalendarDays(year: Int, month: Int): List<CalendarDayItem> {
    val cal = Calendar.getInstance()
    val todayCal = Calendar.getInstance()
    cal.set(year, month, 1, 0, 0, 0)
    cal.set(Calendar.MILLISECOND, 0)

    val maxDaysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
    val firstDayOfWeek = cal.get(Calendar.DAY_OF_WEEK) // 1=Sunday, 2=Monday, etc.

    val list = mutableListOf<CalendarDayItem>()

    // Leading days from previous month
    val prevMonthCal = Calendar.getInstance()
    prevMonthCal.set(year, month - 1, 1)
    val maxPrev = prevMonthCal.getActualMaximum(Calendar.DAY_OF_MONTH)
    val leadingDays = (firstDayOfWeek - Calendar.SUNDAY)
    for (i in (maxPrev - leadingDays + 1)..maxPrev) {
      prevMonthCal.set(Calendar.DAY_OF_MONTH, i)
      val hijri = getHijriDate(prevMonthCal)
      list.add(
        CalendarDayItem(
          gregorianDay = i,
          gregorianMonth = month - 1,
          gregorianYear = year,
          hijriDay = hijri.day,
          hijriMonth = hijri.month,
          hijriMonthName = hijri.monthNameMs,
          isToday = false,
          isSelectedMonth = false
        )
      )
    }

    // Days in current month
    for (d in 1..maxDaysInMonth) {
      cal.set(Calendar.DAY_OF_MONTH, d)
      val hijri = getHijriDate(cal)
      val isToday = (cal.get(Calendar.YEAR) == todayCal.get(Calendar.YEAR) &&
          cal.get(Calendar.MONTH) == todayCal.get(Calendar.MONTH) &&
          cal.get(Calendar.DAY_OF_MONTH) == todayCal.get(Calendar.DAY_OF_MONTH))

      val event = ISLAMIC_EVENTS.find { it.hijriDay == hijri.day && it.hijriMonth == hijri.month }

      list.add(
        CalendarDayItem(
          gregorianDay = d,
          gregorianMonth = month,
          gregorianYear = year,
          hijriDay = hijri.day,
          hijriMonth = hijri.month,
          hijriMonthName = hijri.monthNameMs,
          isToday = isToday,
          isSelectedMonth = true,
          eventTitle = event?.title
        )
      )
    }

    // Trailing days to fill 35 or 42 grid cells
    val remaining = (7 - (list.size % 7)) % 7
    val nextMonthCal = Calendar.getInstance()
    nextMonthCal.set(year, month + 1, 1)
    for (d in 1..remaining) {
      nextMonthCal.set(Calendar.DAY_OF_MONTH, d)
      val hijri = getHijriDate(nextMonthCal)
      list.add(
        CalendarDayItem(
          gregorianDay = d,
          gregorianMonth = month + 1,
          gregorianYear = year,
          hijriDay = hijri.day,
          hijriMonth = hijri.month,
          hijriMonthName = hijri.monthNameMs,
          isToday = false,
          isSelectedMonth = false
        )
      )
    }

    return list
  }
}
