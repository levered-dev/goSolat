package com.example.ui

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.location.Location
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.AzanAudioManager
import com.example.audio.AzanRepository
import com.example.data.HadithRepository
import com.example.data.PrayerPreferences
import com.example.model.*
import com.example.prayer.HijriCalendarHelper
import com.example.prayer.MalaysiaZones
import com.example.prayer.PrayerCalculator
import com.example.prayer.PrayerNotificationHelper
import com.example.prayer.QiblaCalculator
import com.example.sensor.CompassSensorManager
import com.example.weather.WeatherRepository
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class MainViewModel(application: Application) : AndroidViewModel(application) {

  private val context: Context get() = getApplication()
  val prefs = PrayerPreferences(context)
  val audioManager = AzanAudioManager(context)
  private val compassManager = CompassSensorManager(context)
  private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

  // Clock state
  private val _currentTimeStr = MutableStateFlow("")
  val currentTimeStr: StateFlow<String> = _currentTimeStr.asStateFlow()

  private val _currentDateStr = MutableStateFlow("")
  val currentDateStr: StateFlow<String> = _currentDateStr.asStateFlow()

  private val _currentHijriDateStr = MutableStateFlow("")
  val currentHijriDateStr: StateFlow<String> = _currentHijriDateStr.asStateFlow()

  // Selected Prayer Zone
  private val _selectedZone = MutableStateFlow(MalaysiaZones.findZoneByCode(prefs.selectedZoneCode))
  val selectedZone: StateFlow<PrayerZone> = _selectedZone.asStateFlow()

  // Real-time device GPS Location
  private val _realTimeLocation = MutableStateFlow<Location?>(null)
  val realTimeLocation: StateFlow<Location?> = _realTimeLocation.asStateFlow()

  // Prayer Schedule & Countdown
  private val _schedule = MutableStateFlow(
    PrayerCalculator.calculateSchedule(_selectedZone.value)
  )
  val schedule: StateFlow<PrayerSchedule> = _schedule.asStateFlow()

  private val _nextPrayer = MutableStateFlow(
    PrayerCalculator.calculateNextPrayer(_schedule.value)
  )
  val nextPrayer: StateFlow<NextPrayerInfo> = _nextPrayer.asStateFlow()

  // Weather state
  private val _weather = MutableStateFlow(WeatherData(locationName = _selectedZone.value.name))
  val weather: StateFlow<WeatherData> = _weather.asStateFlow()

  private val _isWeatherLoading = MutableStateFlow(false)
  val isWeatherLoading: StateFlow<Boolean> = _isWeatherLoading.asStateFlow()

  // Hadith state
  private val _todayHadith = MutableStateFlow(HadithRepository.getTodayHadith())
  val todayHadith: StateFlow<Hadith> = _todayHadith.asStateFlow()

  // Compass & Qibla state
  val compassState = compassManager.compassState

  val qiblaBearing: StateFlow<Double> = _selectedZone.map { zone ->
    QiblaCalculator.calculateQiblaBearing(zone.latitude, zone.longitude)
  }.stateIn(viewModelScope, SharingStarted.Eagerly, 292.0)

  val distanceToKaabaKm: StateFlow<Double> = _selectedZone.map { zone ->
    QiblaCalculator.calculateDistanceToKaabaKm(zone.latitude, zone.longitude)
  }.stateIn(viewModelScope, SharingStarted.Eagerly, 7150.0)

  // Azan List & Selection
  private val _azanList = MutableStateFlow<List<AzanSound>>(emptyList())
  val azanList: StateFlow<List<AzanSound>> = _azanList.asStateFlow()

  private val _defaultAzanId = MutableStateFlow(prefs.defaultAzanId)
  val defaultAzanId: StateFlow<String> = _defaultAzanId.asStateFlow()

  private val _subuhAzanId = MutableStateFlow(prefs.subuhAzanId)
  val subuhAzanId: StateFlow<String> = _subuhAzanId.asStateFlow()

  private val _azanVolume = MutableStateFlow(prefs.azanVolume)
  val azanVolume: StateFlow<Float> = _azanVolume.asStateFlow()

  private val _enabledPrayers = MutableStateFlow(prefs.getEnabledPrayers())
  val enabledPrayers: StateFlow<Set<PrayerType>> = _enabledPrayers.asStateFlow()

  // Calendar state
  private val _selectedCalendarYear = MutableStateFlow(Calendar.getInstance().get(Calendar.YEAR))
  val selectedCalendarYear: StateFlow<Int> = _selectedCalendarYear.asStateFlow()

  private val _selectedCalendarMonth = MutableStateFlow(Calendar.getInstance().get(Calendar.MONTH))
  val selectedCalendarMonth: StateFlow<Int> = _selectedCalendarMonth.asStateFlow()

  private var timerJob: Job? = null

  init {
    val validIds = setOf("makkah_ali_mulla", "madinah_surayhi")
    if (prefs.defaultAzanId !in validIds) {
      prefs.defaultAzanId = "makkah_ali_mulla"
      _defaultAzanId.value = "makkah_ali_mulla"
    }
    if (prefs.subuhAzanId !in validIds) {
      prefs.subuhAzanId = "madinah_surayhi"
      _subuhAzanId.value = "madinah_surayhi"
    }
    refreshAzanList()
    startClockAndCountdown()
    refreshWeather()
    rescheduleNotifications()
    fetchCurrentLocation()
  }

  @SuppressLint("MissingPermission")
  fun fetchCurrentLocation(onLocationReceived: ((Location) -> Unit)? = null) {
    try {
      fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
        if (location != null) {
          _realTimeLocation.value = location
          onLocationReceived?.invoke(location)
        }
      }
      val cts = CancellationTokenSource()
      fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, cts.token)
        .addOnSuccessListener { location: Location? ->
          if (location != null) {
            _realTimeLocation.value = location
            onLocationReceived?.invoke(location)
          }
        }
    } catch (e: Exception) {
      // Location permission not yet granted or GPS unavailable
    }
  }

  fun startCompass() {
    compassManager.startListening()
  }

  fun stopCompass() {
    compassManager.stopListening()
  }

  private fun startClockAndCountdown() {
    timerJob?.cancel()
    timerJob = viewModelScope.launch {
      val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
      val dateFormat = SimpleDateFormat("EEEE, d MMMM yyyy", Locale("ms", "MY"))

      while (true) {
        val now = Date()
        val cal = Calendar.getInstance()
        _currentTimeStr.value = timeFormat.format(now)
        _currentDateStr.value = dateFormat.format(now)
        _currentHijriDateStr.value = HijriCalendarHelper.getHijriDateString(cal)

        // Update countdown
        _nextPrayer.value = PrayerCalculator.calculateNextPrayer(_schedule.value, System.currentTimeMillis())

        delay(1000)
      }
    }
  }

  fun setZone(zone: PrayerZone) {
    _selectedZone.value = zone
    prefs.selectedZoneCode = zone.code
    _schedule.value = PrayerCalculator.calculateSchedule(zone)
    _nextPrayer.value = PrayerCalculator.calculateNextPrayer(_schedule.value)
    refreshWeather()
    rescheduleNotifications()
  }

  fun refreshWeather() {
    viewModelScope.launch {
      _isWeatherLoading.value = true
      val zone = _selectedZone.value
      val result = WeatherRepository.fetchWeather(zone.latitude, zone.longitude, zone.name)
      _weather.value = result
      _isWeatherLoading.value = false
    }
  }

  @SuppressLint("MissingPermission")
  fun detectLocationAndSetZone(onSuccess: (PrayerZone) -> Unit, onFailure: () -> Unit) {
    try {
      fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
        if (location != null) {
          _realTimeLocation.value = location
          val closest = MalaysiaZones.findClosestZone(location.latitude, location.longitude)
          setZone(closest)
          onSuccess(closest)
        } else {
          onFailure()
        }
      }.addOnFailureListener {
        onFailure()
      }
    } catch (e: Exception) {
      onFailure()
    }
  }

  fun refreshAzanList() {
    val presets = AzanRepository.PRESET_AZANS.map { azan ->
      val downloaded = AzanRepository.isAzanDownloaded(context, azan.id)
      val localFile = if (downloaded) AzanRepository.getLocalAudioFile(context, azan.id).absolutePath else null
      azan.copy(isDownloaded = downloaded, localFilePath = localFile)
    }
    _azanList.value = presets
  }

  fun downloadAzanAudio(azan: AzanSound) {
    viewModelScope.launch {
      val success = audioManager.downloadAzan(azan)
      if (success) {
        refreshAzanList()
      }
    }
  }

  fun deleteAzanAudio(azanId: String) {
    audioManager.deleteDownloadedAzan(azanId)
    refreshAzanList()
  }

  fun playAzanPreview(azan: AzanSound) {
    audioManager.playAzan(azan, volume = _azanVolume.value)
  }

  fun stopAzanPreview() {
    audioManager.stopPlayback()
  }

  fun setDefaultAzan(azanId: String) {
    _defaultAzanId.value = azanId
    prefs.defaultAzanId = azanId
  }

  fun setSubuhAzan(azanId: String) {
    _subuhAzanId.value = azanId
    prefs.subuhAzanId = azanId
  }

  fun setAzanVolume(volume: Float) {
    _azanVolume.value = volume
    prefs.azanVolume = volume
  }

  fun togglePrayerNotification(type: PrayerType) {
    val current = _enabledPrayers.value.toMutableSet()
    val isEnabled = current.contains(type)
    if (isEnabled) {
      current.remove(type)
      prefs.setPrayerNotificationEnabled(type, false)
    } else {
      current.add(type)
      prefs.setPrayerNotificationEnabled(type, true)
    }
    _enabledPrayers.value = current
    rescheduleNotifications()
  }

  fun rescheduleNotifications() {
    PrayerNotificationHelper.scheduleAlarms(context, _schedule.value, _enabledPrayers.value)
  }

  fun nextHadith() {
    val list = HadithRepository.HADITH_COLLECTION
    val currentIndex = list.indexOfFirst { it.id == _todayHadith.value.id }
    val nextIndex = if (currentIndex >= 0 && currentIndex < list.size - 1) currentIndex + 1 else 0
    _todayHadith.value = list[nextIndex]
  }

  fun setCalendarMonth(year: Int, month: Int) {
    _selectedCalendarYear.value = year
    _selectedCalendarMonth.value = month
  }

  fun addCustomYoutubeAzan(title: String, youtubeUrlOrId: String) {
    val cleanId = youtubeUrlOrId.substringAfterLast("/").substringAfter("v=")
    val newAzan = AzanSound(
      id = "custom_" + System.currentTimeMillis(),
      title = title.ifBlank { "Azan Pilihan YouTube" },
      muazzin = "Rakaman Pilihan Pengguna",
      origin = "YouTube Stream",
      youtubeId = cleanId,
      audioUrl = "https://cdn.islamic.network/prayer-times/audio/azan-custom.mp3",
      duration = "3:30",
      isSpecialSubuh = false
    )
    _azanList.value = _azanList.value + newAzan
  }

  override fun onCleared() {
    super.onCleared()
    stopCompass()
    audioManager.stopPlayback()
    timerJob?.cancel()
  }
}
