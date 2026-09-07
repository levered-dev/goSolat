package com.example.audio

import android.content.Context
import com.example.model.AzanSound
import java.io.File
import java.io.FileOutputStream

object AzanRepository {

  val PRESET_RAW_NAMES: Map<String, String> = mapOf(
    "makkah_ali_mulla" to "azan_makkah",
    "madinah_surayhi" to "azan_madinah"
  )

  val PRESET_AZANS: List<AzanSound> = listOf(
    AzanSound(
      id = "makkah_ali_mulla",
      title = "Azan Makkah Al-Mukarramah",
      muazzin = "Sheikh Ali Ahmad Mulla",
      origin = "Masjidil Haram, Makkah",
      youtubeId = "dQw4w9WgXcQ",
      audioUrl = "https://upload.wikimedia.org/wikipedia/commons/a/a7/Adhan%2C_Great_Mosque_of_Mecca_-_Jan_21%2C_2013.webm",
      duration = "3:17",
      isSpecialSubuh = false
    ),
    AzanSound(
      id = "madinah_surayhi",
      title = "Azan Madinah Al-Munawwarah",
      muazzin = "Sheikh Abdul Majid Surayhi",
      origin = "Masjid Nabawi, Madinah",
      youtubeId = "video_madinah_azan",
      audioUrl = "https://upload.wikimedia.org/wikipedia/commons/7/7d/The_Adhan_-_Muslim_Call_to_Prayer_-_Aaqib_Azeez.mp3",
      duration = "1:26",
      isSpecialSubuh = false
    )
  )

  fun getRawResourceId(context: Context, azanId: String): Int? {
    val rawName = PRESET_RAW_NAMES[azanId] ?: return null
    val resId = context.resources.getIdentifier(rawName, "raw", context.packageName)
    return if (resId != 0) resId else null
  }

  fun getAzanStorageDir(context: Context): File {
    val dir = File(context.filesDir, "azan_audio")
    if (!dir.exists()) {
      dir.mkdirs()
    }
    return dir
  }

  fun getLocalAudioFile(context: Context, azanId: String): File {
    return File(getAzanStorageDir(context), "azan_${azanId}.mp3")
  }

  fun ensurePresetMp3File(context: Context, azanId: String): File {
    val file = getLocalAudioFile(context, azanId)
    if (!file.exists() || file.length() < 1000) {
      val rawResId = getRawResourceId(context, azanId)
      if (rawResId != null) {
        try {
          val inputStream = context.resources.openRawResource(rawResId)
          val outputStream = FileOutputStream(file)
          inputStream.copyTo(outputStream)
          outputStream.flush()
          outputStream.close()
          inputStream.close()
        } catch (e: Exception) {
          e.printStackTrace()
        }
      }
    }
    return file
  }

  fun isAzanDownloaded(context: Context, azanId: String): Boolean {
    // Preset sounds bundled in raw MP3 resources are immediately available offline
    if (getRawResourceId(context, azanId) != null) {
      return true
    }
    val file = getLocalAudioFile(context, azanId)
    return file.exists() && file.length() > 5000
  }
}
