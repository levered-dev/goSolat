package com.example.ui.components

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AzanSound
import com.example.model.PrayerType
import com.example.prayer.PrayerNotificationHelper
import com.example.ui.theme.*

@Composable
fun AzanSettingsView(
  azanList: List<AzanSound>,
  defaultAzanId: String,
  subuhAzanId: String,
  playingAzanId: String?,
  isPlaying: Boolean,
  downloadProgress: Map<String, Float>,
  azanVolume: Float,
  enabledPrayers: Set<PrayerType>,
  onPlayAzan: (AzanSound) -> Unit,
  onStopAzan: () -> Unit,
  onDownloadAzan: (AzanSound) -> Unit,
  onDeleteAzan: (String) -> Unit,
  onSetDefaultAzan: (String) -> Unit,
  onSetSubuhAzan: (String) -> Unit,
  onVolumeChange: (Float) -> Unit,
  onTogglePrayer: (PrayerType) -> Unit,
  onAddCustomYoutube: (title: String, url: String) -> Unit = { _, _ -> },
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current

  LazyColumn(
    modifier = modifier
      .fillMaxWidth()
      .padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // Top Banner
    item {
      Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth().testTag("azan_management_card")
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = "Bunyi Azan Penting (Makkah & Madinah)",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
              )
              Text(
                text = "Pilihan rasmi azan merdu dari Dua Tanah Suci: Masjidil Haram & Masjid Nabawi.",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }

            Surface(
              shape = RoundedCornerShape(12.dp),
              color = Emerald100
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Icon(Icons.Default.Verified, contentDescription = null, tint = Emerald800, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Dua Tanah Suci", fontSize = 11.sp, color = Emerald900, fontWeight = FontWeight.Bold)
              }
            }
          }
        }
      }
    }

    // Daily Notification Toggles Card
    item {
      Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Text(
            text = "Pemberitahuan Azan Harian Tepat Waktu",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
          )
          Text(
            text = "Notifikasi automatik akan berbunyi tepat semasa masuk waktu solat",
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )

          Spacer(modifier = Modifier.height(12.dp))

          // 5 Fardhu prayer checkboxes
          val fardhuPrayers = listOf(
            PrayerType.SUBUH, PrayerType.ZOHOR, PrayerType.ASAR, PrayerType.MAGHRIB, PrayerType.ISYAK
          )

          fardhuPrayers.forEach { prayer ->
            val isEnabled = enabledPrayers.contains(prayer)
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                  text = "Solat ${prayer.displayName} (${prayer.arabicName})",
                  fontSize = 14.sp,
                  fontWeight = FontWeight.Medium,
                  color = MaterialTheme.colorScheme.onSurface
                )
                if (prayer == PrayerType.SUBUH) {
                  Spacer(modifier = Modifier.width(6.dp))
                  Surface(shape = RoundedCornerShape(6.dp), color = Gold400.copy(alpha = 0.2f)) {
                    Text(
                      text = "Khas Subuh",
                      fontSize = 10.sp,
                      color = Gold800,
                      fontWeight = FontWeight.Bold,
                      modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                  }
                }
              }

              Switch(
                checked = isEnabled,
                onCheckedChange = { onTogglePrayer(prayer) },
                colors = SwitchDefaults.colors(
                  checkedThumbColor = Color.White,
                  checkedTrackColor = Emerald700
                ),
                modifier = Modifier.testTag("switch_notif_${prayer.name.lowercase()}")
              )
            }
          }

          Spacer(modifier = Modifier.height(8.dp))
          Divider(color = MaterialTheme.colorScheme.surfaceVariant)
          Spacer(modifier = Modifier.height(8.dp))

          // Volume slider
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(text = "Kelantangan Azan:", fontSize = 13.sp, fontWeight = FontWeight.Medium)
            Text(text = "${(azanVolume * 100).toInt()}%", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Emerald800)
          }

          Slider(
            value = azanVolume,
            onValueChange = onVolumeChange,
            valueRange = 0.1f..1.0f,
            colors = SliderDefaults.colors(
              thumbColor = Emerald700,
              activeTrackColor = Emerald600
            ),
            modifier = Modifier.fillMaxWidth().testTag("azan_volume_slider")
          )

          Spacer(modifier = Modifier.height(4.dp))

          // Test notification button
          OutlinedButton(
            onClick = {
              PrayerNotificationHelper.showPrayerNotification(
                context,
                PrayerType.ASAR,
                "Kawasan Anda",
                "16:30"
              )
              Toast.makeText(context, "Ujian pemberitahuan solat telah dihantar!", Toast.LENGTH_SHORT).show()
            },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth().testTag("test_notification_button")
          ) {
            Icon(Icons.Default.NotificationsActive, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Uji Pemberitahuan Azan Sekarang", fontSize = 13.sp)
          }
        }
      }
    }

    // List of Azan audio choices
    item {
      Text(
        text = "Bunyi Azan Rasmi (Makkah & Madinah)",
        fontSize = 15.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurface
      )
    }

    items(azanList) { azan ->
      AzanSoundCard(
        azan = azan,
        isDefault = azan.id == defaultAzanId,
        isSubuh = azan.id == subuhAzanId,
        isPlaying = isPlaying && playingAzanId == azan.id,
        downloadProgress = downloadProgress[azan.id],
        onPlay = { onPlayAzan(azan) },
        onStop = onStopAzan,
        onDownload = { onDownloadAzan(azan) },
        onDelete = { onDeleteAzan(azan.id) },
        onSetDefault = { onSetDefaultAzan(azan.id) },
        onSetSubuh = { onSetSubuhAzan(azan.id) }
      )
    }

    item {
      Spacer(modifier = Modifier.height(24.dp))
    }
  }
}

