package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.MainViewModel
import com.example.ui.components.*
import com.example.ui.theme.*

enum class AppTab(val title: String) {
  SOLAT("Waktu Solat"),
  MASJID("Masjid & Surau"),
  KIBLAT("Arah Kiblat"),
  KALENDAR("Kalendar"),
  AZAN("Bunyi Azan")
}

enum class QiblaViewMode {
  COMPASS,
  MAP,
  AR_CAMERA
}

class MainActivity : ComponentActivity() {

  private val viewModel: MainViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    setContent {
      MyApplicationTheme {
        MainAppContent(viewModel = viewModel)
      }
    }
  }

  override fun onResume() {
    super.onResume()
    viewModel.startCompass()
  }

  override fun onPause() {
    super.onPause()
    viewModel.stopCompass()
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppContent(viewModel: MainViewModel) {
  val context = LocalContext.current
  var currentTab by remember { mutableStateOf(AppTab.SOLAT) }
  var qiblaViewMode by remember { mutableStateOf(QiblaViewMode.COMPASS) }
  var showZonePicker by remember { mutableStateOf(false) }

  // State collection from ViewModel
  val timeStr by viewModel.currentTimeStr.collectAsStateWithLifecycle()
  val dateStr by viewModel.currentDateStr.collectAsStateWithLifecycle()
  val hijriDateStr by viewModel.currentHijriDateStr.collectAsStateWithLifecycle()
  val selectedZone by viewModel.selectedZone.collectAsStateWithLifecycle()
  val schedule by viewModel.schedule.collectAsStateWithLifecycle()
  val nextPrayer by viewModel.nextPrayer.collectAsStateWithLifecycle()
  val weather by viewModel.weather.collectAsStateWithLifecycle()
  val isWeatherLoading by viewModel.isWeatherLoading.collectAsStateWithLifecycle()
  val todayHadith by viewModel.todayHadith.collectAsStateWithLifecycle()

  val compassState by viewModel.compassState.collectAsStateWithLifecycle()
  val qiblaBearing by viewModel.qiblaBearing.collectAsStateWithLifecycle()
  val distanceKm by viewModel.distanceToKaabaKm.collectAsStateWithLifecycle()

  val azanList by viewModel.azanList.collectAsStateWithLifecycle()
  val defaultAzanId by viewModel.defaultAzanId.collectAsStateWithLifecycle()
  val subuhAzanId by viewModel.subuhAzanId.collectAsStateWithLifecycle()
  val playingAzanId by viewModel.audioManager.playingAzanId.collectAsStateWithLifecycle()
  val isPlaying by viewModel.audioManager.isPlaying.collectAsStateWithLifecycle()
  val downloadProgress by viewModel.audioManager.downloadProgress.collectAsStateWithLifecycle()
  val azanVolume by viewModel.azanVolume.collectAsStateWithLifecycle()
  val enabledPrayers by viewModel.enabledPrayers.collectAsStateWithLifecycle()

  val calendarYear by viewModel.selectedCalendarYear.collectAsStateWithLifecycle()
  val calendarMonth by viewModel.selectedCalendarMonth.collectAsStateWithLifecycle()
  val realTimeLocation by viewModel.realTimeLocation.collectAsStateWithLifecycle()

  // Refresh real-time GPS location when navigating to Masjid map tab
  LaunchedEffect(currentTab) {
    if (currentTab == AppTab.MASJID) {
      viewModel.fetchCurrentLocation()
    }
  }

  // Permission launcher for Android 13+ Notification permission
  val notifPermissionLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.RequestPermission()
  ) { /* Handled */ }

  // Permission launcher for Precise GPS Location
  val locationPermissionLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.RequestMultiplePermissions()
  ) { permissions ->
    val isGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
      permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
    if (isGranted) {
      viewModel.fetchCurrentLocation()
    }
  }

  LaunchedEffect(Unit) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
      if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
        notifPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
      }
    }
  }

  // If in AR mode, show full screen AR HUD
  if (currentTab == AppTab.KIBLAT && qiblaViewMode == QiblaViewMode.AR_CAMERA) {
    QiblaArView(
      compassState = compassState,
      qiblaBearing = qiblaBearing,
      distanceKm = distanceKm,
      onCloseAr = { qiblaViewMode = QiblaViewMode.COMPASS }
    )
    return
  }

  Scaffold(
    modifier = Modifier.fillMaxSize(),
    bottomBar = {
      NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 3.dp,
        modifier = Modifier.testTag("bottom_navigation_bar")
      ) {
        val navColors = NavigationBarItemDefaults.colors(
          selectedIconColor = GeminiBlue,
          selectedTextColor = GeminiBlue,
          indicatorColor = GeminiLightPrimaryContainer,
          unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
          unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
        )

        NavigationBarItem(
          selected = currentTab == AppTab.SOLAT,
          onClick = { currentTab = AppTab.SOLAT },
          icon = {
            Icon(
              imageVector = if (currentTab == AppTab.SOLAT) Icons.Filled.AccessTime else Icons.Outlined.AccessTime,
              contentDescription = "Solat"
            )
          },
          label = { Text("Solat", fontSize = 10.sp, fontWeight = if (currentTab == AppTab.SOLAT) FontWeight.Bold else FontWeight.Normal) },
          colors = navColors,
          modifier = Modifier.testTag("tab_solat")
        )

        NavigationBarItem(
          selected = currentTab == AppTab.MASJID,
          onClick = { currentTab = AppTab.MASJID },
          icon = {
            Icon(
              imageVector = if (currentTab == AppTab.MASJID) Icons.Filled.Mosque else Icons.Outlined.Mosque,
              contentDescription = "Masjid"
            )
          },
          label = { Text("Masjid", fontSize = 10.sp, fontWeight = if (currentTab == AppTab.MASJID) FontWeight.Bold else FontWeight.Normal) },
          colors = navColors,
          modifier = Modifier.testTag("tab_masjid")
        )

        NavigationBarItem(
          selected = currentTab == AppTab.KIBLAT,
          onClick = { currentTab = AppTab.KIBLAT },
          icon = {
            Icon(
              imageVector = if (currentTab == AppTab.KIBLAT) Icons.Filled.Explore else Icons.Outlined.Explore,
              contentDescription = "Kiblat"
            )
          },
          label = { Text("Kiblat", fontSize = 10.sp, fontWeight = if (currentTab == AppTab.KIBLAT) FontWeight.Bold else FontWeight.Normal) },
          colors = navColors,
          modifier = Modifier.testTag("tab_kiblat")
        )

        NavigationBarItem(
          selected = currentTab == AppTab.KALENDAR,
          onClick = { currentTab = AppTab.KALENDAR },
          icon = {
            Icon(
              imageVector = if (currentTab == AppTab.KALENDAR) Icons.Filled.CalendarMonth else Icons.Outlined.CalendarMonth,
              contentDescription = "Kalendar"
            )
          },
          label = { Text("Kalendar", fontSize = 10.sp, fontWeight = if (currentTab == AppTab.KALENDAR) FontWeight.Bold else FontWeight.Normal) },
          colors = navColors,
          modifier = Modifier.testTag("tab_kalendar")
        )

        NavigationBarItem(
          selected = currentTab == AppTab.AZAN,
          onClick = { currentTab = AppTab.AZAN },
          icon = {
            Icon(
              imageVector = if (currentTab == AppTab.AZAN) Icons.Filled.MusicNote else Icons.Outlined.MusicNote,
              contentDescription = "Azan"
            )
          },
          label = { Text("Azan", fontSize = 10.sp, fontWeight = if (currentTab == AppTab.AZAN) FontWeight.Bold else FontWeight.Normal) },
          colors = navColors,
          modifier = Modifier.testTag("tab_azan")
        )
      }
    }
  ) { innerPadding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
        .background(MaterialTheme.colorScheme.background)
    ) {
      Crossfade(targetState = currentTab, label = "tab_crossfade") { tab ->
        when (tab) {
          AppTab.SOLAT -> {
            Column(
              modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
              verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
              // 1. Clock, Hijri Date, Live Weather & Zone chip at top
              HeaderClockWeather(
                timeStr = timeStr,
                dateStr = dateStr,
                hijriDateStr = hijriDateStr,
                zone = selectedZone,
                weather = weather,
                isWeatherLoading = isWeatherLoading,
                onZoneClick = { showZonePicker = true },
                onRefreshWeather = { viewModel.refreshWeather() }
              )

              // 2. Next prayer countdown & Prayer Times Schedule
              PrayerScheduleList(
                schedule = schedule,
                nextPrayerInfo = nextPrayer,
                enabledPrayers = enabledPrayers,
                onToggleNotification = { type -> viewModel.togglePrayerNotification(type) }
              )

              // 3. Quick Action Grid (Masjid, Kiblat & Azan YT)
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                // Peta Masjid
                Card(
                  onClick = { currentTab = AppTab.MASJID },
                  shape = RoundedCornerShape(18.dp),
                  colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                  border = androidx.compose.foundation.BorderStroke(1.dp, GeminiBlue.copy(alpha = 0.3f)),
                  elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                  modifier = Modifier
                    .weight(1f)
                    .testTag("quick_action_masjid")
                ) {
                  Column(
                    modifier = Modifier
                      .fillMaxWidth()
                      .padding(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                  ) {
                    Box(
                      modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(GeminiLightPrimaryContainer),
                      contentAlignment = Alignment.Center
                    ) {
                      Icon(
                        imageVector = Icons.Default.Mosque,
                        contentDescription = "Peta Masjid",
                        tint = GeminiBlue,
                        modifier = Modifier.size(20.dp)
                      )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                      text = "Masjid & Surau",
                      fontSize = 12.sp,
                      fontWeight = FontWeight.Bold,
                      color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                      text = "Peta Luar & Arah",
                      fontSize = 9.sp,
                      color = GeminiBlue,
                      fontWeight = FontWeight.Medium
                    )
                  }
                }

                // Kiblat
                Card(
                  onClick = { currentTab = AppTab.KIBLAT },
                  shape = RoundedCornerShape(18.dp),
                  colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                  border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                  elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                  modifier = Modifier
                    .weight(1f)
                    .testTag("quick_action_kiblat")
                ) {
                  Column(
                    modifier = Modifier
                      .fillMaxWidth()
                      .padding(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                  ) {
                    Box(
                      modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                      contentAlignment = Alignment.Center
                    ) {
                      Icon(
                        imageVector = Icons.Default.Explore,
                        contentDescription = "Kiblat",
                        tint = GeminiPurple,
                        modifier = Modifier.size(20.dp)
                      )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                      text = "Kiblat",
                      fontSize = 12.sp,
                      fontWeight = FontWeight.Bold,
                      color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                      text = "Peta & AR",
                      fontSize = 9.sp,
                      color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                  }
                }

                // Azan
                Card(
                  onClick = { currentTab = AppTab.AZAN },
                  shape = RoundedCornerShape(18.dp),
                  colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                  border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                  elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                  modifier = Modifier
                    .weight(1f)
                    .testTag("quick_action_azan")
                ) {
                  Column(
                    modifier = Modifier
                      .fillMaxWidth()
                      .padding(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                  ) {
                    Box(
                      modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                      contentAlignment = Alignment.Center
                    ) {
                      Icon(
                        imageVector = Icons.Default.LibraryMusic,
                        contentDescription = "Azan",
                        tint = GeminiPink,
                        modifier = Modifier.size(20.dp)
                      )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                      text = "Bunyi Azan",
                      fontSize = 12.sp,
                      fontWeight = FontWeight.Bold,
                      color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                      text = "Makkah & Madinah",
                      fontSize = 9.sp,
                      color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                  }
                }
              }

              // 4. Daily Hadith Card with social media sharing
              HadithCard(
                hadith = todayHadith,
                onNextHadith = { viewModel.nextHadith() }
              )

              Spacer(modifier = Modifier.height(16.dp))
            }
          }

          AppTab.MASJID -> {
            val liveLat = realTimeLocation?.latitude ?: selectedZone.latitude
            val liveLon = realTimeLocation?.longitude ?: selectedZone.longitude
            MosqueMapView(
              zone = selectedZone,
              userLatitude = liveLat,
              userLongitude = liveLon,
              isRealLocationActive = realTimeLocation != null,
              onRequestTurnOnLocation = {
                locationPermissionLauncher.launch(
                  arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                  )
                )
              },
              onOpenZonePicker = { showZonePicker = true }
            )
          }

          AppTab.KIBLAT -> {
            when (qiblaViewMode) {
              QiblaViewMode.COMPASS -> {
                QiblaCompassView(
                  compassState = compassState,
                  qiblaBearing = qiblaBearing,
                  distanceKm = distanceKm,
                  zone = selectedZone,
                  onOpenArMode = { qiblaViewMode = QiblaViewMode.AR_CAMERA },
                  onOpenMapView = { qiblaViewMode = QiblaViewMode.MAP }
                )
              }
              QiblaViewMode.MAP -> {
                QiblaMapView(
                  zone = selectedZone,
                  qiblaBearing = qiblaBearing,
                  distanceKm = distanceKm,
                  onBackToCompass = { qiblaViewMode = QiblaViewMode.COMPASS }
                )
              }
              QiblaViewMode.AR_CAMERA -> {
                // Handled in root above
              }
            }
          }

          AppTab.KALENDAR -> {
            IslamicCalendarView(
              year = calendarYear,
              month = calendarMonth,
              onMonthChanged = { y, m -> viewModel.setCalendarMonth(y, m) }
            )
          }

          AppTab.AZAN -> {
            AzanSettingsView(
              azanList = azanList,
              defaultAzanId = defaultAzanId,
              subuhAzanId = subuhAzanId,
              playingAzanId = playingAzanId,
              isPlaying = isPlaying,
              downloadProgress = downloadProgress,
              azanVolume = azanVolume,
              enabledPrayers = enabledPrayers,
              onPlayAzan = { azan -> viewModel.playAzanPreview(azan) },
              onStopAzan = { viewModel.stopAzanPreview() },
              onDownloadAzan = { azan -> viewModel.downloadAzanAudio(azan) },
              onDeleteAzan = { id -> viewModel.deleteAzanAudio(id) },
              onSetDefaultAzan = { id -> viewModel.setDefaultAzan(id) },
              onSetSubuhAzan = { id -> viewModel.setSubuhAzan(id) },
              onVolumeChange = { vol -> viewModel.setAzanVolume(vol) },
              onTogglePrayer = { type -> viewModel.togglePrayerNotification(type) }
            )
          }
        }
      }
    }
  }

  // Zone Picker Modal Bottom Sheet
  if (showZonePicker) {
    ZonePickerSheet(
      currentZone = selectedZone,
      onZoneSelected = { zone -> viewModel.setZone(zone) },
      onDetectGpsLocation = { onSuccess, onFailure ->
        viewModel.detectLocationAndSetZone(onSuccess, onFailure)
      },
      onDismiss = { showZonePicker = false }
    )
  }
}
