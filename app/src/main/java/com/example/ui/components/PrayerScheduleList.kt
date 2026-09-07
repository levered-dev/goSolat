package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.NotificationsOff
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.NextPrayerInfo
import com.example.model.PrayerItem
import com.example.model.PrayerSchedule
import com.example.model.PrayerType
import com.example.ui.theme.*

@Composable
fun PrayerScheduleList(
  schedule: PrayerSchedule,
  nextPrayerInfo: NextPrayerInfo,
  enabledPrayers: Set<PrayerType>,
  onToggleNotification: (PrayerType) -> Unit,
  modifier: Modifier = Modifier
) {
  Column(
    modifier = modifier.fillMaxWidth(),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    // Next Prayer Hero Card in SleekPrimary (#6750A4)
    NextPrayerCard(nextPrayerInfo = nextPrayerInfo)

    // Prayer Times Container Card
    Card(
      shape = RoundedCornerShape(28.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
      elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
      border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
      modifier = Modifier.fillMaxWidth().testTag("prayer_schedule_card")
    ) {
      Column(modifier = Modifier.fillMaxWidth()) {
        // Card Header
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp, vertical = 14.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "Waktu Solat",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )

          Surface(
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.secondaryContainer
          ) {
            Text(
              text = schedule.zoneCode,
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onSecondaryContainer,
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
            )
          }
        }

        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

        // List of 7 prayers separated by dividers
        schedule.items.forEachIndexed { index, item ->
          PrayerRowItem(
            item = item,
            isNext = item.type == nextPrayerInfo.nextPrayer,
            isNotificationEnabled = enabledPrayers.contains(item.type),
            onToggleNotification = { onToggleNotification(item.type) }
          )

          if (index < schedule.items.size - 1) {
            HorizontalDivider(
              color = SleekDivider,
              modifier = Modifier.padding(horizontal = 16.dp)
            )
          }
        }
      }
    }
  }
}

@Composable
fun NextPrayerCard(nextPrayerInfo: NextPrayerInfo) {
  Card(
    shape = RoundedCornerShape(28.dp),
    colors = CardDefaults.cardColors(containerColor = Color.Transparent),
    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    modifier = Modifier
      .fillMaxWidth()
      .background(
        brush = androidx.compose.ui.graphics.Brush.linearGradient(
          listOf(Color(0xFF1A73E8), Color(0xFF7C4DFF), Color(0xFFD96570))
        ),
        shape = RoundedCornerShape(28.dp)
      )
      .testTag("next_prayer_card")
  ) {
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .padding(20.dp)
    ) {
      // Subtle background watermark icon
      Icon(
        imageVector = Icons.Default.Mosque,
        contentDescription = null,
        tint = Color.White.copy(alpha = 0.12f),
        modifier = Modifier
          .size(130.dp)
          .align(Alignment.TopEnd)
          .offset(x = 16.dp, y = (-12).dp)
      )

      Column(modifier = Modifier.fillMaxWidth()) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.9f),
                modifier = Modifier.size(13.dp)
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = "SOLAT SETERUSNYA",
                color = Color.White.copy(alpha = 0.9f),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.2.sp
              )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
              text = nextPrayerInfo.nextPrayer.displayName,
              color = Color.White,
              fontSize = 32.sp,
              fontWeight = FontWeight.Bold
            )
          }

          // Countdown timer box
          Column(horizontalAlignment = Alignment.End) {
            val countdownStr = String.format(
              "%02dj %02dm %02ds",
              nextPrayerInfo.remainingHours,
              nextPrayerInfo.remainingMinutes,
              nextPrayerInfo.remainingSeconds
            )

            Text(
              text = countdownStr,
              color = Color.White,
              fontSize = 16.sp,
              fontWeight = FontWeight.Bold,
              fontFamily = FontFamily.Monospace,
              letterSpacing = 0.5.sp
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
              text = "Waktu: ${nextPrayerInfo.nextPrayerTimeFormatted}",
              color = Color.White.copy(alpha = 0.85f),
              fontSize = 12.sp
            )
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Progress bar indicating interval to next prayer
        LinearProgressIndicator(
          progress = { nextPrayerInfo.progressToNext },
          modifier = Modifier
            .fillMaxWidth()
            .height(6.dp)
            .clip(RoundedCornerShape(3.dp)),
          color = Color.White,
          trackColor = Color.White.copy(alpha = 0.25f)
        )
      }
    }
  }
}

