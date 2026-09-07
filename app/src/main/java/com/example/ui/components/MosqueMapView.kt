package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.MosqueRepository
import com.example.model.Mosque
import com.example.model.PrayerZone
import com.example.ui.theme.*
import java.util.Locale
import kotlin.math.*

/**
 * Screen presenting a concise summary of mosques and surau relative to the user's real location,
 * directing users to external map apps (Google Maps, Waze) rather than embedding a map.
 */
@Composable
fun MosqueMapView(
  zone: PrayerZone,
  userLatitude: Double = zone.latitude,
  userLongitude: Double = zone.longitude,
  isRealLocationActive: Boolean = false,
  onRequestTurnOnLocation: () -> Unit = {},
  onOpenZonePicker: () -> Unit = {},
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  var searchQuery by remember { mutableStateOf("") }
  var selectedFilter by remember { mutableStateOf("Semua") }

  // Computed nearby mosques based on active user coordinates (real GPS or zone center)
  val allNearbyMosques = remember(userLatitude, userLongitude) {
    MosqueRepository.getNearbyMosques(userLatitude, userLongitude)
  }

  // Filtered mosques
  val filteredMosques = remember(allNearbyMosques, searchQuery, selectedFilter) {
    allNearbyMosques.filter { mosque ->
      val matchesSearch = searchQuery.isBlank() ||
        mosque.name.contains(searchQuery, ignoreCase = true) ||
        mosque.district.contains(searchQuery, ignoreCase = true) ||
        mosque.state.contains(searchQuery, ignoreCase = true) ||
        mosque.address.contains(searchQuery, ignoreCase = true)

      val matchesFilter = when (selectedFilter) {
        "Masjid Sahaja" -> mosque.type.equals("Masjid", ignoreCase = true)
        "Surau Sahaja" -> mosque.type.equals("Surau", ignoreCase = true)
        "Dekat (< 3km)" -> mosque.distanceKm < 3.0
        "Bawah 10km" -> mosque.distanceKm < 10.0
        "Solat Jumaat" -> mosque.hasFridayPrayer
        "Mesra OKU" -> mosque.facilities.any { it.contains("OKU", ignoreCase = true) }
        "Parkir Luas" -> mosque.facilities.any { it.contains("Parkir", ignoreCase = true) }
        else -> true
      }

      matchesSearch && matchesFilter
    }
  }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
  ) {
    // 1. Header Bar
    Surface(
      color = MaterialTheme.colorScheme.surface,
      tonalElevation = 2.dp,
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 12.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(GeminiLightPrimaryContainer),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.Mosque,
                contentDescription = "Masjid & Surau",
                tint = GeminiBlue,
                modifier = Modifier.size(22.dp)
              )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                text = "Masjid & Surau Berhampiran",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
              )
              Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { onOpenZonePicker() }
              ) {
                Icon(
                  imageVector = Icons.Default.LocationOn,
                  contentDescription = "Zon",
                  tint = GeminiBlue,
                  modifier = Modifier.size(13.dp)
                )
                Spacer(modifier = Modifier.width(3.dp))
                Text(
                  text = "${zone.name} (${zone.code})",
                  fontSize = 12.sp,
                  color = GeminiBlue,
                  fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = "• Tukar",
                  fontSize = 11.sp,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
            }
          }

          // External Map Quick Button
          FilledTonalButton(
            onClick = { openGoogleMapsSearch(context, userLatitude, userLongitude) },
            shape = RoundedCornerShape(14.dp),
            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
            modifier = Modifier.testTag("quick_open_maps_header")
          ) {
            Icon(Icons.Default.Explore, contentDescription = null, modifier = Modifier.size(15.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Peta Luar", fontSize = 11.sp, fontWeight = FontWeight.Bold)
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Search Bar
        OutlinedTextField(
          value = searchQuery,
          onValueChange = { searchQuery = it },
          placeholder = {
            Text(
              "Cari nama masjid, surau, daerah...",
              fontSize = 13.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          },
          leadingIcon = {
            Icon(
              imageVector = Icons.Default.Search,
              contentDescription = "Cari",
              tint = GeminiBlue,
              modifier = Modifier.size(18.dp)
            )
          },
          trailingIcon = {
            if (searchQuery.isNotEmpty()) {
              IconButton(onClick = { searchQuery = "" }) {
                Icon(
                  imageVector = Icons.Default.Close,
                  contentDescription = "Padam carian",
                  modifier = Modifier.size(16.dp)
                )
              }
            }
          },
          singleLine = true,
          shape = RoundedCornerShape(24.dp),
          colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            focusedBorderColor = GeminiBlue,
            unfocusedBorderColor = Color.Transparent
          ),
          modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .testTag("search_mosque_input")
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Filter Chips Row
        val filters = listOf(
          "Semua",
          "Masjid Sahaja",
          "Surau Sahaja",
          "Dekat (< 3km)",
          "Bawah 10km",
          "Solat Jumaat",
          "Mesra OKU",
          "Parkir Luas"
        )
        LazyRow(
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          items(filters) { filter ->
            val isSelected = selectedFilter == filter
            Surface(
              shape = RoundedCornerShape(16.dp),
              color = if (isSelected) GeminiLightPrimaryContainer else MaterialTheme.colorScheme.surfaceVariant,
              border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, GeminiBlue) else null,
              onClick = { selectedFilter = filter },
              modifier = Modifier.testTag("filter_chip_$filter")
            ) {
              Text(
                text = filter,
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) GeminiBlue else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
              )
            }
          }
        }
      }
    }

    // 2. Main Scrollable Content
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 16.dp, vertical = 12.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      // Real Location (Lokasi Sebenar) Notice / Activation Card
      item {
        LocationStatusCard(
          isRealLocationActive = isRealLocationActive,
          latitude = userLatitude,
          longitude = userLongitude,
          zoneName = zone.name,
          onTurnOnLocation = onRequestTurnOnLocation
        )
      }

      // Direct to External Map Hero Banner Card
      item {
        ExternalMapHeroCard(
          userLat = userLatitude,
          userLon = userLongitude
        )
      }

      // Summary Header Row
      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "${filteredMosques.size} masjid & surau dijumpai",
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )

          Text(
            text = if (isRealLocationActive) "Dari Lokasi Sebenar" else "Dari Pusat Zon",
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = if (isRealLocationActive) Emerald700 else Gold800
          )
        }
      }

      // Concise Mosque & Surau List
      if (filteredMosques.isEmpty()) {
        item {
          Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(
              modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              Icon(
                imageVector = Icons.Default.SearchOff,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(36.dp)
              )
              Spacer(modifier = Modifier.height(8.dp))
              Text(
                text = "Tiada masjid atau surau sepadan carian",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
              )
              Spacer(modifier = Modifier.height(8.dp))
              OutlinedButton(
                onClick = {
                  searchQuery = ""
                  selectedFilter = "Semua"
                }
              ) {
                Text("Set Semula Tapisan", fontSize = 12.sp)
              }
            }
          }
        }
      } else {
        items(filteredMosques, key = { it.id }) { mosque ->
          ConciseMosqueCard(
            mosque = mosque,
            userLat = userLatitude,
            userLon = userLongitude,
            isRealLocation = isRealLocationActive
          )
        }
      }

      item {
        Spacer(modifier = Modifier.height(28.dp))
      }
    }
  }
}

