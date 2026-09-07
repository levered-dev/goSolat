package com.example.ui.components

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.prayer.QiblaCalculator
import com.example.sensor.CompassState
import com.example.ui.theme.*
import kotlin.math.*

@Composable
fun QiblaArView(
  compassState: CompassState,
  qiblaBearing: Double,
  distanceKm: Double,
  onCloseAr: () -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val lifecycleOwner = LocalLifecycleOwner.current

  var hasCameraPermission by remember {
    mutableStateOf(
      ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
    )
  }

  val permissionLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.RequestPermission()
  ) { granted ->
    hasCameraPermission = granted
  }

  LaunchedEffect(Unit) {
    if (!hasCameraPermission) {
      permissionLauncher.launch(Manifest.permission.CAMERA)
    }
  }

  // Calculate delta angle between phone azimuth and Qibla bearing
  val rawDelta = ((qiblaBearing - compassState.azimuth + 540) % 360) - 180
  val isCentered = abs(rawDelta) <= 3.5f

  // Haptic pulse when centered
  var wasCentered by remember { mutableStateOf(false) }
  LaunchedEffect(isCentered) {
    if (isCentered && !wasCentered) {
      triggerVibration(context)
    }
    wasCentered = isCentered
  }

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(Color.Black)
      .testTag("qibla_ar_view")
  ) {
    if (hasCameraPermission) {
      // Live Camera Feed
      AndroidView(
        factory = { ctx ->
          val previewView = PreviewView(ctx).apply {
            scaleType = PreviewView.ScaleType.FILL_CENTER
          }
          val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
          cameraProviderFuture.addListener({
            try {
              val cameraProvider = cameraProviderFuture.get()
              val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
              }
              val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
              cameraProvider.unbindAll()
              cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, preview)
            } catch (e: Exception) {
              e.printStackTrace()
            }
          }, ContextCompat.getMainExecutor(ctx))
          previewView
        },
        modifier = Modifier.fillMaxSize()
      )
    } else {
      // Camera permission placeholder
      Column(
        modifier = Modifier
          .fillMaxSize()
          .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
      ) {
        Icon(Icons.Default.CameraAlt, contentDescription = null, tint = Gold400, modifier = Modifier.size(64.dp))
        Spacer(modifier = Modifier.height(16.dp))
        Text(
          text = "Kebenaran Kamera Diperlukan",
          color = Color.White,
          fontSize = 18.sp,
          fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
          text = "Kamera diperlukan untuk memaparkan arah Kiblat secara Augmented Reality (AR) di skrin anda.",
          color = Color(0xFFCBD5E1),
          fontSize = 13.sp,
          textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(20.dp))
        Button(
          onClick = { permissionLauncher.launch(Manifest.permission.CAMERA) },
          colors = ButtonDefaults.buttonColors(containerColor = Emerald700)
        ) {
          Text("Benarkan Akses Kamera", fontWeight = FontWeight.Bold)
        }
      }
    }

    // Centered 3D Kaaba & Direction HUD
    Box(
      modifier = Modifier
        .fillMaxSize(),
      contentAlignment = Alignment.Center
    ) {
      CenteredKaabaArTarget(
        isCentered = isCentered,
        rawDelta = rawDelta,
        qiblaBearing = qiblaBearing,
        currentAzimuth = compassState.azimuth.toDouble()
      )
    }

    // Top HUD Bar
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .statusBarsPadding()
        .padding(16.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      IconButton(
        onClick = onCloseAr,
        modifier = Modifier
          .size(42.dp)
          .clip(CircleShape)
          .background(Color(0x990F172A))
          .testTag("close_ar_camera_button")
      ) {
        Icon(Icons.Default.Close, contentDescription = "Tutup AR", tint = Color.White)
      }

      Surface(
        shape = RoundedCornerShape(20.dp),
        color = Color(0x990F172A),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x44FFFFFF))
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(Icons.Default.Explore, contentDescription = null, tint = Gold400, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "Kiblat: ${String.format("%.1f", qiblaBearing)}° | Telefon: ${String.format("%.0f", compassState.azimuth)}°",
            color = Color.White,
            fontSize = 12.sp,
            fontFamily = FontFamily.Monospace
          )
        }
      }
    }

    // Bottom Guidance Card
    Column(
      modifier = Modifier
        .align(Alignment.BottomCenter)
        .navigationBarsPadding()
        .padding(16.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      // Rotation hint arrows if target is not centered
      if (!isCentered) {
        Surface(
          shape = RoundedCornerShape(16.dp),
          color = Color(0xCC0F172A),
          border = androidx.compose.foundation.BorderStroke(1.dp, Gold500)
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            if (rawDelta < 0) {
              Icon(Icons.Default.ArrowBack, contentDescription = "Pusing Kiri", tint = Gold400)
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "Pusing telefon ke kiri sebanyak ${abs(rawDelta).toInt()}°",
                color = Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
              )
            } else {
              Text(
                text = "Pusing telefon ke kanan sebanyak ${abs(rawDelta).toInt()}°",
                color = Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
              )
              Spacer(modifier = Modifier.width(8.dp))
              Icon(Icons.Default.ArrowForward, contentDescription = "Pusing Kanan", tint = Gold400)
            }
          }
        }
      } else {
        // Successfully aligned HUD
        Surface(
          shape = RoundedCornerShape(16.dp),
          color = Color(0xEE064E3B),
          border = androidx.compose.foundation.BorderStroke(2.dp, QiblaGreen)
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = QiblaGreen, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                text = "TEPAT KE ARAH KAABAH!",
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
              )
              Text(
                text = "Jarak ke Makkah: ${String.format("%,.0f", distanceKm)} km",
                color = Emerald100,
                fontSize = 11.sp
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      OutlinedButton(
        onClick = onCloseAr,
        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x66FFFFFF)),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.height(38.dp)
      ) {
        Icon(Icons.Default.ArrowBack, contentDescription = null, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text("Kembali ke Mod Kompas Biasa", fontSize = 12.sp)
      }
    }
  }
}