@Composable
fun AzanSoundCard(
  azan: AzanSound,
  isDefault: Boolean,
  isSubuh: Boolean,
  isPlaying: Boolean,
  downloadProgress: Float?,
  onPlay: () -> Unit,
  onStop: () -> Unit,
  onDownload: () -> Unit,
  onDelete: () -> Unit,
  onSetDefault: () -> Unit,
  onSetSubuh: () -> Unit
) {
  Card(
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(
      containerColor = if (isDefault || isSubuh) Emerald50.copy(alpha = 0.6f) else MaterialTheme.colorScheme.surface
    ),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    border = if (isDefault || isSubuh) androidx.compose.foundation.BorderStroke(1.5.dp, Emerald600) else null,
    modifier = Modifier.fillMaxWidth().testTag("azan_card_${azan.id}")
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = azan.title,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
          )
          Text(
            text = "${azan.muazzin} • ${azan.origin}",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
          Text(
            text = "🕌 Seruan Azan Asli • Tempoh: ${azan.duration}",
            fontSize = 11.sp,
            color = Emerald700
          )
        }

        // Play / Stop button
        IconButton(
          onClick = { if (isPlaying) onStop() else onPlay() },
          modifier = Modifier
            .size(44.dp)
            .clip(CircleShape)
            .background(if (isPlaying) Color(0xFFEF4444) else Emerald700)
            .testTag("play_azan_${azan.id}")
        ) {
          Icon(
            imageVector = if (isPlaying) Icons.Default.Stop else Icons.Default.PlayArrow,
            contentDescription = if (isPlaying) "Berhenti" else "Mainkan",
            tint = Color.White,
            modifier = Modifier.size(24.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Download progress or status badge
      if (downloadProgress != null) {
        Column(modifier = Modifier.fillMaxWidth()) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text(text = "Memuat turun fail audio...", fontSize = 11.sp, color = Emerald800)
            Text(text = "${(downloadProgress * 100).toInt()}%", fontSize = 11.sp, fontWeight = FontWeight.Bold)
          }
          LinearProgressIndicator(
            progress = { downloadProgress },
            modifier = Modifier
              .fillMaxWidth()
              .height(6.dp)
              .clip(RoundedCornerShape(3.dp)),
            color = Emerald700
          )
        }
      } else {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          if (azan.isDownloaded) {
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = Emerald100
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Icon(Icons.Default.CloudDone, contentDescription = null, tint = Emerald800, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "Tersimpan Luar Talian (Offline)", fontSize = 11.sp, color = Emerald900, fontWeight = FontWeight.SemiBold)
              }
            }
          } else {
            Button(
              onClick = onDownload,
              shape = RoundedCornerShape(10.dp),
              colors = ButtonDefaults.buttonColors(containerColor = Emerald800),
              contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
              modifier = Modifier.height(32.dp).testTag("download_azan_${azan.id}")
            ) {
              Icon(Icons.Default.CloudDownload, contentDescription = null, modifier = Modifier.size(14.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text("Muat Turun Luar Talian", fontSize = 11.sp)
            }
          }

          // Active role badges
          Row {
            if (isDefault) {
              Surface(shape = RoundedCornerShape(8.dp), color = Emerald700) {
                Text(
                  text = "Azan Utama",
                  color = Color.White,
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
              }
            }
            if (isSubuh) {
              Spacer(modifier = Modifier.width(4.dp))
              Surface(shape = RoundedCornerShape(8.dp), color = Gold600) {
                Text(
                  text = "Azan Subuh",
                  color = Color.White,
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Action buttons: Set as default or Set as subuh
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
      ) {
        if (!isDefault) {
          OutlinedButton(
            onClick = onSetDefault,
            shape = RoundedCornerShape(10.dp),
            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
            modifier = Modifier.height(32.dp).testTag("set_default_azan_${azan.id}")
          ) {
            Text("Pilih Azan Utama", fontSize = 11.sp)
          }
        }

        Spacer(modifier = Modifier.width(6.dp))

        if (!isSubuh) {
          OutlinedButton(
            onClick = onSetSubuh,
            shape = RoundedCornerShape(10.dp),
            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
            modifier = Modifier.height(32.dp).testTag("set_subuh_azan_${azan.id}")
          ) {
            Text("Pilih Azan Subuh", fontSize = 11.sp)
          }
        }

        if (azan.isDownloaded) {
          Spacer(modifier = Modifier.width(6.dp))
          IconButton(
            onClick = onDelete,
            modifier = Modifier.size(32.dp)
          ) {
            Icon(Icons.Default.DeleteOutline, contentDescription = "Padam Audio", tint = Color(0xFFEF4444), modifier = Modifier.size(18.dp))
          }
        }
      }
    }
  }
}