/**
 * Interactive card displaying whether real GPS location is active,
 * allowing user to turn on real location to get accurate distances & directions.
 */
@Composable
fun LocationStatusCard(
  isRealLocationActive: Boolean,
  latitude: Double,
  longitude: Double,
  zoneName: String,
  onTurnOnLocation: () -> Unit,
  modifier: Modifier = Modifier
) {
  if (isRealLocationActive) {
    // Real location is active
    Card(
      shape = RoundedCornerShape(16.dp),
      colors = CardDefaults.cardColors(containerColor = Emerald50),
      border = androidx.compose.foundation.BorderStroke(1.dp, Emerald600.copy(alpha = 0.4f)),
      modifier = modifier
        .fillMaxWidth()
        .testTag("location_status_card_active")
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Row(
          modifier = Modifier.weight(1f),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Box(
            modifier = Modifier
              .size(36.dp)
              .clip(CircleShape)
              .background(Emerald600),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.GpsFixed,
              contentDescription = "GPS Aktif",
              tint = Color.White,
              modifier = Modifier.size(18.dp)
            )
          }
          Spacer(modifier = Modifier.width(10.dp))
          Column {
            Text(
              text = "Lokasi Sebenar Aktif (GPS)",
              fontSize = 13.sp,
              fontWeight = FontWeight.Bold,
              color = Emerald900
            )
            Text(
              text = "Jarak & arah dikira terus dari koordinat sebenar anda (${String.format(Locale.US, "%.3f, %.3f", latitude, longitude)})",
              fontSize = 11.sp,
              color = Emerald800,
              lineHeight = 14.sp
            )
          }
        }

        IconButton(
          onClick = onTurnOnLocation,
          modifier = Modifier.size(36.dp)
        ) {
          Icon(
            imageVector = Icons.Default.Refresh,
            contentDescription = "Kemaskini Lokasi",
            tint = Emerald800,
            modifier = Modifier.size(20.dp)
          )
        }
      }
    }
  } else {
    // Real location is NOT active — prompt user to turn on real location
    Card(
      shape = RoundedCornerShape(16.dp),
      colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFBEB)), // Warm amber
      border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFDE68A)),
      modifier = modifier
        .fillMaxWidth()
        .testTag("location_status_card_inactive")
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(14.dp)
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(34.dp)
              .clip(CircleShape)
              .background(Color(0xFFF59E0B)),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.LocationOff,
              contentDescription = "Lokasi belum aktif",
              tint = Color.White,
              modifier = Modifier.size(18.dp)
            )
          }
          Spacer(modifier = Modifier.width(10.dp))
          Column {
            Text(
              text = "Turn On Lokasi Sebenar",
              fontSize = 14.sp,
              fontWeight = FontWeight.Bold,
              color = Color(0xFF92400E)
            )
            Text(
              text = "Ketahui kedudukan & jarak tepat dari posisi anda",
              fontSize = 11.sp,
              color = Color(0xFFB45309)
            )
          }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
          text = "Kedudukan masjid/surau kini dianggarkan berdasarkan zon $zoneName. Sila turn on lokasi sebenar untuk mengetahui jarak dan arah yang tepat dari lokasi semasa anda.",
          fontSize = 12.sp,
          color = Color(0xFF78350F),
          lineHeight = 16.sp
        )

        Spacer(modifier = Modifier.height(10.dp))

        Button(
          onClick = onTurnOnLocation,
          shape = RoundedCornerShape(12.dp),
          colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD97706)),
          contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("turn_on_real_location_button")
        ) {
          Icon(Icons.Default.MyLocation, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("Turn On Lokasi Sebenar Sekarang", fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
      }
    }
  }
}