@Composable
fun PrayerRowItem(
  item: PrayerItem,
  isNext: Boolean,
  isNotificationEnabled: Boolean,
  onToggleNotification: () -> Unit
) {
  val icon = getPrayerIcon(item.type)

  val rowBackground = if (isNext) {
    SleekPrimary.copy(alpha = 0.08f)
  } else {
    Color.Transparent
  }

  Row(
    modifier = Modifier
      .fillMaxWidth()
      .background(rowBackground)
      .padding(horizontal = 18.dp, vertical = 12.dp)
      .testTag("prayer_row_${item.type.name.lowercase()}"),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween
  ) {
    Row(verticalAlignment = Alignment.CenterVertically) {
      // Icon Box
      Box(
        modifier = Modifier
          .size(38.dp)
          .clip(RoundedCornerShape(12.dp))
          .background(
            if (isNext) SleekPrimaryContainer else if (item.isPassed) Color(0xFFF1F0F4) else SleekSecondaryContainer
          ),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = icon,
          contentDescription = item.type.displayName,
          tint = if (isNext) SleekPrimaryDark else if (item.isPassed) SleekTextTertiary else SleekPrimaryDark,
          modifier = Modifier.size(20.dp)
        )
      }

      Spacer(modifier = Modifier.width(12.dp))

      Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = item.type.displayName,
            fontSize = 15.sp,
            fontWeight = if (isNext) FontWeight.Bold else FontWeight.Medium,
            color = if (isNext) SleekPrimary else if (item.isPassed) SleekTextTertiary else MaterialTheme.colorScheme.onSurface
          )

          if (isNext) {
            Spacer(modifier = Modifier.width(6.dp))
            Box(
              modifier = Modifier
                .size(7.dp)
                .clip(CircleShape)
                .background(SleekPrimary)
            )
          }
        }

        Text(
          text = item.type.arabicName,
          fontSize = 11.sp,
          color = if (isNext) SleekPrimary.copy(alpha = 0.8f) else SleekTextTertiary
        )
      }
    }

    Row(verticalAlignment = Alignment.CenterVertically) {
      Column(horizontalAlignment = Alignment.End) {
        Text(
          text = item.timeFormatted,
          fontSize = 15.sp,
          fontWeight = if (isNext) FontWeight.Bold else FontWeight.SemiBold,
          color = if (isNext) SleekPrimary else if (item.isPassed) SleekTextTertiary else MaterialTheme.colorScheme.onSurface
        )
        Text(
          text = "24j: ${item.time24}",
          fontSize = 10.sp,
          color = SleekTextTertiary
        )
      }

      if (item.type.isFardhu) {
        Spacer(modifier = Modifier.width(8.dp))
        IconButton(
          onClick = onToggleNotification,
          modifier = Modifier
            .size(36.dp)
            .testTag("notif_toggle_${item.type.name.lowercase()}")
        ) {
          Icon(
            imageVector = if (isNotificationEnabled) Icons.Filled.Notifications else Icons.Outlined.NotificationsOff,
            contentDescription = if (isNotificationEnabled) "Azan Aktif" else "Azan Senyap",
            tint = if (isNotificationEnabled) SleekPrimary else SleekTextTertiary,
            modifier = Modifier.size(20.dp)
          )
        }
      }
    }
  }
}

fun getPrayerIcon(type: PrayerType): ImageVector {
  return when (type) {
    PrayerType.IMSAK -> Icons.Default.Bedtime
    PrayerType.SUBUH -> Icons.Default.WbTwilight
    PrayerType.SYURUK -> Icons.Default.WbSunny
    PrayerType.ZOHOR -> Icons.Default.LightMode
    PrayerType.ASAR -> Icons.Default.WbSunny
    PrayerType.MAGHRIB -> Icons.Default.WbTwilight
    PrayerType.ISYAK -> Icons.Default.NightsStay
  }
}

