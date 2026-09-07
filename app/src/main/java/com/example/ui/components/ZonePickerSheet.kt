package com.example.ui.components

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MyLocation
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
import androidx.core.content.ContextCompat
import com.example.model.PrayerZone
import com.example.prayer.MalaysiaZones
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ZonePickerSheet(
  currentZone: PrayerZone,
  onZoneSelected: (PrayerZone) -> Unit,
  onDetectGpsLocation: (onSuccess: (PrayerZone) -> Unit, onFailure: () -> Unit) -> Unit,
  onDismiss: () -> Unit
) {
  val context = LocalContext.current
  val states = remember { MalaysiaZones.getStates() }
  var selectedState by remember { mutableStateOf(currentZone.state) }
  val zonesForState = remember(selectedState) { MalaysiaZones.getZonesForState(selectedState) }
  var isDetectingLocation by remember { mutableStateOf(false) }

  val locationPermissionLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.RequestMultiplePermissions()
  ) { permissions ->
    val fineGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
    val coarseGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
    if (fineGranted || coarseGranted) {
      isDetectingLocation = true
      onDetectGpsLocation(
        { zone ->
          isDetectingLocation = false
          Toast.makeText(context, "Zon dikesan: ${zone.code} - ${zone.name}", Toast.LENGTH_SHORT).show()
          onDismiss()
        },
        {
          isDetectingLocation = false
          Toast.makeText(context, "Tidak dapat mengesan lokasi GPS. Sila pilih zon secara manual.", Toast.LENGTH_SHORT).show()
        }
      )
    } else {
      Toast.makeText(context, "Kebenaran lokasi diperlukan untuk pengesanan automatik.", Toast.LENGTH_SHORT).show()
    }
  }

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
    shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
    containerColor = MaterialTheme.colorScheme.surface
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 20.dp, vertical = 8.dp)
        .navigationBarsPadding()
    ) {
      // Header
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "Pilih Zon Waktu Solat",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
          )
          Text(
            text = "Berdasarkan ketetapan JAKIM Malaysia",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }

        IconButton(onClick = onDismiss) {
          Icon(imageVector = Icons.Default.Close, contentDescription = "Tutup")
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Auto GPS Detect Button
      Button(
        onClick = {
          val hasFine = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
          val hasCoarse = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
          if (hasFine || hasCoarse) {
            isDetectingLocation = true
            onDetectGpsLocation(
              { zone ->
                isDetectingLocation = false
                Toast.makeText(context, "Zon dikesan: ${zone.code} - ${zone.name}", Toast.LENGTH_SHORT).show()
                onDismiss()
              },
              {
                isDetectingLocation = false
                Toast.makeText(context, "Gagal mendapatkan lokasi. Sila semak GPS anda.", Toast.LENGTH_SHORT).show()
              }
            )
          } else {
            locationPermissionLauncher.launch(
              arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
              )
            )
          }
        },
        colors = ButtonDefaults.buttonColors(
          containerColor = Emerald700,
          contentColor = Color.White
        ),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier
          .fillMaxWidth()
          .height(48.dp)
          .testTag("auto_gps_detect_button")
      ) {
        if (isDetectingLocation) {
          CircularProgressIndicator(
            color = Color.White,
            modifier = Modifier.size(20.dp),
            strokeWidth = 2.dp
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(text = "Mengesan lokasi semasa...", fontSize = 14.sp)
        } else {
          Icon(
            imageVector = Icons.Default.MyLocation,
            contentDescription = "GPS",
            modifier = Modifier.size(20.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "Kesan Lokasi Secara Automatik (GPS)",
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold
          )
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // States horizontal scroll chips
      Text(
        text = "Pilih Negeri:",
        fontSize = 13.sp,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )

      Spacer(modifier = Modifier.height(8.dp))

      LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        items(states) { state ->
          val isSelected = state == selectedState
          FilterChip(
            selected = isSelected,
            onClick = { selectedState = state },
            label = { Text(text = state, fontSize = 12.sp) },
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = Emerald100,
              selectedLabelColor = Emerald900
            ),
            shape = RoundedCornerShape(12.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Zones list for the selected state
      Text(
        text = "Zon bagi $selectedState:",
        fontSize = 13.sp,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )

      Spacer(modifier = Modifier.height(8.dp))

      LazyColumn(
        modifier = Modifier
          .fillMaxWidth()
          .heightIn(max = 320.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        items(zonesForState) { zone ->
          val isCurrent = zone.code == currentZone.code
          Surface(
            shape = RoundedCornerShape(12.dp),
            color = if (isCurrent) Emerald50 else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            border = if (isCurrent) androidx.compose.foundation.BorderStroke(1.5.dp, Emerald600) else null,
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(12.dp))
              .clickable {
                onZoneSelected(zone)
                onDismiss()
              }
              .testTag("zone_item_${zone.code.lowercase()}")
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = "${zone.code} - ${zone.name}",
                  fontSize = 14.sp,
                  fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Medium,
                  color = if (isCurrent) Emerald900 else MaterialTheme.colorScheme.onSurface
                )
                Text(
                  text = "Lat: ${String.format("%.2f", zone.latitude)}°, Lon: ${String.format("%.2f", zone.longitude)}°",
                  fontSize = 11.sp,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
              }

              if (isCurrent) {
                Box(
                  modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(Emerald600),
                  contentAlignment = Alignment.Center
                ) {
                  Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Dipilih",
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                  )
                }
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(16.dp))
    }
  }
}
