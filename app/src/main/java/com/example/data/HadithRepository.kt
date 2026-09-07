package com.example.data

import android.content.Context
import android.content.Intent
import com.example.model.Hadith
import java.util.Calendar

object HadithRepository {

  val HADITH_COLLECTION: List<Hadith> = listOf(
    Hadith(
      id = 1,
      arabicText = "أَحَبُّ الأَعْمَالِ إِلَى اللَّهِ الصَّلاَةُ عَلَى وَقْتِهَا",
      translationMs = "Amalan yang paling dicintai oleh Allah adalah solat pada waktunya.",
      narrator = "Riwayat Abdullah bin Mas'ud RA",
      sourceBook = "Sahih al-Bukhari (No. 527) & Sahih Muslim (No. 85)",
      chapter = "Keutamaan Solat Tepat Pada Waktunya"
    ),
    Hadith(
      id = 2,
      arabicText = "مَنْ صَلَّى الْبَرْدَيْنِ دَخَلَ الْجَنَّةَ",
      translationMs = "Sesiapa yang mendirikan solat pada dua waktu yang dingin (Subuh dan Asar), nescaya dia akan masuk syurga.",
      narrator = "Riwayat Abu Musa al-Asy'ari RA",
      sourceBook = "Sahih al-Bukhari (No. 574) & Sahih Muslim (No. 635)",
      chapter = "Keutamaan Solat Subuh & Asar"
    ),
    Hadith(
      id = 3,
      arabicText = "خَيْرُكُمْ مَنْ تَعَلَّمَ الْقُرْآنَ وَعَلَّمَهُ",
      translationMs = "Sebaik-baik kalian adalah orang yang mempelajari Al-Quran dan mengajarkannya kepada orang lain.",
      narrator = "Riwayat Uthman bin Affan RA",
      sourceBook = "Sahih al-Bukhari (No. 5027)",
      chapter = "Keutamaan Membaca & Mengajar Al-Quran"
    ),
    Hadith(
      id = 4,
      arabicText = "تَبَسُّمُكَ فِي وَجْهِ أَخِيكَ لَكَ صَدَقَةٌ",
      translationMs = "Senyumanmu di hadapan saudaramu adalah satu sedekah bagimu.",
      narrator = "Riwayat Abu Dzar RA",
      sourceBook = "Jami' at-Tirmidhi (No. 1956 - Hasan)",
      chapter = "Kemuliaan Akhlak & Sedekah"
    ),
    Hadith(
      id = 5,
      arabicText = "الْمُسْلِمُ مَنْ سَلِمَ الْمُسْلِمُونَ مِنْ لِسَانِهِ وَيَدِهِ",
      translationMs = "Seorang Muslim yang sejati adalah orang yang mana orang-orang Muslim lain selamat dari bahaya lidah dan tangannya.",
      narrator = "Riwayat Abdullah bin Amr RA",
      sourceBook = "Sahih al-Bukhari (No. 10) & Sahih Muslim (No. 40)",
      chapter = "Menjaga Lisan dan Tangan"
    ),
    Hadith(
      id = 6,
      arabicText = "مَنْ سَلَكَ طَرِيقًا يَلْتَمِسُ فِيهِ عِلْمًا سَهَّلَ اللَّهُ لَهُ بِهِ طَرِيقًا إِلَى الْجَنَّةِ",
      translationMs = "Barangsiapa yang menempuh satu jalan untuk mencari ilmu, nescaya Allah akan memudahkan baginya jalan menuju ke Syurga.",
      narrator = "Riwayat Abu Hurairah RA",
      sourceBook = "Sahih Muslim (No. 2699)",
      chapter = "Kelebihan Menuntut Ilmu"
    ),
    Hadith(
      id = 7,
      arabicText = "إِنَّمَا الأَعْمَالُ بِالنِّيَّاتِ وَإِنَّمَا لِكُلِّ امْرِئٍ مَا نَوَى",
      translationMs = "Sesungguhnya setiap amalan itu bergantung kepada niat, dan bagi setiap orang apa yang dia niatkan.",
      narrator = "Riwayat Umar bin al-Khattab RA",
      sourceBook = "Sahih al-Bukhari (No. 1) & Sahih Muslim (No. 1907)",
      chapter = "Ikhlas dan Niat Kerana Allah"
    ),
    Hadith(
      id = 8,
      arabicText = "مَا نَقَصَتْ صَدَقَةٌ مِنْ مَالٍ وَمَا زَادَ اللَّهُ عَبْدًا بِعَفْوٍ إِلاَّ عِزًّا",
      translationMs = "Sedekah tidak akan mengurangkan harta, dan tidaklah Allah menambah bagi seorang hamba yang memberi kemaafan melainkan kemuliaan.",
      narrator = "Riwayat Abu Hurairah RA",
      sourceBook = "Sahih Muslim (No. 2588)",
      chapter = "Keberkatan Sedekah dan Memaafkan"
    ),
    Hadith(
      id = 9,
      arabicText = "رَكْعَتَا الْفَجْرِ خَيْرٌ مِنَ الدُّنْيَا وَمَا فِيهَا",
      translationMs = "Dua rakaat sunat fajar (sebelum Subuh) adalah lebih baik daripada dunia dan segala isinya.",
      narrator = "Riwayat Ummul Mukminin Aisyah RA",
      sourceBook = "Sahih Muslim (No. 725)",
      chapter = "Keutamaan Solat Sunat Rawatib Subuh"
    )
  )

  fun getTodayHadith(): Hadith {
    val cal = Calendar.getInstance()
    val dayOfYear = cal.get(Calendar.DAY_OF_YEAR)
    val index = dayOfYear % HADITH_COLLECTION.size
    return HADITH_COLLECTION[index]
  }

  fun formatForSharing(hadith: Hadith): String {
    return buildString {
      append("✨ Hadis Pilihan Hari Ini ✨\n\n")
      append("${hadith.arabicText}\n\n")
      append("\"${hadith.translationMs}\"\n\n")
      append("📖 ${hadith.narrator}\n")
      append("📚 ${hadith.sourceBook}\n")
      append("🔖 Topik: ${hadith.chapter}\n\n")
      append("🤲 Dikongsi daripada Aplikasi Waktu Solat & Kiblat")
    }
  }

  fun shareHadith(context: Context, hadith: Hadith) {
    val shareText = formatForSharing(hadith)
    val sendIntent = Intent(Intent.ACTION_SEND).apply {
      type = "text/plain"
      putExtra(Intent.EXTRA_TEXT, shareText)
      putExtra(Intent.EXTRA_SUBJECT, "Hadis Hari Ini: ${hadith.chapter}")
    }
    val chooser = Intent.createChooser(sendIntent, "Kongsi Hadis ke...")
    chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    context.startActivity(chooser)
  }
}
