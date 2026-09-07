package com.example.ui.components

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.PrayerZone
import com.example.prayer.QiblaCalculator
import com.example.sensor.CompassState
import com.example.ui.theme.*
import kotlin.math.*

@Composable
fun QiblaCompassView(
  compassState: CompassState,
  qiblaBearing: Double,
  distanceKm: Double,
  zone: PrayerZone,
  onOpenArMode: () -> Unit,
  onOpenMapView: () -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val isAligned = remember(compassState.azimuth, qiblaBearing) {
    QiblaCalculator.isAligned(compassState.azimuth, qiblaBearing, thresholdDegrees = 3.5f)
  }

  // Trigger haptic vibration once when entering alignment
  var wasAligned by remember { mutableStateOf(false) }
  LaunchedEffect(isAligned) {
    if (isAligned && !wasAligned) {
      triggerVibration(context)
    }
    wasAligned = isAligned
  }

  // Smooth compass needle rotation
  val animatedHeading by animateFloatAsState(
    targetValue = compassState.azimuth,
    animationSpec = tween(120),
    label = "compass_rot"
  )

  val needleAngle = (qiblaBearing - animatedHeading + 360) % 360

  val statusColor by animateColorAsState(
    targetValue = if (isAligned) QiblaGreen else Emerald700,
    label = "status_col"
  )

  Column(
    modifier = modifier
      .fillMaxWidth()
      .padding(16.dp),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    // Mode Switcher Buttons: [Kompas] [Peta Interaktif] [AR Kamera]
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.Center
    ) {
      Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        modifier = Modifier.padding(bottom = 12.dp)
      ) {
        Row(
          modifier = Modifier.padding(4.dp),
          horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          FilledTonalButton(
            onClick = {},
            colors = ButtonDefaults.filledTonalButtonColors(
              containerColor = Emerald700,
              contentColor = Color.White
            ),
            shape = RoundedCornerShape(12.dp),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
            modifier = Modifier.height(38.dp)
          ) {
            Icon(Icons.Default.Explore, contentDescription = "Kompas", modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Kompas", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
          }

          OutlinedButton(
            onClick = onOpenMapView,
            shape = RoundedCornerShape(12.dp),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
            modifier = Modifier.height(38.dp).testTag("qibla_map_button")
          ) {
            Icon(Icons.Default.Map, contentDescription = "Peta", modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Peta Interaktif", fontSize = 12.sp)
          }

          Button(
            onClick = onOpenArMode,
            colors = ButtonDefaults.buttonColors(
              containerColor = Gold600,
              contentColor = Color.White
            ),
            shape = RoundedCornerShape(12.dp),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
            modifier = Modifier.height(38.dp).testTag("qibla_ar_camera_button")
          ) {
            Icon(Icons.Default.CameraAlt, contentDescription = "AR Kamera", modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Kamera AR", fontSize = 12.sp, fontWeight = FontWeight.Bold)
          }
        }
      }
    }

    // Status Banner (Tepat Menghadap Kiblat vs Laraskan Telefon)
    Surface(
      shape = RoundedCornerShape(14.dp),
      color = if (isAligned) QiblaGreen.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant,
      border = androidx.compose.foundation.BorderStroke(1.5.dp, if (isAligned) QiblaGreen else Color.Transparent),
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 12.dp)
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(vertical = 10.dp, horizontal = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
      ) {
        Icon(
          imageVector = if (isAligned) Icons.Default.CheckCircle else Icons.Default.Navigation,
          contentDescription = "Status Kiblat",
          tint = if (isAligned) QiblaGreen else Gold600,
          modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = if (isAligned) "TEPAT MENGHADAP KIBLAT (KAABAH)" else "Pusing telefon sehingga jarum selari dengan Kaabah",
          fontSize = 12.sp,
          fontWeight = FontWeight.Bold,
          color = if (isAligned) Emerald900 else MaterialTheme.colorScheme.onSurface,
          textAlign = TextAlign.Center
        )
      }
    }

    Spacer(modifier = Modifier.height(18.dp))

    // Interactive Animated Compass Dial
    Box(
      modifier = Modifier
        .size(280.dp)
        .testTag("compass_canvas_box"),
      contentAlignment = Alignment.Center
    ) {
      Canvas(
        modifier = Modifier.fillMaxSize()
      ) {
        val center = Offset(size.width / 2f, size.height / 2f)
        val radius = size.minDimension / 2f - 16.dp.toPx()

        // Outer glow when aligned
        if (isAligned) {
          drawCircle(
            color = Color(0x3322C55E),
            radius = radius + 12.dp.toPx(),
            center = center
          )
        }

        // Compass background ring
        drawCircle(
          color = Color(0xFF1E293B),
          radius = radius,
          center = center
        )
        drawCircle(
          color = Color(0xFF0F172A),
          radius = radius - 8.dp.toPx(),
          center = center
        )

        // Draw compass tick marks (every 15 degrees)
        for (deg in 0 until 360 step 15) {
          val rad = Math.toRadians((deg - animatedHeading - 90).toDouble())
          val isMajor = deg % 90 == 0
          val isMedium = deg % 30 == 0
          val tickLen = if (isMajor) 14.dp.toPx() else if (isMedium) 9.dp.toPx() else 5.dp.toPx()
          val strokeW = if (isMajor) 3.dp.toPx() else 1.5.dp.toPx()
          val tickColor = if (isMajor) Color(0xFFFBBF24) else Color(0x8894A3B8)

          val startX = center.x + (radius - 12.dp.toPx()) * cos(rad).toFloat()
          val startY = center.y + (radius - 12.dp.toPx()) * sin(rad).toFloat()
          val endX = center.x + (radius - 12.dp.toPx() - tickLen) * cos(rad).toFloat()
          val endY = center.y + (radius - 12.dp.toPx() - tickLen) * sin(rad).toFloat()

          drawLine(
            color = tickColor,
            start = Offset(startX, startY),
            end = Offset(endX, endY),
            strokeWidth = strokeW,
            cap = StrokeCap.Round
          )
        }

        // Draw Kaaba Pointer Needle (Points at needleAngle)
        rotate(needleAngle.toFloat(), pivot = center) {
          val needlePath = Path().apply {
            moveTo(center.x, center.y - radius + 22.dp.toPx())
            lineTo(center.x - 14.dp.toPx(), center.y)
            lineTo(center.x + 14.dp.toPx(), center.y)
            close()
          }

          drawPath(
            path = needlePath,
            brush = Brush.verticalGradient(
              colors = if (isAligned) listOf(Color(0xFF22C55E), Color(0xFF15803D))
              else listOf(Color(0xFFF59E0B), Color(0xFFB45309))
            )
          )

          // South tail of needle
          val tailPath = Path().apply {
            moveTo(center.x, center.y + radius - 30.dp.toPx())
            lineTo(center.x - 10.dp.toPx(), center.y)
            lineTo(center.x + 10.dp.toPx(), center.y)
            close()
          }
          drawPath(path = tailPath, color = Color(0x5564748B))

          // Draw Kaaba icon circle at the tip of the needle
          drawCircle(
            color = if (isAligned) Color(0xFF22C55E) else Color(0xFFFBBF24),
            radius = 16.dp.toPx(),
            center = Offset(center.x, center.y - radius + 32.dp.toPx())
          )
          drawCircle(
            color = Color(0xFF0F172A),
            radius = 12.dp.toPx(),
            center = Offset(center.x, center.y - radius + 32.dp.toPx())
          )
        }

        // Center hub
        drawCircle(color = Color(0xFFFBBF24), radius = 12.dp.toPx(), center = center)
        drawCircle(color = Color(0xFF0F172A), radius = 6.dp.toPx(), center = center)
      }

      // Center Kaaba text icon
      Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
          text = "🕋",
          fontSize = 26.sp
        )
      }
    }

    Spacer(modifier = Modifier.height(18.dp))

    // Readout cards: Arah Kiblat, Arah Telefon, Jarak ke Kaabah
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.weight(1f)
      ) {
        Column(
          modifier = Modifier.padding(12.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Text(text = "Arah Kiblat", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
          Text(
            text = "${String.format("%.1f", qiblaBearing)}°",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            color = Emerald800
          )
          Text(text = "Barat Laut", fontSize = 11.sp, color = Emerald700)
        }
      }

      Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.weight(1f)
      ) {
        Column(
          modifier = Modifier.padding(12.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Text(text = "Arah Telefon", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
          Text(
            text = "${String.format("%.0f", compassState.azimuth)}°",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            color = if (isAligned) QiblaGreen else MaterialTheme.colorScheme.onSurface
          )
          Text(
            text = if (isAligned) "Selari" else "Belum Selari",
            fontSize = 11.sp,
            color = if (isAligned) QiblaGreen else Color(0xFF64748B)
          )
        }
      }

      Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.weight(1f)
      ) {
        Column(
          modifier = Modifier.padding(12.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Text(text = "Jarak Kaabah", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
          Text(
            text = "${String.format("%,.0f", distanceKm)}",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            color = Gold700
          )
          Text(text = "kilometer", fontSize = 11.sp, color = Gold700)
        }
      }
    }

    Spacer(modifier = Modifier.height(14.dp))

    // Calibration tip
    Surface(
      shape = RoundedCornerShape(12.dp),
      color = Emerald50.copy(alpha = 0.6f),
      modifier = Modifier.fillMaxWidth()
    ) {
      Row(
        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Icon(
          imageVector = Icons.Default.Info,
          contentDescription = "Panduan",
          tint = Emerald800,
          modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = "Petua: Gerakkan telefon dalam bentuk angka 8 jika sensor kompas memerlukan penentukuran (kalibrasi).",
          fontSize = 11.sp,
          color = Emerald900,
          lineHeight = 16.sp
        )
      }
    }
  }
}

private fun triggerVibration(context: Context) {
  try {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
      val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
      vibratorManager?.defaultVibrator?.vibrate(VibrationEffect.createOneShot(120, VibrationEffect.DEFAULT_AMPLITUDE))
    } else {
      @Suppress("DEPRECATION")
      val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        vibrator?.vibrate(VibrationEffect.createOneShot(120, VibrationEffect.DEFAULT_AMPLITUDE))
      } else {
        @Suppress("DEPRECATION")
        vibrator?.vibrate(120)
      }
    }
  } catch (e: Exception) {
    // Ignore vibration failure
  }
}
