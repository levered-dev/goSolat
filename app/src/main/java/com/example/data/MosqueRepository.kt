package com.example.data

import com.example.model.Mosque
import kotlin.math.*

object MosqueRepository {

  val ALL_MOSQUES: List<Mosque> = listOf(
    // WLY01: Kuala Lumpur & Putrajaya
    Mosque(
      id = "masjid_negara",
      name = "Masjid Negara",
      arabicName = "المسجد الوطني",
      zoneCode = "WLY01",
      state = "Wilayah Persekutuan",
      district = "Kuala Lumpur",
      address = "Jalan Perdana, Tasik Perdana, 50480 Kuala Lumpur",
      latitude = 3.1418,
      longitude = 101.6917,
      capacity = 15000,
      hasFridayPrayer = true,
      facilities = listOf("Solat Jumaat", "Parkir Luas", "Mesra OKU", "Ruang Muslimah", "Pendingin Hawa", "Kawasan Bersejarah"),
      phone = "+603-2693 7772"
    ),
    Mosque(
      id = "masjid_wilayah",
      name = "Masjid Wilayah Persekutuan",
      arabicName = "مسجد الإقليم الفدرالي",
      zoneCode = "WLY01",
      state = "Wilayah Persekutuan",
      district = "Kuala Lumpur",
      address = "Jalan Tuanku Abdul Halim, Kompleks Kerajaan, 50480 Kuala Lumpur",
      latitude = 3.1772,
      longitude = 101.6706,
      capacity = 17000,
      hasFridayPrayer = true,
      facilities = listOf("Solat Jumaat", "Parkir Luas", "Mesra OKU", "Ruang Muslimah", "Dewan Serbaguna", "Perpustakaan"),
      phone = "+603-6201 8791"
    ),
    Mosque(
      id = "masjid_jamek_kl",
      name = "Masjid Jamek Sultan Abdul Samad",
      arabicName = "مسjid جامع كوالالمبور",
      zoneCode = "WLY01",
      state = "Wilayah Persekutuan",
      district = "Kuala Lumpur",
      address = "Jalan Tun Perak, City Centre, 50050 Kuala Lumpur",
      latitude = 3.1492,
      longitude = 101.6957,
      capacity = 2500,
      hasFridayPrayer = true,
      facilities = listOf("Solat Jumaat", "LRT Masjid Jamek", "Mesra Pelancong", "Kawasan Wuduk Moden"),
      phone = "+603-2691 2829"
    ),
    Mosque(
      id = "masjid_assyakirin_klcc",
      name = "Masjid As-Syakirin KLCC",
      arabicName = "مسجد الشاكرين",
      zoneCode = "WLY01",
      state = "Wilayah Persekutuan",
      district = "Kuala Lumpur",
      address = "Lot 41, Seksyen 58, Jalan Pinang, Kuala Lumpur City Centre, 50450",
      latitude = 3.1566,
      longitude = 101.7161,
      capacity = 12000,
      hasFridayPrayer = true,
      facilities = listOf("Solat Jumaat", "Dekat Taman KLCC", "Mesra OKU", "Pendingin Hawa", "Kawasan Wuduk Moden"),
      phone = "+603-2382 7200"
    ),
    Mosque(
      id = "masjid_putra",
      name = "Masjid Putra (Masjid Merah Jambu)",
      arabicName = "مسجد بوترا",
      zoneCode = "WLY01",
      state = "Wilayah Persekutuan",
      district = "Putrajaya",
      address = "Presint 1, 62000 Putrajaya",
      latitude = 2.9360,
      longitude = 101.6898,
      capacity = 15000,
      hasFridayPrayer = true,
      facilities = listOf("Solat Jumaat", "Tepi Tasik Putrajaya", "Mesra Pelancong", "Ruang Muslimah", "Parkir Awam"),
      phone = "+603-8888 5678"
    ),
    Mosque(
      id = "masjid_besi_putrajaya",
      name = "Masjid Tuanku Mizan Zainal Abidin (Masjid Besi)",
      arabicName = "مسجد السلطان ميزان زين العابدين",
      zoneCode = "WLY01",
      state = "Wilayah Persekutuan",
      district = "Putrajaya",
      address = "Presint 3, 62100 Putrajaya",
      latitude = 2.9238,
      longitude = 101.6847,
      capacity = 24000,
      hasFridayPrayer = true,
      facilities = listOf("Solat Jumaat", "Struktur Keluli Moden", "Mesra OKU", "Parkir Luas", "Laluan Berangin"),
      phone = "+603-8880 4300"
    ),

    // SGR01: Selangor (Shah Alam, Petaling, Gombak, Subang)
    Mosque(
      id = "masjid_biru_shah_alam",
      name = "Masjid Sultan Salahuddin Abdul Aziz Shah (Masjid Biru)",
      arabicName = "مسجد السلطان صلاح الدين عبد العزيز شاه",
      zoneCode = "SGR01",
      state = "Selangor",
      district = "Shah Alam",
      address = "Persiaran Masjid St., Seksyen 14, 40000 Shah Alam, Selangor",
      latitude = 3.0784,
      longitude = 101.5208,
      capacity = 24000,
      hasFridayPrayer = true,
      facilities = listOf("Solat Jumaat", "Kubah Biru Ikonik", "Parkir Bertingkat", "Mesra OKU", "Taman Rekreasi"),
      phone = "+603-5519 9988"
    ),
    Mosque(
      id = "masjid_hijau_cyberjaya",
      name = "Masjid Raja Haji Fisabilillah (Masjid Hijau)",
      arabicName = "مسجد راجا حاجي في سبيل الله",
      zoneCode = "SGR01",
      state = "Selangor",
      district = "Cyberjaya",
      address = "Persiaran Semarak Api, Cyberjaya, 63000 Sepang, Selangor",
      latitude = 2.9247,
      longitude = 101.6508,
      capacity = 8300,
      hasFridayPrayer = true,
      facilities = listOf("Solat Jumaat", "Bangunan Hijau GBI", "Solar Rooftop", "Ruang Muslimah", "Parkir Luas"),
      phone = "+603-8322 3662"
    ),
    Mosque(
      id = "masjid_alfalah_usj",
      name = "Masjid Al-Falah USJ 9",
      arabicName = "مسجد الفلاح",
      zoneCode = "SGR01",
      state = "Selangor",
      district = "Subang Jaya",
      address = "Jalan USJ 9/1, USJ 9, 47620 Subang Jaya, Selangor",
      latitude = 3.0428,
      longitude = 101.5886,
      capacity = 4500,
      hasFridayPrayer = true,
      facilities = listOf("Solat Jumaat", "Pusat Komuniti", "Mesra OKU", "Pendingin Hawa"),
      phone = "+603-8024 1633"
    ),
    Mosque(
      id = "masjid_kota_damansara",
      name = "Masjid Kota Damansara",
      arabicName = "مسجد كوتا دامنسارا",
      zoneCode = "SGR01",
      state = "Selangor",
      district = "Petaling Jaya",
      address = "Jalan Sepah Puteri 5/21, Seksyen 5 Kota Damansara, 47810 Petaling Jaya",
      latitude = 3.1678,
      longitude = 101.5843,
      capacity = 4000,
      hasFridayPrayer = true,
      facilities = listOf("Solat Jumaat", "Parkir Selesa", "Ruang Muslimah", "Mesra Keluarga"),
      phone = "+603-6140 1819"
    ),

    // PNG01: Pulau Pinang
    Mosque(
      id = "masjid_negeri_penang",
      name = "Masjid Negeri Pulau Pinang",
      arabicName = "مسجد ولاية بينانج",
      zoneCode = "PNG01",
      state = "Pulau Pinang",
      district = "Georgetown",
      address = "Jalan Air Itam, 10460 George Town, Pulau Pinang",
      latitude = 5.4053,
      longitude = 100.3015,
      capacity = 5000,
      hasFridayPrayer = true,
      facilities = listOf("Solat Jumaat", "Kubah Bunga Raya", "Parkir Luas", "Mesra OKU"),
      phone = "+604-828 2062"
    ),
    Mosque(
      id = "masjid_terapung_penang",
      name = "Masjid Terapung Tanjung Bungah",
      arabicName = "مسجد تانجونج بونجاه العائم",
      zoneCode = "PNG01",
      state = "Pulau Pinang",
      district = "Tanjung Bungah",
      address = "Jalan Tanjung Bungah, 11200 Tanjung Bungah, Pulau Pinang",
      latitude = 5.4678,
      longitude = 100.2798,
      capacity = 1500,
      hasFridayPrayer = true,
      facilities = listOf("Solat Jumaat", "Pemandangan Laut", "Mesra Pelancong", "Ruang Muslimah"),
      phone = "+604-899 5050"
    ),

    // JHR02: Johor Bahru & Sekitarnya
    Mosque(
      id = "masjid_sultan_abu_bakar_jb",
      name = "Masjid Sultan Abu Bakar",
      arabicName = "مسجد السلطان أبو بكر",
      zoneCode = "JHR02",
      state = "Johor",
      district = "Johor Bahru",
      address = "Jalan Gertak Merah, Masjid Sultan Abu Bakar, 80000 Johor Bahru",
      latitude = 1.4589,
      longitude = 103.7547,
      capacity = 3000,
      hasFridayPrayer = true,
      facilities = listOf("Solat Jumaat", "Seni Bina Victoria Islam", "Pemandangan Selat Tebrau", "Bersejarah"),
      phone = "+607-223 4935"
    ),
    Mosque(
      id = "masjid_kota_iskandar",
      name = "Masjid Kota Iskandar",
      arabicName = "مسجد كوتا إسكندر",
      zoneCode = "JHR02",
      state = "Johor",
      district = "Iskandar Puteri",
      address = "Kota Iskandar, 79100 Iskandar Puteri, Johor",
      latitude = 1.4206,
      longitude = 103.6294,
      capacity = 6000,
      hasFridayPrayer = true,
      facilities = listOf("Solat Jumaat", "Seni Bina Moorish", "Parkir Luas", "Mesra OKU"),
      phone = "+607-266 7000"
    ),

    // MLK01: Melaka
    Mosque(
      id = "masjid_selat_melaka",
      name = "Masjid Selat Melaka (Masjid Terapung)",
      arabicName = "مسجد مضيق ملقا",
      zoneCode = "MLK01",
      state = "Melaka",
      district = "Melaka Tengah",
      address = "Jalan Pulau Melaka 8, 75000 Melaka",
      latitude = 2.1798,
      longitude = 102.2505,
      capacity = 2000,
      hasFridayPrayer = true,
      facilities = listOf("Solat Jumaat", "Terapung di Selat Melaka", "Pemandangan Matahari Terbenam", "Mesra Pelancong"),
      phone = "+606-281 9284"
    ),
    Mosque(
      id = "masjid_al_azim_melaka",
      name = "Masjid Al-Azim (Masjid Negeri Melaka)",
      arabicName = "مسجد العظيم",
      zoneCode = "MLK01",
      state = "Melaka",
      district = "Bukit Palah",
      address = "Jalan Panglima Awang, 75400 Melaka",
      latitude = 2.2158,
      longitude = 102.2612,
      capacity = 11000,
      hasFridayPrayer = true,
      facilities = listOf("Solat Jumaat", "Bumbung Bertingkat Tradisional", "Parkir Luas", "Mesra OKU"),
      phone = "+606-284 3400"
    ),

    // PRK02: Ipoh, Kuala Kangsar
    Mosque(
      id = "masjid_ubudiah_kuala_kangsar",
      name = "Masjid Ubudiah",
      arabicName = "مسجد العبودية",
      zoneCode = "PRK02",
      state = "Perak",
      district = "Kuala Kangsar",
      address = "Jalan Istana, 33000 Kuala Kangsar, Perak",
      latitude = 4.7644,
      longitude = 100.9497,
      capacity = 2300,
      hasFridayPrayer = true,
      facilities = listOf("Solat Jumaat", "Warisan Kebangsaan", "Kubah Emas Indah", "Mesra Pelancong"),
      phone = "+605-776 1144"
    ),
    Mosque(
      id = "masjid_negeri_perak_ipoh",
      name = "Masjid Sultan Idris Shah II",
      arabicName = "مسجد السلطان إدريس شاه الثاني",
      zoneCode = "PRK02",
      state = "Perak",
      district = "Ipoh",
      address = "Jalan Panglima Bukit Gantang Wahab, 30000 Ipoh, Perak",
      latitude = 4.5969,
      longitude = 101.0772,
      capacity = 5000,
      hasFridayPrayer = true,
      facilities = listOf("Solat Jumaat", "Pusat Bandar Ipoh", "Parkir Bertingkat", "Mesra OKU"),
      phone = "+605-254 3662"
    ),

    // KDH01: Alor Setar
    Mosque(
      id = "masjid_zahir_alor_setar",
      name = "Masjid Zahir (Masjid Negeri Kedah)",
      arabicName = "مسجد الظاهر",
      zoneCode = "KDH01",
      state = "Kedah",
      district = "Kota Setar",
      address = "Jalan Mahkamah, Bandar Alor Setar, 05000 Alor Setar, Kedah",
      latitude = 6.1206,
      longitude = 100.3653,
      capacity = 5000,
      hasFridayPrayer = true,
      facilities = listOf("Solat Jumaat", "Top 10 Masjid Tercantik Dunia", "Kubah Hitam Elegan", "Bersejarah"),
      phone = "+604-733 3288"
    ),

    // TRG01: Kuala Terengganu
    Mosque(
      id = "masjid_kristal_terengganu",
      name = "Masjid Kristal",
      arabicName = "مسجد البلور",
      zoneCode = "TRG01",
      state = "Terengganu",
      district = "Kuala Terengganu",
      address = "Pulau Wan Man, Losong, 21000 Kuala Terengganu, Terengganu",
      latitude = 5.3218,
      longitude = 103.1189,
      capacity = 1500,
      hasFridayPrayer = true,
      facilities = listOf("Solat Jumaat", "Struktur Kaca & Keluli", "Taman Tamadun Islam", "Mesra Pelancong"),
      phone = "+609-627 8888"
    ),

    // KTN01: Kota Bharu
    Mosque(
      id = "masjid_muhammadi_kelantan",
      name = "Masjid Muhammadi",
      arabicName = "المسجد المحمدي",
      zoneCode = "KTN01",
      state = "Kelantan",
      district = "Kota Bharu",
      address = "Jalan Sultanah Zainab, Bandar Kota Bharu, 15000 Kota Bharu",
      latitude = 6.1309,
      longitude = 102.2372,
      capacity = 3000,
      hasFridayPrayer = true,
      facilities = listOf("Solat Jumaat", "Pusat Pengajian Bersejarah", "Ruang Muslimah", "Pusat Bandar"),
      phone = "+609-744 3284"
    ),

    // SBH01: Kota Kinabalu
    Mosque(
      id = "masjid_bandaraya_kk",
      name = "Masjid Bandaraya Kota Kinabalu (Masjid Terapung Likas)",
      arabicName = "مسجد مدينة كوتا كينابالو",
      zoneCode = "SBH01",
      state = "Sabah",
      district = "Kota Kinabalu",
      address = "Jalan Pasir, Teluk Likas, 88400 Kota Kinabalu, Sabah",
      latitude = 5.9958,
      longitude = 116.1080,
      capacity = 12000,
      hasFridayPrayer = true,
      facilities = listOf("Solat Jumaat", "Laguna Menghadap Laut", "Mesra Pelancong", "Parkir Luas"),
      phone = "+6088-212 121"
    ),

    // SWK01: Kuching
    Mosque(
      id = "masjid_jamek_sarawak",
      name = "Masjid Jamek Negeri Sarawak",
      arabicName = "مسجد جامع ساراواك",
      zoneCode = "SWK01",
      state = "Sarawak",
      district = "Petra Jaya, Kuching",
      address = "Petra Jaya, 93050 Kuching, Sarawak",
      latitude = 1.5794,
      longitude = 110.3478,
      capacity = 10000,
      hasFridayPrayer = true,
      facilities = listOf("Solat Jumaat", "Kubah Keemasan", "Taman Landskap Luas", "Mesra OKU"),
      phone = "+6082-441 555"
    ),

    // --- SURAU & SURAU JUMAAT MENGIKUT ZON SEMASA ---
    // WLY01: Surau Kuala Lumpur & Putrajaya
    Mosque(
      id = "surau_al_ikhlas_bb",
      name = "Surau Al-Ikhlas Bukit Bintang",
      arabicName = "مصلى الإخلاص",
      type = "Surau",
      zoneCode = "WLY01",
      state = "Wilayah Persekutuan",
      district = "Kuala Lumpur",
      address = "Jalan Walter Grenier, Bukit Bintang, 55100 Kuala Lumpur",
      latitude = 3.1465,
      longitude = 101.7115,
      capacity = 350,
      hasFridayPrayer = false,
      facilities = listOf("Solat Berjemaah", "Ruang Muslimah", "Pendingin Hawa", "Dekat Pusat Beli-belah"),
      phone = "+603-2144 1122"
    ),
    Mosque(
      id = "surau_jumaat_madinah_bangsar",
      name = "Surau Jumaat Al-Madinah Bangsar South",
      arabicName = "مصلى المدينة الجمعة",
      type = "Surau",
      zoneCode = "WLY01",
      state = "Wilayah Persekutuan",
      district = "Kuala Lumpur",
      address = "The Horizon, Avenue 3, Bangsar South, 59200 Kuala Lumpur",
      latitude = 3.1118,
      longitude = 101.6668,
      capacity = 1200,
      hasFridayPrayer = true,
      facilities = listOf("Solat Jumaat", "Dekat LRT Kerinchi", "Mesra OKU", "Pendingin Hawa", "Kuliah Harian"),
      phone = "+603-2242 8899"
    ),
    Mosque(
      id = "surau_jumaat_at_taqwa_ttdi",
      name = "Surau Jumaat At-Taqwa TTDI",
      arabicName = "مصلى التقوى الجمعة",
      type = "Surau",
      zoneCode = "WLY01",
      state = "Wilayah Persekutuan",
      district = "Kuala Lumpur",
      address = "Jalan Datuk Sulaiman, Taman Tun Dr Ismail, 60000 Kuala Lumpur",
      latitude = 3.1408,
      longitude = 101.6289,
      capacity = 900,
      hasFridayPrayer = true,
      facilities = listOf("Solat Jumaat", "Parkir Komuniti", "Ruang Muslimah", "Kelas Mengaji"),
      phone = "+603-7728 5544"
    ),
    Mosque(
      id = "surau_muhajirin_presint9",
      name = "Surau Al-Muhajirin Presint 9",
      arabicName = "مصلى المهاجرين",
      type = "Surau",
      zoneCode = "WLY01",
      state = "Wilayah Persekutuan",
      district = "Putrajaya",
      address = "Jalan P9 B/1, Presint 9, 62250 Putrajaya",
      latitude = 2.9345,
      longitude = 101.6750,
      capacity = 600,
      hasFridayPrayer = true,
      facilities = listOf("Solat Jumaat", "Dekat Hospital Putrajaya", "Parkir Awam", "Dewan Terbuka"),
      phone = "+603-8889 1234"
    ),
    Mosque(
      id = "surau_mizan_presint8",
      name = "Surau Al-Mizan Presint 8",
      arabicName = "مصلى الميزان",
      type = "Surau",
      zoneCode = "WLY01",
      state = "Wilayah Persekutuan",
      district = "Putrajaya",
      address = "Presint 8, 62250 Putrajaya",
      latitude = 2.9312,
      longitude = 101.6885,
      capacity = 500,
      hasFridayPrayer = false,
      facilities = listOf("Solat 5 Waktu", "Tepi Tasik", "Laluan Basikal", "Ruang Muslimah"),
      phone = "+603-8888 2233"
    ),

    // SGR01: Surau Selangor
    Mosque(
      id = "surau_hidayah_seksyen7",
      name = "Surau Al-Hidayah Seksyen 7 Shah Alam",
      arabicName = "مصلى الهداية",
      type = "Surau",
      zoneCode = "SGR01",
      state = "Selangor",
      district = "Shah Alam",
      address = "Jalan Plumbum 7/95, Seksyen 7, 40000 Shah Alam, Selangor",
      latitude = 3.0760,
      longitude = 101.4985,
      capacity = 800,
      hasFridayPrayer = true,
      facilities = listOf("Solat Jumaat", "Dekat UiTM Shah Alam", "Parkir Luas", "Kuliah Maghrib"),
      phone = "+603-5510 4455"
    ),
    Mosque(
      id = "surau_jumaat_falah_usj",
      name = "Surau Jumaat Al-Falah USJ 9",
      arabicName = "مصلى الفلاح الجمعة",
      type = "Surau",
      zoneCode = "SGR01",
      state = "Selangor",
      district = "Subang Jaya",
      address = "Jalan USJ 9/1, 47620 Subang Jaya, Selangor",
      latitude = 3.0450,
      longitude = 101.5900,
      capacity = 1000,
      hasFridayPrayer = true,
      facilities = listOf("Solat Jumaat", "Dekat Taipan USJ", "Pendingin Hawa", "Mesra Warga Emas"),
      phone = "+603-8024 6677"
    ),
    Mosque(
      id = "surau_husna_sunway",
      name = "Surau Al-Husna Bandar Sunway",
      arabicName = "مصلى الحسنى",
      type = "Surau",
      zoneCode = "SGR01",
      state = "Selangor",
      district = "Petaling Jaya",
      address = "PJS 10, Bandar Sunway, 46150 Petaling Jaya, Selangor",
      latitude = 3.0710,
      longitude = 101.6080,
      capacity = 450,
      hasFridayPrayer = false,
      facilities = listOf("Solat 5 Waktu", "Dekat Sunway Pyramid", "Ruang Muslimah", "Kawasan Komuniti"),
      phone = "+603-5638 9900"
    ),
    Mosque(
      id = "surau_kauthar_bangi",
      name = "Surau Al-Kauthar Seksyen 4 Bangi",
      arabicName = "مصلى الكوثر",
      type = "Surau",
      zoneCode = "SGR01",
      state = "Selangor",
      district = "Hulu Langat",
      address = "Seksyen 4 Tambahan, 43650 Bandar Baru Bangi, Selangor",
      latitude = 2.9550,
      longitude = 101.7760,
      capacity = 700,
      hasFridayPrayer = true,
      facilities = listOf("Solat Jumaat", "Pusat Komuniti", "Tadika Islam", "Parkir Luas"),
      phone = "+603-8925 1100"
    ),
    Mosque(
      id = "surau_ar_rahman_cyberjaya",
      name = "Surau Ar-Rahman Cyberjaya",
      arabicName = "مصلى الرحمن سايبرجايا",
      type = "Surau",
      zoneCode = "SGR01",
      state = "Selangor",
      district = "Sepang",
      address = "Persiaran Multimedia, Cyber 6, 63000 Cyberjaya, Selangor",
      latitude = 2.9215,
      longitude = 101.6550,
      capacity = 550,
      hasFridayPrayer = false,
      facilities = listOf("Solat Berjemaah", "Hab Teknologi", "Mesra Pelajar MMU", "Pendingin Hawa"),
      phone = "+603-8318 7766"
    ),

    // JHR02: Surau Johor
    Mosque(
      id = "surau_muttaqin_skudai",
      name = "Surau Al-Muttaqin Taman Universiti",
      arabicName = "مصلى المتقين",
      type = "Surau",
      zoneCode = "JHR02",
      state = "Johor",
      district = "Johor Bahru",
      address = "Jalan Kebudayaan, Taman Universiti, 81300 Skudai, Johor",
      latitude = 1.5360,
      longitude = 103.6280,
      capacity = 600,
      hasFridayPrayer = true,
      facilities = listOf("Solat Jumaat", "Dekat UTM Skudai", "Ruang Muslimah", "Parkir"),
      phone = "+607-521 3344"
    ),

    // PNG01: Surau Pulau Pinang
    Mosque(
      id = "surau_bayan_lepas",
      name = "Surau Jumaat Bayan Lepas",
      arabicName = "مصلى بيان ليباس الجمعة",
      type = "Surau",
      zoneCode = "PNG01",
      state = "Pulau Pinang",
      district = "Barat Daya",
      address = "Jalan Sultan Azlan Shah, 11900 Bayan Lepas, Pulau Pinang",
      latitude = 5.3010,
      longitude = 100.2780,
      capacity = 650,
      hasFridayPrayer = true,
      facilities = listOf("Solat Jumaat", "Dekat Lapangan Terbang Antarabangsa", "Pendingin Hawa"),
      phone = "+604-643 8811"
    ),

    // KTN01: Surau Kelantan
    Mosque(
      id = "surau_al_ittihad_kb",
      name = "Surau Al-Ittihad Kota Bharu",
      arabicName = "مصلى الاتحاد",
      type = "Surau",
      zoneCode = "KTN01",
      state = "Kelantan",
      district = "Kota Bharu",
      address = "Pengkalan Chepa, 16100 Kota Bharu, Kelantan",
      latitude = 6.1250,
      longitude = 102.2450,
      capacity = 450,
      hasFridayPrayer = false,
      facilities = listOf("Solat 5 Waktu", "Kuliah Kitab Pondok", "Ruang Muslimah"),
      phone = "+609-773 2211"
    ),

    // PRK02: Surau Perak
    Mosque(
      id = "surau_al_ehsan_ipoh",
      name = "Surau Al-Ehsan Bandar Ipoh",
      arabicName = "مصلى الإحسان إيبوه",
      type = "Surau",
      zoneCode = "PRK02",
      state = "Perak",
      district = "Kinta",
      address = "Jalan Kampar, 30250 Ipoh, Perak",
      latitude = 4.5950,
      longitude = 101.0850,
      capacity = 500,
      hasFridayPrayer = false,
      facilities = listOf("Solat 5 Waktu", "Pusat Bandar", "Pendingin Hawa"),
      phone = "+605-254 3322"
    ),

    // MLK01: Surau Melaka
    Mosque(
      id = "surau_al_faizin_melaka",
      name = "Surau Al-Faizin Bandar Hilir Melaka",
      arabicName = "مصلى الفائزين",
      type = "Surau",
      zoneCode = "MLK01",
      state = "Melaka",
      district = "Melaka Tengah",
      address = "Bandar Hilir, 75000 Melaka",
      latitude = 2.2050,
      longitude = 102.2480,
      capacity = 400,
      hasFridayPrayer = false,
      facilities = listOf("Solat 5 Waktu", "Kawasan Warisan Pelancongan", "Ruang Muslimah"),
      phone = "+606-282 4455"
    ),

    // NGS02: Surau Negeri Sembilan
    Mosque(
      id = "surau_darul_ulum_seremban",
      name = "Surau Darul Ulum Seremban 2",
      arabicName = "مصلى دار العلوم",
      type = "Surau",
      zoneCode = "NGS02",
      state = "Negeri Sembilan",
      district = "Seremban",
      address = "Garden Homes, Seremban 2, 70300 Seremban",
      latitude = 2.7230,
      longitude = 101.9380,
      capacity = 600,
      hasFridayPrayer = true,
      facilities = listOf("Solat Jumaat", "Parkir Komuniti", "Taman Rekreasi"),
      phone = "+606-601 2299"
    ),

    // SBH01: Surau Sabah
    Mosque(
      id = "surau_al_huda_kk",
      name = "Surau Al-Huda Sembulan",
      arabicName = "مصلى الهدى كوتا كينابالو",
      type = "Surau",
      zoneCode = "SBH01",
      state = "Sabah",
      district = "Kota Kinabalu",
      address = "Kampung Sembulan, 88100 Kota Kinabalu, Sabah",
      latitude = 5.9750,
      longitude = 116.0820,
      capacity = 450,
      hasFridayPrayer = false,
      facilities = listOf("Solat 5 Waktu", "Tepi Laut", "Kawasan Perkampungan"),
      phone = "+6088-245 678"
    ),

    // SWK01: Surau Sarawak
    Mosque(
      id = "surau_darul_falah_kuching",
      name = "Surau Darul Falah Petra Jaya",
      arabicName = "مصلى دار الفلاح كوتشينغ",
      type = "Surau",
      zoneCode = "SWK01",
      state = "Sarawak",
      district = "Kuching",
      address = "Tupong Batu, Petra Jaya, 93050 Kuching",
      latitude = 1.5520,
      longitude = 110.3420,
      capacity = 500,
      hasFridayPrayer = false,
      facilities = listOf("Solat 5 Waktu", "Komuniti Melayu Sarawak", "Dewan Mengaji"),
      phone = "+6082-412 900"
    )
  )

  /**
   * Calculates Haversine distance in kilometers between two lat/lng coordinates.
   */
  fun calculateDistanceKm(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
    val earthRadiusKm = 6371.0
    val dLat = Math.toRadians(lat2 - lat1)
    val dLon = Math.toRadians(lon2 - lon1)
    val a = sin(dLat / 2).pow(2.0) +
            cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
            sin(dLon / 2).pow(2.0)
    val c = 2 * atan2(sqrt(a), sqrt(1 - a))
    return earthRadiusKm * c
  }

  /**
   * Returns mosques sorted by proximity to the specified latitude and longitude,
   * with each mosque updated with its computed distanceKm.
   */
  fun getNearbyMosques(userLat: Double, userLon: Double): List<Mosque> {
    return ALL_MOSQUES.map { mosque ->
      val dist = calculateDistanceKm(userLat, userLon, mosque.latitude, mosque.longitude)
      mosque.copy(distanceKm = (dist * 10).roundToInt() / 10.0)
    }.sortedBy { it.distanceKm }
  }
}
