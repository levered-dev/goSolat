package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material.icons.filled.ZoomOut
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.PrayerZone
import com.example.prayer.QiblaCalculator
import com.example.ui.theme.*
import kotlin.math.sin

@Composable
fun QiblaMapView(
  zone: PrayerZone,
  qiblaBearing: Double,
  distanceKm: Double,
  onBackToCompass: () -> Unit,
  modifier: Modifier = Modifier
) {
  var scale by remember { mutableStateOf(1.0f) }
  var offsetX by remember { mutableStateOf(0f) }
  var offsetY by remember { mutableStateOf(0f) }

  // Pulsing animation along the path
  val infiniteTransition = rememberInfiniteTransition(label = "pulse_map")
  val pulseProgress by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = 1f,
    animationSpec = infiniteRepeatable(
      animation = tween(2200, easing = LinearEasing),
      repeatMode = RepeatMode.Restart
    ),
    label = "pulse"
  )

  Column(
    modifier = modifier
      .fillMaxWidth()
      .padding(16.dp)
  ) {
    // Top Bar with back button
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        IconButton(
          onClick = onBackToCompass,
          modifier = Modifier.testTag("back_from_map_button")
        ) {
          Icon(Icons.Default.ArrowBack, contentDescription = "Kembali ke Kompas")
        }
        Spacer(modifier = Modifier.width(4.dp))
        Column {
          Text(
            text = "Peta Trajektori Kiblat",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
          )
          Text(
            text = "${zone.name} ➔ Kaabah (Makkah)",
            fontSize = 12.sp,
            color = Emerald700
          )
        }
      }

      Surface(
        shape = RoundedCornerShape(12.dp),
        color = Emerald100
      ) {
        Text(
          text = "${String.format("%.1f", qiblaBearing)}° Barat Laut",
          color = Emerald900,
          fontSize = 12.sp,
          fontWeight = FontWeight.Bold,
          modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
        )
      }
    }

    Spacer(modifier = Modifier.height(12.dp))

    // Interactive Canvas Map View
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(380.dp)
        .clip(RoundedCornerShape(20.dp))
        .background(Color(0xFF0F172A))
        .pointerInput(Unit) {
          detectTransformGestures { _, pan, zoom, _ ->
            scale = (scale * zoom).coerceIn(0.8f, 2.5f)
            offsetX += pan.x
            offsetY += pan.y
          }
        }
        .testTag("interactive_qibla_map")
    ) {
      Canvas(modifier = Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        // World Grid Lines (Latitude / Longitude Mercator style)
        val gridColor = Color(0x22334155)
        for (i in 0..10) {
          val y = h * (i / 10f)
          drawLine(gridColor, Offset(0f, y), Offset(w, y), strokeWidth = 1.dp.toPx())
          val x = w * (i / 10f)
          drawLine(gridColor, Offset(x, 0f), Offset(x, h), strokeWidth = 1.dp.toPx())
        }

        // Draw regional continents silhouettes simplified
        // Arabian Peninsula
        val makkahPos = Offset(w * 0.22f * scale + offsetX, h * 0.38f * scale + offsetY)
        // Southeast Asia (Malaysia)
        val userPos = Offset(w * 0.78f * scale + offsetX, h * 0.68f * scale + offsetY)

        // Trajectory arc path from User to Kaaba
        val trajectoryPath = Path().apply {
          moveTo(userPos.x, userPos.y)
          // Quadratic bezier curving slightly north towards the great circle
          val controlX = (userPos.x + makkahPos.x) / 2f
          val controlY = minOf(userPos.y, makkahPos.y) - 60.dp.toPx() * scale
          quadraticTo(controlX, controlY, makkahPos.x, makkahPos.y)
        }

        // Draw background great-circle dashed line
        drawPath(
          path = trajectoryPath,
          color = Color(0x66FBBF24),
          style = Stroke(
            width = 3.dp.toPx(),
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(20f, 15f), 0f)
          )
        )

        // Draw pulsing beacon along trajectory
        val currentPulsePos = Offset(
          userPos.x + (makkahPos.x - userPos.x) * pulseProgress,
          userPos.y + (makkahPos.y - userPos.y) * pulseProgress - sin(pulseProgress * Math.PI).toFloat() * 60.dp.toPx() * scale
        )
        drawCircle(
          color = Gold400,
          radius = 8.dp.toPx(),
          center = currentPulsePos
        )
        drawCircle(
          color = Gold400.copy(alpha = 0.4f),
          radius = 16.dp.toPx(),
          center = currentPulsePos
        )

        // User Position Marker (Malaysia)
        drawCircle(
          color = Color(0x4438BDF8),
          radius = 20.dp.toPx() * scale,
          center = userPos
        )
        drawCircle(
          color = Color(0xFF0284C7),
          radius = 10.dp.toPx() * scale,
          center = userPos
        )
        drawCircle(
          color = Color.White,
          radius = 4.dp.toPx() * scale,
          center = userPos
        )

        // Kaaba Marker (Makkah)
        drawCircle(
          color = Color(0x4422C55E),
          radius = 24.dp.toPx() * scale,
          center = makkahPos
        )
        drawCircle(
          color = Color(0xFF15803D),
          radius = 12.dp.toPx() * scale,
          center = makkahPos
        )
        drawCircle(
          color = Color(0xFFFBBF24),
          radius = 6.dp.toPx() * scale,
          center = makkahPos
        )
      }

      // Overlaid badges on top of canvas for labels
      // Kaaba Sanctuary Label
      Box(
        modifier = Modifier
          .align(Alignment.TopStart)
          .padding(16.dp)
      ) {
        Surface(
          shape = RoundedCornerShape(10.dp),
          color = Color(0xCC064E3B),
          border = androidx.compose.foundation.BorderStroke(1.dp, Gold400)
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(text = "🕋", fontSize = 16.sp)
            Spacer(modifier = Modifier.width(6.dp))
            Column {
              Text(text = "Kaabah, Makkah", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
              Text(text = "21.42° N, 39.83° E", color = Gold100, fontSize = 10.sp)
            }
          }
        }
      }

      // User location Label
      Box(
        modifier = Modifier
          .align(Alignment.BottomEnd)
          .padding(16.dp)
      ) {
        Surface(
          shape = RoundedCornerShape(10.dp),
          color = Color(0xCC0F172A),
          border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF38BDF8))
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(Icons.Default.NearMe, contentDescription = "Lokasi", tint = Color(0xFF38BDF8), modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Column {
              Text(text = zone.state, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
              Text(text = "Lokasi Semasa", color = Color(0xFFBAE6FD), fontSize = 10.sp)
            }
          }
        }
      }

      // Zoom Controls
      Column(
        modifier = Modifier
          .align(Alignment.CenterEnd)
          .padding(end = 12.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        IconButton(
          onClick = { scale = (scale + 0.2f).coerceAtMost(2.5f) },
          modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(Color(0x991E293B))
        ) {
          Icon(Icons.Default.ZoomIn, contentDescription = "Zum Masuk", tint = Color.White)
        }
        IconButton(
          onClick = { scale = (scale - 0.2f).coerceAtLeast(0.8f) },
          modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(Color(0x991E293B))
        ) {
          Icon(Icons.Default.ZoomOut, contentDescription = "Zum Keluar", tint = Color.White)
        }
      }
    }

    Spacer(modifier = Modifier.height(14.dp))

    // Distance & Navigation Details Card
    Card(
      shape = RoundedCornerShape(16.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
      elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
      modifier = Modifier.fillMaxWidth()
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(text = "Jarak Trajektori Kiblat", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
          Text(
            text = "${String.format("%,.1f", distanceKm)} KM",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            color = Emerald800
          )
        }

        Button(
          onClick = onBackToCompass,
          shape = RoundedCornerShape(12.dp),
          colors = ButtonDefaults.buttonColors(containerColor = Emerald700)
        ) {
          Text("Buka Kompas", fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
      }
    }
  }
}