private fun triggerVibration(context: Context) {
  try {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
      val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
      vibratorManager?.defaultVibrator?.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE))
    } else {
      @Suppress("DEPRECATION")
      val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        vibrator?.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE))
      } else {
        @Suppress("DEPRECATION")
        vibrator?.vibrate(100)
      }
    }
  } catch (e: Exception) {
    // Ignore
  }
}

/**
 * Centered 3D Kaaba HUD with Direction Indicators ("berbentuk kaabah dan arah").
 * Placed in the exact center of the AR Camera view.
 */
@Composable
fun CenteredKaabaArTarget(
  isCentered: Boolean,
  rawDelta: Double,
  qiblaBearing: Double,
  currentAzimuth: Double,
  modifier: Modifier = Modifier
) {
  // Pulsing radiant aura when perfectly aligned
  val infiniteTransition = rememberInfiniteTransition(label = "kaaba_aura")
  val pulseRadius by infiniteTransition.animateFloat(
    initialValue = 60f,
    targetValue = 95f,
    animationSpec = infiniteRepeatable(
      animation = tween(1400, easing = EaseOutQuad),
      repeatMode = RepeatMode.Restart
    ),
    label = "pulse_radius"
  )
  val pulseAlpha by infiniteTransition.animateFloat(
    initialValue = 0.7f,
    targetValue = 0f,
    animationSpec = infiniteRepeatable(
      animation = tween(1400, easing = EaseOutQuad),
      repeatMode = RepeatMode.Restart
    ),
    label = "pulse_alpha"
  )

  // Arrow pulse when turning
  val arrowOffset by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = 8f,
    animationSpec = infiniteRepeatable(
      animation = tween(700, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "arrow_bounce"
  )

  Column(
    modifier = modifier,
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center
  ) {
    // Top Direction Prompt or Aligned Banner
    if (isCentered) {
      Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color(0xEE064E3B),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, QiblaGreen),
        shadowElevation = 6.dp,
        modifier = Modifier.padding(bottom = 8.dp)
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(
            imageVector = Icons.Default.Navigation,
            contentDescription = null,
            tint = QiblaGreen,
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "KIBLAT TEPAT",
            color = Color.White,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
          )
        }
      }
    } else {
      // Dynamic turn guidance indicator
      Row(
        modifier = Modifier.padding(bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        if (rawDelta < -3.5) {
          Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color(0xDD0F172A),
            border = androidx.compose.foundation.BorderStroke(1.dp, Gold400),
            modifier = Modifier.offset(x = (-arrowOffset).dp)
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(Icons.Default.ArrowBack, contentDescription = "Pusing Kiri", tint = Gold400, modifier = Modifier.size(14.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = "Pusing Kiri ${abs(rawDelta).toInt()}°",
                color = Color.White,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
              )
            }
          }
        } else if (rawDelta > 3.5) {
          Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color(0xDD0F172A),
            border = androidx.compose.foundation.BorderStroke(1.dp, Gold400),
            modifier = Modifier.offset(x = arrowOffset.dp)
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = "Pusing Kanan ${abs(rawDelta).toInt()}°",
                color = Color.White,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
              )
              Spacer(modifier = Modifier.width(4.dp))
              Icon(Icons.Default.ArrowForward, contentDescription = "Pusing Kanan", tint = Gold400, modifier = Modifier.size(14.dp))
            }
          }
        }
      }
    }

    // Main 3D Kaaba Canvas with Direction Indicators
    Box(
      contentAlignment = Alignment.Center,
      modifier = Modifier.size(220.dp)
    ) {
      Canvas(modifier = Modifier.fillMaxSize()) {
        val centerX = size.width / 2f
        val centerY = size.height / 2f

        // 1. Radiant Aura Pulse (when aligned or approaching)
        if (isCentered) {
          drawCircle(
            color = Color(0xFF00E676).copy(alpha = pulseAlpha),
            radius = pulseRadius * density,
            center = Offset(centerX, centerY)
          )
        }

        // 2. Outer Direction Reticle Ring
        val reticleRadius = 88.dp.toPx()
        drawCircle(
          color = if (isCentered) QiblaGreen else Color(0x66FFFFFF),
          radius = reticleRadius,
          center = Offset(centerX, centerY),
          style = Stroke(width = if (isCentered) 3.dp.toPx() else 1.5.dp.toPx())
        )

        // 3. Compass Tick Marks along ring
        for (angle in 0 until 360 step 30) {
          val rad = Math.toRadians(angle.toDouble())
          val isCardinal = angle % 90 == 0
          val tickLen = if (isCardinal) 8.dp.toPx() else 4.dp.toPx()
          val startX = centerX + (reticleRadius - tickLen) * sin(rad).toFloat()
          val startY = centerY - (reticleRadius - tickLen) * cos(rad).toFloat()
          val endX = centerX + reticleRadius * sin(rad).toFloat()
          val endY = centerY - reticleRadius * cos(rad).toFloat()

          drawLine(
            color = if (isCentered) QiblaGreen.copy(alpha = 0.8f) else Color(0x88FFFFFF),
            start = Offset(startX, startY),
            end = Offset(endX, endY),
            strokeWidth = if (isCardinal) 2.dp.toPx() else 1.dp.toPx()
          )
        }

        // 4. Direction Arrow to Qibla on the perimeter
        // Pointer angle relative to current phone orientation
        val relativePointerAngle = (rawDelta).toFloat()
        rotate(degrees = relativePointerAngle, pivot = Offset(centerX, centerY)) {
          // Arrow pointing from perimeter
          val arrowPath = Path().apply {
            val arrowY = centerY - reticleRadius
            moveTo(centerX, arrowY - 14.dp.toPx())
            lineTo(centerX - 8.dp.toPx(), arrowY + 2.dp.toPx())
            lineTo(centerX + 8.dp.toPx(), arrowY + 2.dp.toPx())
            close()
          }
          drawPath(
            path = arrowPath,
            color = if (isCentered) QiblaGreen else Gold400,
            style = Fill
          )
        }

        // 5. Reticle Alignment Crosshair Ticks
        val tickGap = 52.dp.toPx()
        val tickExt = 68.dp.toPx()
        val reticleColor = if (isCentered) QiblaGreen else Color(0x99FFFFFF)
        val tickStroke = if (isCentered) 3.dp.toPx() else 1.5.dp.toPx()

        drawLine(reticleColor, Offset(centerX - tickExt, centerY), Offset(centerX - tickGap, centerY), strokeWidth = tickStroke)
        drawLine(reticleColor, Offset(centerX + tickGap, centerY), Offset(centerX + tickExt, centerY), strokeWidth = tickStroke)
        drawLine(reticleColor, Offset(centerX, centerY - tickExt), Offset(centerX, centerY - tickGap), strokeWidth = tickStroke)
        drawLine(reticleColor, Offset(centerX, centerY + tickGap), Offset(centerX, centerY + tickExt), strokeWidth = tickStroke)

        // 6. Draw 3D Isometric Kaaba Cube in Center
        draw3DKaaba(centerX, centerY)
      }
    }

    // Bottom Badge: Kaaba Bearing & Coordinates
    Surface(
      shape = RoundedCornerShape(14.dp),
      color = if (isCentered) Color(0xDD064E3B) else Color(0xCC0F172A),
      border = androidx.compose.foundation.BorderStroke(1.dp, if (isCentered) QiblaGreen else Gold400),
      modifier = Modifier.padding(top = 8.dp)
    ) {
      Row(
        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "KAABAH",
          color = if (isCentered) QiblaGreen else Gold400,
          fontSize = 12.sp,
          fontWeight = FontWeight.Bold
        )
        Text(
          text = "  •  ${String.format("%.1f", qiblaBearing)}°",
          color = Color.White,
          fontSize = 12.sp,
          fontFamily = FontFamily.Monospace,
          fontWeight = FontWeight.SemiBold
        )
      }
    }
  }
}

