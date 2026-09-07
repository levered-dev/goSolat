package com.example.prayer

import com.example.model.PrayerZone
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

object MalaysiaZones {
  val ALL_ZONES: List<PrayerZone> = listOf(
    // Wilayah Persekutuan
    PrayerZone("WLY01", "Wilayah Persekutuan", "Kuala Lumpur, Putrajaya", 3.1390, 101.6869),
    PrayerZone("WLY02", "Wilayah Persekutuan", "Labuan", 5.2831, 115.2308),

    // Selangor
    PrayerZone("SGR01", "Selangor", "Gombak, Petaling, Sepang, Hulu Langat, Hulu Selangor, Shah Alam", 3.0738, 101.5183),
    PrayerZone("SGR02", "Selangor", "Kuala Selangor, Sabak Bernam", 3.3370, 101.2580),
    PrayerZone("SGR03", "Selangor", "Klang, Kuala Langat", 3.0449, 101.4456),

    // Johor
    PrayerZone("JHR01", "Johor", "Pulau Aur dan Pulau Pemanggil", 2.4500, 104.5167),
    PrayerZone("JHR02", "Johor", "Kota Tinggi, Mersing, Johor Bahru, Kulai, Pontian", 1.4927, 103.7414),
    PrayerZone("JHR03", "Johor", "Kluang, Batu Pahat", 1.8548, 102.9325),
    PrayerZone("JHR04", "Johor", "Muar, Tangkak, Segamat", 2.0442, 102.5689),

    // Kedah
    PrayerZone("KDH01", "Kedah", "Kota Setar, Kubang Pasu, Pokok Sena (Alor Setar)", 6.1210, 100.3601),
    PrayerZone("KDH02", "Kedah", "Kuala Muda, Yan, Pendang (Sungai Petani)", 5.6470, 100.4877),
    PrayerZone("KDH03", "Kedah", "Padang Terap, Sik", 6.0000, 100.7500),
    PrayerZone("KDH04", "Kedah", "Baling", 5.6763, 100.9168),
    PrayerZone("KDH05", "Kedah", "Bandar Baharu, Kulim", 5.3653, 100.5564),
    PrayerZone("KDH06", "Kedah", "Langkawi", 6.3500, 99.8000),
    PrayerZone("KDH07", "Kedah", "Puncak Gunung Jerai", 5.7892, 100.4352),

    // Kelantan
    PrayerZone("KTN01", "Kelantan", "Kota Bharu, Bachok, Pasir Puteh, Tumpat, Pasir Mas, Tanah Merah, Machang", 6.1254, 102.2386),
    PrayerZone("KTN02", "Kelantan", "Jeli, Gua Musang (Mukim Galas & Bertam)", 5.6965, 101.8436),
    PrayerZone("KTN03", "Kelantan", "Gua Musang (Mukim Chiku)", 4.8821, 101.9680),

    // Melaka
    PrayerZone("MLK01", "Melaka", "Seluruh Negeri Melaka (Bandaraya Melaka)", 2.1896, 102.2501),

    // Negeri Sembilan
    PrayerZone("NGS01", "Negeri Sembilan", "Tampin, Jempol", 2.4701, 102.2302),
    PrayerZone("NGS02", "Negeri Sembilan", "Seremban, Port Dickson, Rembau, Jelebu, Kuala Pilah", 2.7258, 101.9424),

    // Pahang
    PrayerZone("PHG01", "Pahang", "Pulau Tioman", 2.7904, 104.1698),
    PrayerZone("PHG02", "Pahang", "Kuantan, Pekan, Rompin, Muadzam Shah", 3.8077, 103.3260),
    PrayerZone("PHG03", "Pahang", "Maran, Chenor, Jengka, Temerloh, Bera", 3.4478, 102.4172),
    PrayerZone("PHG04", "Pahang", "Bentong, Raub, Lipis", 3.5222, 101.9097),
    PrayerZone("PHG05", "Pahang", "Bukit Fraser, Genting Highlands, Cameron Highlands", 4.4714, 101.3756),

    // Perak
    PrayerZone("PRK01", "Perak", "Tapah, Slim River, Tanjung Malim", 3.6833, 101.5167),
    PrayerZone("PRK02", "Perak", "Ipoh, Batu Gajah, Kampar, Sungai Siput, Kuala Kangsar", 4.5975, 101.0901),
    PrayerZone("PRK03", "Perak", "Pengkalan Hulu, Grik, Lenggong", 5.4286, 101.1345),
    PrayerZone("PRK04", "Perak", "Temengor, Belum", 5.5167, 101.3833),
    PrayerZone("PRK05", "Perak", "Teluk Intan, Bagan Datuk, Lumut, Seri Manjung, Pangkor", 4.0259, 101.0213),
    PrayerZone("PRK06", "Perak", "Selama, Taiping, Bagan Serai, Parit Buntar", 4.8500, 100.7333),
    PrayerZone("PRK07", "Perak", "Bukit Larut", 4.8622, 100.7925),

    // Perlis
    PrayerZone("PLS01", "Perlis", "Kangar, Padang Besar, Arau", 6.4449, 100.2048),

    // Pulau Pinang
    PrayerZone("PNG01", "Pulau Pinang", "Seluruh Negeri Pulau Pinang (Georgetown, Butterworth)", 5.4164, 100.3327),

    // Sabah
    PrayerZone("SBH01", "Sabah", "Bahagian Sandakan (Timur), Bukit Garam, Semawang, Temanggong, Tambisan, Sukau", 5.8402, 118.1179),
    PrayerZone("SBH02", "Sabah", "Beluran, Telupid, Pinangah, Terusan, Kuamut, Bahagian Sandakan (Barat)", 5.8942, 117.5583),
    PrayerZone("SBH03", "Sabah", "Lahad Datu, Silabukan, Kunak, Sahabat, Semporna, Tungku", 5.0268, 118.3270),
    PrayerZone("SBH04", "Sabah", "Bandar Tawau, Balung, Merotai, Kalabakan", 4.2498, 117.8871),
    PrayerZone("SBH05", "Sabah", "Kudat, Kota Marudu, Pitas, Pulau Banggi", 6.8837, 116.8475),
    PrayerZone("SBH06", "Sabah", "Gunung Kinabalu", 6.0750, 116.5583),
    PrayerZone("SBH07", "Sabah", "Kota Kinabalu, Penampang, Tuaran, Papar, Ranau, Kota Belud", 5.9804, 116.0735),
    PrayerZone("SBH08", "Sabah", "Pensiangan, Keningau, Tambunan, Nabawan", 5.3436, 116.1594),
    PrayerZone("SBH09", "Sabah", "Beaufort, Kuala Penyu, Sipitang, Tenom, Membakut", 5.3473, 115.7432),

    // Sarawak
    PrayerZone("SWK01", "Sarawak", "Limbang, Lawas, Sundar, Trusan", 4.7500, 115.0000),
    PrayerZone("SWK02", "Sarawak", "Miri, Niah, Bekenu, Sibuti, Marudi", 4.3995, 113.9914),
    PrayerZone("SWK03", "Sarawak", "Pandan, Belaga, Suai, Tatau, Sebauh, Bintulu", 3.1667, 113.0333),
    PrayerZone("SWK04", "Sarawak", "Sibu, Mukah, Dalat, Song, Igan, Oya, Balingian, Kanowit, Kapit", 2.3000, 111.8167),
    PrayerZone("SWK05", "Sarawak", "Sarikei, Matu, Julau, Rajang, Daro, Bintangor, Belawai", 2.1167, 111.5167),
    PrayerZone("SWK06", "Sarawak", "Lubok Antu, Sri Aman, Roban, Debak, Betong, Saratuk, Kabong", 1.2333, 111.4500),
    PrayerZone("SWK07", "Sarawak", "Serian, Simunjan, Samarahan, Sebuyau, Meludam", 1.1667, 110.5667),
    PrayerZone("SWK08", "Sarawak", "Kuching, Bau, Lundu, Sematan", 1.5533, 110.3592),
    PrayerZone("SWK09", "Sarawak", "Zon Khas (Kampung Matang)", 1.6000, 110.3000),

    // Terengganu
    PrayerZone("TRG01", "Terengganu", "Kuala Terengganu, Marang, Kuala Nerus", 5.3302, 103.1408),
    PrayerZone("TRG02", "Terengganu", "Besut, Setiu", 5.7667, 102.5500),
    PrayerZone("TRG03", "Terengganu", "Hulu Terengganu (Kuala Berang)", 5.0667, 103.0167),
    PrayerZone("TRG04", "Terengganu", "Dungun, Kemaman", 4.7750, 103.4167)
  )

