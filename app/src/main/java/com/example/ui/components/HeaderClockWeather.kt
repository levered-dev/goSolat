package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.PrayerZone
import com.example.model.WeatherData
import com.example.ui.theme.*

@Composable
fun HeaderClockWeather(
  timeStr: String,
  dateStr: String,
  hijriDateStr: String,
  zone: PrayerZone,
  weather: WeatherData,
  isWeatherLoading: Boolean,
  onZoneClick: () -> Unit,
  onRefreshWeather: () -> Unit,
  modifier: Modifier = Modifier
) {
  val infiniteTransition = rememberInfiniteTransition(label = "weather_spin")
  val rotation by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = 360f,
    animationSpec = infiniteRepeatable(
      animation = tween(1000, easing = LinearEasing),
      repeatMode = RepeatMode.Restart
    ),
    label = "spin"
  )

  Card(
    shape = RoundedCornerShape(28.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
    modifier = modifier
      .fillMaxWidth()
      .testTag("header_clock_weather")
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(20.dp)
    ) {
      // Gemini Brand Bar
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Image(
            painter = painterResource(id = R.drawable.img_app_icon),
            contentDescription = "Ikon Aplikasi goSolat",
            contentScale = ContentScale.Crop,
            modifier = Modifier
              .size(28.dp)
              .clip(RoundedCornerShape(8.dp))
              .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(8.dp))
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "goSolat",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
          )
        }

        Surface(
          shape = RoundedCornerShape(12.dp),
          color = MaterialTheme.colorScheme.surfaceVariant
        ) {
          Text(
            text = "Pintar & Tepat",
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium,
            color = GeminiBlue,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Top row: Location & Clock on left, Weather card on right
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
      ) {
        Column(modifier = Modifier.weight(1f)) {
          // Location with pin icon in GeminiBlue
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
              .clip(RoundedCornerShape(8.dp))
              .clickable { onZoneClick() }
              .testTag("zone_picker_button")
              .padding(vertical = 2.dp)
          ) {
            Icon(
              imageVector = Icons.Filled.LocationOn,
              contentDescription = "Zon Solat",
              tint = GeminiBlue,
              modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "${zone.name}, MY",
              fontSize = 13.sp,
              fontWeight = FontWeight.Medium,
              letterSpacing = (-0.2).sp,
              color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.width(2.dp))
            Icon(
              imageVector = Icons.Default.ArrowDropDown,
              contentDescription = "Pilih Kawasan",
              tint = MaterialTheme.colorScheme.onSurfaceVariant,
              modifier = Modifier.size(16.dp)
            )
          }

          Spacer(modifier = Modifier.height(4.dp))

          // Large clean time display (Light tracking)
          Text(
            text = timeStr.ifEmpty { "--:--" },
            fontSize = 40.sp,
            fontWeight = FontWeight.Light,
            letterSpacing = (-1).sp,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.testTag("header_digital_clock")
          )

          Spacer(modifier = Modifier.height(2.dp))

          // Date line: Hijri & Gregorian
          Text(
            text = "$hijriDateStr • $dateStr",
            fontSize = 12.sp,
            fontWeight = FontWeight.Normal,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Weather card in GeminiLightPrimaryContainer
        Surface(
          shape = RoundedCornerShape(20.dp),
          color = MaterialTheme.colorScheme.primaryContainer,
          modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .clickable { onRefreshWeather() }
            .testTag("refresh_weather_button")
        ) {
          Column(
            modifier = Modifier
              .width(76.dp)
              .padding(vertical = 12.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
          ) {
            val weatherIcon = when {
              weather.weatherCode in 95..99 -> Icons.Default.Thunderstorm
              weather.weatherCode in 51..82 -> Icons.Default.WaterDrop
              weather.weatherCode in 1..3 -> Icons.Default.Cloud
              else -> Icons.Default.WbSunny
            }

            Icon(
              imageVector = weatherIcon,
              contentDescription = "Cuaca Semasa",
              tint = MaterialTheme.colorScheme.onPrimaryContainer,
              modifier = Modifier
                .size(28.dp)
                .rotate(if (isWeatherLoading) rotation else 0f)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
              text = "${String.format("%.0f", weather.temperatureC)}°C",
              fontSize = 13.sp,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onPrimaryContainer
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Bottom subtle weather details strip
      Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = weather.conditionMs,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )

          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.WaterDrop,
                contentDescription = null,
                tint = SleekPrimary,
                modifier = Modifier.size(12.dp)
              )
              Spacer(modifier = Modifier.width(3.dp))
              Text(
                text = "${weather.humidityPercent}%",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.Air,
                contentDescription = null,
                tint = SleekPrimary,
                modifier = Modifier.size(12.dp)
              )
              Spacer(modifier = Modifier.width(3.dp))
              Text(
                text = "${String.format("%.0f", weather.windSpeedKmh)} km/j",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }
        }
      }
    }
  }
}

