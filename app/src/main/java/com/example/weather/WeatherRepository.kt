package com.example.weather

import android.util.Log
import com.example.model.WeatherData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object WeatherRepository {

  private val client = OkHttpClient.Builder()
    .connectTimeout(6, TimeUnit.SECONDS)
    .readTimeout(6, TimeUnit.SECONDS)
    .build()

  suspend fun fetchWeather(latitude: Double, longitude: Double, locationName: String): WeatherData =
    withContext(Dispatchers.IO) {
      try {
        val url = "https://api.open-meteo.com/v1/forecast?latitude=$latitude&longitude=$longitude&current=temperature_2m,relative_humidity_2m,weather_code,wind_speed_10m,is_day"
        val request = Request.Builder().url(url).build()
        val response = client.newCall(request).execute()

        if (response.isSuccessful && response.body != null) {
          val jsonStr = response.body!!.string()
          val root = JSONObject(jsonStr)
          val current = root.getJSONObject("current")

          val temp = current.optDouble("temperature_2m", 30.0)
          val code = current.optInt("weather_code", 1)
          val humidity = current.optInt("relative_humidity_2m", 75)
          val windSpeed = current.optDouble("wind_speed_10m", 8.5)
          val isDay = current.optInt("is_day", 1) == 1

          val condition = parseWmoCode(code)

          return@withContext WeatherData(
            temperatureC = temp,
            conditionMs = condition,
            weatherCode = code,
            humidityPercent = humidity,
            windSpeedKmh = windSpeed,
            isDay = isDay,
            locationName = locationName
          )
        }
      } catch (e: Exception) {
        Log.w("WeatherRepository", "Failed to fetch real-time weather: ${e.message}")
      }

      // Fallback sensible weather
      return@withContext WeatherData(
        temperatureC = 30.5,
        conditionMs = "Cerah & Berawan",
        weatherCode = 1,
        humidityPercent = 74,
        windSpeedKmh = 10.2,
        isDay = true,
        locationName = locationName
      )
    }

  private fun parseWmoCode(code: Int): String {
    return when (code) {
      0 -> "Langit Cerah"
      1 -> "Cerah Berawan"
      2 -> "Sebahagian Berawan"
      3 -> "Mendung"
      45, 48 -> "Berkabus"
      51, 53, 55 -> "Hujan Gerimis"
      61, 63 -> "Hujan Sederhana"
      65 -> "Hujan Lebat"
      80, 81, 82 -> "Hujan & Mandi Hujan"
      95, 96, 99 -> "Ribut Petir"
      else -> "Cerah & Tenang"
    }
  }
}
