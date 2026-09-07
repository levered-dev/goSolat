package com.example.prayer

import kotlin.math.*

object QiblaCalculator {
  // Kaaba Holy Sanctuary, Makkah coordinates
  const val KAABA_LATITUDE = 21.422487
  const val KAABA_LONGITUDE = 39.826206

  /**
   * Calculates the true Qibla bearing in degrees clockwise from True North (0°..360°).
   */
  fun calculateQiblaBearing(latitude: Double, longitude: Double): Double {
    val phi1 = Math.toRadians(latitude)
    val phi2 = Math.toRadians(KAABA_LATITUDE)
    val deltaLambda = Math.toRadians(KAABA_LONGITUDE - longitude)

    val y = sin(deltaLambda) * cos(phi2)
    val x = cos(phi1) * sin(phi2) - sin(phi1) * cos(phi2) * cos(deltaLambda)

    var bearing = Math.toDegrees(atan2(y, x))
    bearing = (bearing + 360.0) % 360.0
    return bearing
  }

  /**
   * Calculates the great-circle distance to the Kaaba in kilometers.
   */
  fun calculateDistanceToKaabaKm(latitude: Double, longitude: Double): Double {
    val r = 6371.0 // Earth's mean radius in km
    val phi1 = Math.toRadians(latitude)
    val phi2 = Math.toRadians(KAABA_LATITUDE)
    val deltaPhi = Math.toRadians(KAABA_LATITUDE - latitude)
    val deltaLambda = Math.toRadians(KAABA_LONGITUDE - longitude)

    val a = sin(deltaPhi / 2).pow(2) + cos(phi1) * cos(phi2) * sin(deltaLambda / 2).pow(2)
    val c = 2 * atan2(sqrt(a), sqrt(1 - a))
    return r * c
  }

  /**
   * Check if phone heading is aligned within threshold (e.g. ±3 degrees).
   */
  fun isAligned(currentHeading: Float, qiblaBearing: Double, thresholdDegrees: Float = 3.0f): Boolean {
    val diff = abs(normalizeDegree(currentHeading.toDouble() - qiblaBearing))
    return diff <= thresholdDegrees || diff >= (360.0 - thresholdDegrees)
  }

  private fun normalizeDegree(deg: Double): Double {
    var d = deg % 360.0
    if (d < 0) d += 360.0
    return d
  }
}
