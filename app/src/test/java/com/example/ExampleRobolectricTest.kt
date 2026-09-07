package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.model.PrayerType
import com.example.prayer.HijriCalendarHelper
import com.example.prayer.MalaysiaZones
import com.example.prayer.PrayerCalculator
import com.example.prayer.QiblaCalculator
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.util.Calendar

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("goSolat", appName)
  }

  @Test
  fun `verify Malaysia zones catalog`() {
    val zones = MalaysiaZones.ALL_ZONES
    assertTrue("Zones catalog should have multiple zones", zones.size > 30)

    val klZone = MalaysiaZones.findZoneByCode("WLY01")
    assertEquals("Kuala Lumpur, Putrajaya", klZone.name)
    assertEquals("Wilayah Persekutuan", klZone.state)

    val closest = MalaysiaZones.findClosestZone(3.139, 101.6869)
    assertEquals("WLY01", closest.code)
  }

  @Test
  fun `verify Qibla calculator for Kuala Lumpur`() {
    val bearing = QiblaCalculator.calculateQiblaBearing(3.139, 101.6869)
    // Bearing from KL to Makkah is approximately 292.8 degrees
    assertTrue("Qibla bearing should be around 292 degrees", bearing in 290.0..295.0)

    val distance = QiblaCalculator.calculateDistanceToKaabaKm(3.139, 101.6869)
    assertTrue("Distance to Kaaba should be around 7100 km", distance in 6900.0..7300.0)

    assertTrue("Should detect alignment when facing bearing", QiblaCalculator.isAligned(292.8f, bearing, 3f))
    assertFalse("Should not detect alignment when facing opposite", QiblaCalculator.isAligned(112.8f, bearing, 3f))
  }

  @Test
  fun `verify PrayerCalculator generates complete 7 prayers`() {
    val klZone = MalaysiaZones.findZoneByCode("WLY01")
    val cal = Calendar.getInstance()
    cal.set(2026, Calendar.SEPTEMBER, 6, 12, 0, 0)
    val schedule = PrayerCalculator.calculateSchedule(klZone, cal.time)

    assertEquals(7, schedule.items.size)
    val prayerTypes = schedule.items.map { it.type }
    assertTrue(prayerTypes.contains(PrayerType.IMSAK))
    assertTrue(prayerTypes.contains(PrayerType.SUBUH))
    assertTrue(prayerTypes.contains(PrayerType.SYURUK))
    assertTrue(prayerTypes.contains(PrayerType.ZOHOR))
    assertTrue(prayerTypes.contains(PrayerType.ASAR))
    assertTrue(prayerTypes.contains(PrayerType.MAGHRIB))
    assertTrue(prayerTypes.contains(PrayerType.ISYAK))
  }

  @Test
  fun `verify Hijri calendar converter and Islamic events`() {
    val cal = Calendar.getInstance()
    val hijriDateStr = HijriCalendarHelper.getHijriDateString(cal)
    assertTrue("Hijri date string should contain H", hijriDateStr.contains("H"))

    val events = HijriCalendarHelper.ISLAMIC_EVENTS
    assertTrue("Should have Islamic events list", events.isNotEmpty())
    assertTrue("Should include Hari Raya Aidilfitri", events.any { it.title.contains("Aidilfitri") })
  }

  @Test
  fun `verify only essential Azans Makkah and Madinah are retained`() {
    val azans = com.example.audio.AzanRepository.PRESET_AZANS
    assertEquals(2, azans.size)
    val ids = azans.map { it.id }.toSet()
    assertTrue(ids.contains("makkah_ali_mulla"))
    assertTrue(ids.contains("madinah_surayhi"))
  }
}