/**
 * Prominent card directing users to external map applications (Google Maps & Waze).
 */
@Composable
fun ExternalMapHeroCard(
  userLat: Double,
  userLon: Double,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current

  Card(
    shape = RoundedCornerShape(18.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    border = androidx.compose.foundation.BorderStroke(1.dp, GeminiBlue.copy(alpha = 0.35f)),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    modifier = modifier
      .fillMaxWidth()
      .testTag("external_map_hero_card")
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp)
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
          modifier = Modifier
            .size(34.dp)
            .clip(CircleShape)
            .background(GeminiBlue),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.Navigation,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(18.dp)
          )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column {
          Text(
            text = "Cari di Aplikasi Peta Luar",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
          )
          Text(
            text = "Buka Google Maps atau Waze untuk melihat peta penuh & trafik",
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        // Google Maps Launcher
        Button(
          onClick = { openGoogleMapsSearch(context, userLat, userLon) },
          shape = RoundedCornerShape(12.dp),
          colors = ButtonDefaults.buttonColors(containerColor = GeminiBlue),
          contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
          modifier = Modifier
            .weight(1f)
            .testTag("open_google_maps_button")
        ) {
          Icon(Icons.Default.Explore, contentDescription = null, modifier = Modifier.size(15.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("Google Maps", fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }

        // Waze Launcher
        OutlinedButton(
          onClick = { openWazeSearch(context, userLat, userLon) },
          shape = RoundedCornerShape(12.dp),
          contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
          modifier = Modifier
            .weight(1f)
            .testTag("open_waze_button")
        ) {
          Icon(Icons.Default.DirectionsCar, contentDescription = null, modifier = Modifier.size(15.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("Waze", fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
      }
    }
  }
}

/**
 * Concise Card displaying a mosque or surau with its relative distance and direction,
 * and direct one-click actions to open navigation in external map apps.
 */
@Composable
fun ConciseMosqueCard(
  mosque: Mosque,
  userLat: Double,
  userLon: Double,
  isRealLocation: Boolean,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val isSurau = mosque.type.equals("Surau", ignoreCase = true)

  // Calculate relative bearing and cardinal direction from user to mosque
  val bearing = remember(userLat, userLon, mosque.latitude, mosque.longitude) {
    calculateBearing(userLat, userLon, mosque.latitude, mosque.longitude)
  }
  val cardinalDir = remember(bearing) {
    getCardinalDirection(bearing)
  }

  Card(
    shape = RoundedCornerShape(18.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    modifier = modifier
      .fillMaxWidth()
      .testTag("mosque_card_${mosque.id}")
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp)
    ) {
      // Header: Name & Distance Badge
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = mosque.name,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
          )
          if (mosque.arabicName.isNotBlank()) {
            Text(
              text = mosque.arabicName,
              fontSize = 12.sp,
              color = GeminiPurple
            )
          }
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Concise Distance Pill
        Surface(
          shape = RoundedCornerShape(12.dp),
          color = if (mosque.distanceKm < 3.0) Emerald100 else GeminiLightPrimaryContainer
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              imageVector = Icons.Default.NearMe,
              contentDescription = null,
              tint = if (mosque.distanceKm < 3.0) Emerald900 else GeminiLightOnPrimaryContainer,
              modifier = Modifier.size(11.dp)
            )
            Spacer(modifier = Modifier.width(3.dp))
            Text(
              text = "${mosque.distanceKm} km",
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              color = if (mosque.distanceKm < 3.0) Emerald900 else GeminiLightOnPrimaryContainer
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(4.dp))

      // Relative Direction / Bearing from current user position
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        Surface(
          shape = RoundedCornerShape(6.dp),
          color = MaterialTheme.colorScheme.surfaceVariant
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              imageVector = Icons.Default.Explore,
              contentDescription = null,
              tint = GeminiBlue,
              modifier = Modifier.size(11.dp)
            )
            Spacer(modifier = Modifier.width(3.dp))
            Text(
              text = "Arah: $cardinalDir (${bearing.roundToInt()}°)",
              fontSize = 11.sp,
              fontWeight = FontWeight.SemiBold,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }

        Text(
          text = if (isRealLocation) "• dari lokasi anda" else "• dari pusat zon",
          fontSize = 10.sp,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }

      Spacer(modifier = Modifier.height(6.dp))

      // Address ringkas
      Text(
        text = mosque.address,
        fontSize = 12.sp,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
      )

      Spacer(modifier = Modifier.height(8.dp))

      // Badges: Type + Facilities
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Type Badge (Masjid vs Surau)
        Surface(
          shape = RoundedCornerShape(6.dp),
          color = if (isSurau) Color(0xFFFEF3C7) else Color(0xFFE6F4EA)
        ) {
          Text(
            text = if (isSurau) "Surau" else "Masjid",
            fontSize = 10.sp,
            color = if (isSurau) Color(0xFFB45309) else Color(0xFF137333),
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
          )
        }

        if (mosque.hasFridayPrayer) {
          Surface(
            shape = RoundedCornerShape(6.dp),
            color = Color(0xFFE0F2FE)
          ) {
            Text(
              text = "Solat Jumaat",
              fontSize = 10.sp,
              color = Color(0xFF0284C7),
              fontWeight = FontWeight.SemiBold,
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
          }
        }

        mosque.facilities.take(2).forEach { facility ->
          Surface(
            shape = RoundedCornerShape(6.dp),
            color = MaterialTheme.colorScheme.surfaceVariant
          ) {
            Text(
              text = facility,
              fontSize = 10.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Direct Action Buttons to External Maps & Sharing
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Google Maps Navigation
        Button(
          onClick = { openMosqueInGoogleMaps(context, mosque) },
          shape = RoundedCornerShape(12.dp),
          colors = ButtonDefaults.buttonColors(containerColor = GeminiBlue),
          contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
          modifier = Modifier
            .weight(1f)
            .height(34.dp)
            .testTag("nav_google_maps_${mosque.id}")
        ) {
          Icon(Icons.Default.Directions, contentDescription = null, modifier = Modifier.size(13.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("Google Maps", fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }

        // Waze Navigation
        OutlinedButton(
          onClick = { openMosqueInWaze(context, mosque) },
          shape = RoundedCornerShape(12.dp),
          contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
          modifier = Modifier
            .weight(0.7f)
            .height(34.dp)
            .testTag("nav_waze_${mosque.id}")
        ) {
          Icon(Icons.Default.NearMe, contentDescription = null, modifier = Modifier.size(13.dp))
          Spacer(modifier = Modifier.width(3.dp))
          Text("Waze", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
        }

        // Share button
        IconButton(
          onClick = { shareMosqueDetails(context, mosque) },
          modifier = Modifier
            .size(34.dp)
            .testTag("share_mosque_${mosque.id}")
        ) {
          Icon(
            imageVector = Icons.Default.Share,
            contentDescription = "Kongsi",
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(16.dp)
          )
        }
      }
    }
  }
}

// --- Helper Functions for External Maps and Calculations ---

fun openGoogleMapsSearch(context: Context, lat: Double, lon: Double) {
  val geoUri = Uri.parse("geo:$lat,$lon?q=masjid+dan+surau")
  val mapIntent = Intent(Intent.ACTION_VIEW, geoUri).apply {
    setPackage("com.google.android.apps.maps")
  }
  try {
    context.startActivity(mapIntent)
  } catch (e: Exception) {
    val webUri = Uri.parse("https://www.google.com/maps/search/masjid+dan+surau/@$lat,$lon,14z")
    try {
      context.startActivity(Intent(Intent.ACTION_VIEW, webUri))
    } catch (e2: Exception) {
      Toast.makeText(context, "Tidak dapat membuka aplikasi peta", Toast.LENGTH_SHORT).show()
    }
  }
}

fun openWazeSearch(context: Context, lat: Double, lon: Double) {
  val wazeUri = Uri.parse("waze://?q=masjid&navigate=yes")
  val intent = Intent(Intent.ACTION_VIEW, wazeUri).apply {
    setPackage("com.waze")
  }
  try {
    context.startActivity(intent)
  } catch (e: Exception) {
    val webUri = Uri.parse("https://waze.com/ul?q=masjid&navigate=yes")
    try {
      context.startActivity(Intent(Intent.ACTION_VIEW, webUri))
    } catch (e2: Exception) {
      Toast.makeText(context, "Aplikasi Waze tidak dijumpai", Toast.LENGTH_SHORT).show()
    }
  }
}

fun openMosqueInGoogleMaps(context: Context, mosque: Mosque) {
  val geoUri = Uri.parse("geo:${mosque.latitude},${mosque.longitude}?q=${Uri.encode(mosque.name)}")
  val mapIntent = Intent(Intent.ACTION_VIEW, geoUri).apply {
    setPackage("com.google.android.apps.maps")
  }
  try {
    context.startActivity(mapIntent)
  } catch (e: Exception) {
    val fallback = Uri.parse("https://www.google.com/maps/search/?api=1&query=${mosque.latitude},${mosque.longitude}")
    try {
      context.startActivity(Intent(Intent.ACTION_VIEW, fallback))
    } catch (e2: Exception) {
      Toast.makeText(context, "Tidak dapat membuka Google Maps", Toast.LENGTH_SHORT).show()
    }
  }
}

fun openMosqueInWaze(context: Context, mosque: Mosque) {
  val uri = Uri.parse("waze://?ll=${mosque.latitude},${mosque.longitude}&navigate=yes")
  val intent = Intent(Intent.ACTION_VIEW, uri).apply {
    setPackage("com.waze")
  }
  try {
    context.startActivity(intent)
  } catch (e: Exception) {
    val fallback = Uri.parse("https://waze.com/ul?ll=${mosque.latitude},${mosque.longitude}&navigate=yes")
    try {
      context.startActivity(Intent(Intent.ACTION_VIEW, fallback))
    } catch (e2: Exception) {
      Toast.makeText(context, "Aplikasi Waze tidak dijumpai", Toast.LENGTH_SHORT).show()
    }
  }
}

fun shareMosqueDetails(context: Context, mosque: Mosque) {
  val text = "${mosque.name} (${mosque.type})\nAlamat: ${mosque.address}\nPeta: https://www.google.com/maps/search/?api=1&query=${mosque.latitude},${mosque.longitude}"
  val intent = Intent(Intent.ACTION_SEND).apply {
    type = "text/plain"
    putExtra(Intent.EXTRA_SUBJECT, mosque.name)
    putExtra(Intent.EXTRA_TEXT, text)
  }
  try {
    context.startActivity(Intent.createChooser(intent, "Kongsi ${mosque.name}"))
  } catch (e: Exception) {
    // Ignore
  }
}

/**
 * Calculates initial bearing in degrees from (lat1, lon1) to (lat2, lon2).
 */
fun calculateBearing(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
  val phi1 = Math.toRadians(lat1)
  val phi2 = Math.toRadians(lat2)
  val deltaLambda = Math.toRadians(lon2 - lon1)
  val y = sin(deltaLambda) * cos(phi2)
  val x = cos(phi1) * sin(phi2) - sin(phi1) * cos(phi2) * cos(deltaLambda)
  val theta = atan2(y, x)
  return (Math.toDegrees(theta) + 360) % 360
}

/**
 * Converts degrees bearing to Malay 8-point compass cardinal direction.
 */
fun getCardinalDirection(bearing: Double): String {
  val normalized = ((bearing % 360) + 360) % 360
  return when {
    normalized >= 337.5 || normalized < 22.5 -> "Utara"
    normalized < 67.5 -> "Timur Laut"
    normalized < 112.5 -> "Timur"
    normalized < 157.5 -> "Tenggara"
    normalized < 202.5 -> "Selatan"
    normalized < 247.5 -> "Barat Daya"
    normalized < 292.5 -> "Barat"
    else -> "Barat Laut"
  }
}
