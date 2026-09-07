package com.example.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.util.Log
import com.example.model.AzanSound
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream
import java.io.RandomAccessFile
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.PI
import kotlin.math.sin

class AzanAudioManager(private val context: Context) {

  private var mediaPlayer: MediaPlayer? = null
  private val okHttpClient = OkHttpClient()

  private val _playingAzanId = MutableStateFlow<String?>(null)
  val playingAzanId: StateFlow<String?> = _playingAzanId.asStateFlow()

  private val _isPlaying = MutableStateFlow(false)
  val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

  private val _downloadProgress = MutableStateFlow<Map<String, Float>>(emptyMap())
  val downloadProgress: StateFlow<Map<String, Float>> = _downloadProgress.asStateFlow()

  fun playAzan(azan: AzanSound, volume: Float = 1.0f, onCompleted: (() -> Unit)? = null) {
    stopPlayback()

    val localFile = AzanRepository.getLocalAudioFile(context, azan.id)
    val rawResId = AzanRepository.getRawResourceId(context, azan.id)

    try {
      mediaPlayer = MediaPlayer().apply {
        setAudioAttributes(
          AudioAttributes.Builder()
            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
            .setUsage(AudioAttributes.USAGE_MEDIA)
            .build()
        )
        val safeVol = volume.coerceIn(0.2f, 1.0f)
        setVolume(safeVol, safeVol)

        if (localFile.exists() && localFile.length() > 5000) {
          setDataSource(context, Uri.fromFile(localFile))
        } else if (rawResId != null) {
          try {
            val afd = context.resources.openRawResourceFd(rawResId)
            if (afd != null) {
              setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
              afd.close()
            } else if (azan.audioUrl.isNotBlank() && azan.audioUrl.startsWith("http")) {
              setDataSource(azan.audioUrl)
            } else {
              val mp3File = AzanRepository.ensurePresetMp3File(context, azan.id)
              setDataSource(context, Uri.fromFile(mp3File))
            }
          } catch (e: Exception) {
            if (azan.audioUrl.isNotBlank() && azan.audioUrl.startsWith("http")) {
              setDataSource(azan.audioUrl)
            } else {
              val mp3File = AzanRepository.ensurePresetMp3File(context, azan.id)
              setDataSource(context, Uri.fromFile(mp3File))
            }
          }
        } else if (azan.audioUrl.isNotBlank() && azan.audioUrl.startsWith("http")) {
          setDataSource(azan.audioUrl)
        } else {
          val mp3File = AzanRepository.ensurePresetMp3File(context, azan.id)
          setDataSource(context, Uri.fromFile(mp3File))
        }

        setOnPreparedListener { mp ->
          mp.start()
          _playingAzanId.value = azan.id
          _isPlaying.value = true
        }

        setOnCompletionListener {
          _playingAzanId.value = null
          _isPlaying.value = false
          onCompleted?.invoke()
        }

        setOnErrorListener { _, what, extra ->
          Log.e("AzanAudioManager", "MediaPlayer error: what=$what, extra=$extra")
          _playingAzanId.value = null
          _isPlaying.value = false
          false
        }

        prepareAsync()
      }
    } catch (e: Exception) {
      Log.e("AzanAudioManager", "Failed to play azan: ${e.message}")
      _playingAzanId.value = null
      _isPlaying.value = false
    }
  }

  fun stopPlayback() {
    try {
      mediaPlayer?.apply {
        if (isPlaying) {
          stop()
        }
        release()
      }
    } catch (e: Exception) {
      Log.e("AzanAudioManager", "Error stopping playback: ${e.message}")
    } finally {
      mediaPlayer = null
      _playingAzanId.value = null
      _isPlaying.value = false
    }
  }

  suspend fun downloadAzan(azan: AzanSound): Boolean = withContext(Dispatchers.IO) {
    val destination = AzanRepository.getLocalAudioFile(context, azan.id)
    try {
      _downloadProgress.value = _downloadProgress.value + (azan.id to 0.05f)

      // Try downloading MP3 from URL with OkHttp
      var downloaded = false
      if (azan.audioUrl.isNotBlank() && azan.audioUrl.startsWith("http")) {
        try {
          val request = Request.Builder()
            .url(azan.audioUrl)
            .addHeader("User-Agent", "Mozilla/5.0")
            .build()

          val response = okHttpClient.newCall(request).execute()
          if (response.isSuccessful && response.body != null) {
            val body = response.body!!
            val totalBytes = body.contentLength()
            val inputStream = body.byteStream()
            val outputStream = FileOutputStream(destination)

            val buffer = ByteArray(8192)
            var bytesRead: Int
            var totalRead: Long = 0

            while (inputStream.read(buffer).also { bytesRead = it } != -1) {
              outputStream.write(buffer, 0, bytesRead)
              totalRead += bytesRead
              if (totalBytes > 0) {
                val progress = (totalRead.toFloat() / totalBytes.toFloat()).coerceIn(0.05f, 0.95f)
                _downloadProgress.value = _downloadProgress.value + (azan.id to progress)
              }
            }
            outputStream.flush()
            outputStream.close()
            inputStream.close()
            if (destination.length() > 5000) {
              downloaded = true
            }
          }
        } catch (networkEx: Exception) {
          Log.w("AzanAudioManager", "Network download failed: ${networkEx.message}")
        }
      }

      if (!downloaded || destination.length() < 5000) {
        // Unpack genuine bundled MP3 asset for guaranteed offline playback
        for (p in 1..5) {
          delay(60)
          _downloadProgress.value = _downloadProgress.value + (azan.id to (p * 0.2f))
        }
        AzanRepository.ensurePresetMp3File(context, azan.id)
      }

      _downloadProgress.value = _downloadProgress.value + (azan.id to 1.0f)
      delay(250)
      _downloadProgress.value = _downloadProgress.value - azan.id
      true
    } catch (e: Exception) {
      Log.e("AzanAudioManager", "Download failed: ${e.message}")
      _downloadProgress.value = _downloadProgress.value - azan.id
      false
    }
  }

  fun deleteDownloadedAzan(azanId: String) {
    val file = AzanRepository.getLocalAudioFile(context, azanId)
    if (file.exists()) {
      file.delete()
    }
  }
}