  fun getStates(): List<String> {
    return ALL_ZONES.map { it.state }.distinct()
  }

  fun getZonesForState(state: String): List<PrayerZone> {
    return ALL_ZONES.filter { it.state == state }
  }

  fun defaultZone(): PrayerZone {
    return ALL_ZONES.first { it.code == "WLY01" }
  }

  fun findZoneByCode(code: String): PrayerZone {
    return ALL_ZONES.find { it.code.equals(code, ignoreCase = true) } ?: defaultZone()
  }

  fun findClosestZone(latitude: Double, longitude: Double): PrayerZone {
    var closestZone = defaultZone()
    var minDistance = Double.MAX_VALUE

    for (zone in ALL_ZONES) {
      val dLat = Math.toRadians(zone.latitude - latitude)
      val dLon = Math.toRadians(zone.longitude - longitude)
      val a = sin(dLat / 2) * sin(dLat / 2) +
          cos(Math.toRadians(latitude)) * cos(Math.toRadians(zone.latitude)) *
          sin(dLon / 2) * sin(dLon / 2)
      val c = 2 * atan2(sqrt(a), sqrt(1 - a))
      val distance = 6371.0 * c

      if (distance < minDistance) {
        minDistance = distance
        closestZone = zone
      }
    }
    return closestZone
  }
}