/**
 * Draws an isometric 3D Kaaba structure with Kiswah golden band,
 * Bab al-Kaaba (golden door), and marble base.
 */
private fun androidx.compose.ui.graphics.drawscope.DrawScope.draw3DKaaba(
  centerX: Float,
  centerY: Float
) {
  val cubeHalfWidth = 36.dp.toPx()
  val cubeHeight = 46.dp.toPx()
  val isoYOffset = 18.dp.toPx()

  // Base Y position
  val baseY = centerY + cubeHeight / 2.5f

  // --- 1. Base Shadow ---
  drawOval(
    color = Color(0x66000000),
    topLeft = Offset(centerX - cubeHalfWidth * 1.2f, baseY - 4.dp.toPx()),
    size = Size(cubeHalfWidth * 2.4f, 14.dp.toPx())
  )

  // --- 2. Left / Front Facade of Kaaba (Deep Obsidian Black) ---
  val frontPath = Path().apply {
    moveTo(centerX, baseY)
    lineTo(centerX - cubeHalfWidth, baseY - isoYOffset)
    lineTo(centerX - cubeHalfWidth, baseY - isoYOffset - cubeHeight)
    lineTo(centerX, baseY - cubeHeight)
    close()
  }
  drawPath(frontPath, Color(0xFF0F0F0F))

  // --- 3. Right / Side Facade of Kaaba (Shadow Black) ---
  val rightPath = Path().apply {
    moveTo(centerX, baseY)
    lineTo(centerX + cubeHalfWidth, baseY - isoYOffset)
    lineTo(centerX + cubeHalfWidth, baseY - isoYOffset - cubeHeight)
    lineTo(centerX, baseY - cubeHeight)
    close()
  }
  drawPath(rightPath, Color(0xFF1C1C1C))

  // --- 4. Top Roof Face of Kaaba (Charcoal with perspective) ---
  val roofPath = Path().apply {
    moveTo(centerX, baseY - cubeHeight)
    lineTo(centerX - cubeHalfWidth, baseY - isoYOffset - cubeHeight)
    lineTo(centerX, baseY - isoYOffset * 2 - cubeHeight)
    lineTo(centerX + cubeHalfWidth, baseY - isoYOffset - cubeHeight)
    close()
  }
  drawPath(roofPath, Color(0xFF2B2B2B))
  drawPath(roofPath, Color(0xFFD4AF37), style = Stroke(width = 1.dp.toPx()))

  // --- 5. Kiswah Golden Band (Hizam) ---
  // Band on Front Face
  val kiswahFront = Path().apply {
    val bandTop = baseY - cubeHeight + 10.dp.toPx()
    val bandHeight = 7.dp.toPx()
    moveTo(centerX, bandTop)
    lineTo(centerX - cubeHalfWidth, bandTop - isoYOffset)
    lineTo(centerX - cubeHalfWidth, bandTop - isoYOffset + bandHeight)
    lineTo(centerX, bandTop + bandHeight)
    close()
  }
  drawPath(kiswahFront, Color(0xFFFFD700))

  // Band on Right Face
  val kiswahRight = Path().apply {
    val bandTop = baseY - cubeHeight + 10.dp.toPx()
    val bandHeight = 7.dp.toPx()
    moveTo(centerX, bandTop)
    lineTo(centerX + cubeHalfWidth, bandTop - isoYOffset)
    lineTo(centerX + cubeHalfWidth, bandTop - isoYOffset + bandHeight)
    lineTo(centerX, bandTop + bandHeight)
    close()
  }
  drawPath(kiswahRight, Color(0xFFFFC107))

  // --- 6. Bab al-Kaaba (Golden Door on Front Face) ---
  val doorPath = Path().apply {
    val doorLeft = centerX - cubeHalfWidth * 0.55f
    val doorRight = centerX - cubeHalfWidth * 0.15f
    val doorBottom = baseY - isoYOffset * 0.35f
    val doorTop = doorBottom - 20.dp.toPx()

    moveTo(doorRight, doorBottom)
    lineTo(doorLeft, doorBottom - isoYOffset * 0.4f)
    lineTo(doorLeft, doorTop - isoYOffset * 0.4f)
    lineTo(doorRight, doorTop)
    close()
  }
  drawPath(doorPath, Color(0xFFF59E0B))
  drawPath(doorPath, Color(0xFFFFFBEB), style = Stroke(width = 1.dp.toPx()))

  // --- 7. Hajar al-Aswad Silver Corner Casing ---
  drawCircle(
    color = Color(0xFFE2E8F0),
    radius = 3.dp.toPx(),
    center = Offset(centerX, baseY - 2.dp.toPx())
  )
  drawCircle(
    color = Color(0xFF1E293B),
    radius = 1.5.dp.toPx(),
    center = Offset(centerX, baseY - 2.dp.toPx())
  )

  // --- 8. Mizab ar-Rahmah (Golden Spout on Top Rim) ---
  drawLine(
    color = Color(0xFFFFD700),
    start = Offset(centerX - cubeHalfWidth * 0.5f, baseY - isoYOffset * 1.5f - cubeHeight),
    end = Offset(centerX - cubeHalfWidth * 0.65f, baseY - isoYOffset * 1.6f - cubeHeight - 3.dp.toPx()),
    strokeWidth = 2.dp.toPx()
  )

  // --- 9. Shadherwan (White Marble Base) ---
  val marbleBase = Path().apply {
    moveTo(centerX, baseY + 3.dp.toPx())
    lineTo(centerX - cubeHalfWidth * 1.05f, baseY - isoYOffset + 3.dp.toPx())
    lineTo(centerX, baseY - isoYOffset * 2 + 3.dp.toPx())
    lineTo(centerX + cubeHalfWidth * 1.05f, baseY - isoYOffset + 3.dp.toPx())
    close()
  }
  drawPath(marbleBase, Color(0x55FFFFFF), style = Stroke(width = 1.5.dp.toPx()))
}
