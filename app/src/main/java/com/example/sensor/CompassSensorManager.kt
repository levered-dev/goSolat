package com.example.sensor

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.abs

data class CompassState(
  val azimuth: Float = 0f, // 0..360 degrees
  val pitch: Float = 0f,   // tilt forward/back in degrees
  val roll: Float = 0f,    // tilt left/right in degrees
  val accuracy: Int = SensorManager.SENSOR_STATUS_ACCURACY_HIGH
)

class CompassSensorManager(context: Context) : SensorEventListener {

  private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
  private val rotationVectorSensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
  private val accelerometerSensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
  private val magneticFieldSensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)

  private val _compassState = MutableStateFlow(CompassState())
  val compassState: StateFlow<CompassState> = _compassState.asStateFlow()

  private val rotationMatrix = FloatArray(9)
  private val orientationAngles = FloatArray(3)
  private val lastAccelerometer = FloatArray(3)
  private val lastMagnetometer = FloatArray(3)
  private var isAccelerometerSet = false
  private var isMagnetometerSet = false

  private var smoothedAzimuth = 0f

  fun startListening() {
    if (rotationVectorSensor != null) {
      sensorManager.registerListener(this, rotationVectorSensor, SensorManager.SENSOR_DELAY_UI)
    } else {
      accelerometerSensor?.let {
        sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
      }
      magneticFieldSensor?.let {
        sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
      }
    }
  }

  fun stopListening() {
    sensorManager.unregisterListener(this)
    isAccelerometerSet = false
    isMagnetometerSet = false
  }

  override fun onSensorChanged(event: SensorEvent) {
    if (event.sensor.type == Sensor.TYPE_ROTATION_VECTOR) {
      SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values)
      SensorManager.getOrientation(rotationMatrix, orientationAngles)
      updateOrientation(orientationAngles, event.accuracy)
    } else if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
      System.arraycopy(event.values, 0, lastAccelerometer, 0, event.values.size)
      isAccelerometerSet = true
      computeOrientationFallback(event.accuracy)
    } else if (event.sensor.type == Sensor.TYPE_MAGNETIC_FIELD) {
      System.arraycopy(event.values, 0, lastMagnetometer, 0, event.values.size)
      isMagnetometerSet = true
      computeOrientationFallback(event.accuracy)
    }
  }

  private fun computeOrientationFallback(accuracy: Int) {
    if (isAccelerometerSet && isMagnetometerSet) {
      if (SensorManager.getRotationMatrix(rotationMatrix, null, lastAccelerometer, lastMagnetometer)) {
        SensorManager.getOrientation(rotationMatrix, orientationAngles)
        updateOrientation(orientationAngles, accuracy)
      }
    }
  }

  private fun updateOrientation(angles: FloatArray, accuracy: Int) {
    var rawAzimuth = Math.toDegrees(angles[0].toDouble()).toFloat()
    if (rawAzimuth < 0f) rawAzimuth += 360f

    val pitch = Math.toDegrees(angles[1].toDouble()).toFloat()
    val roll = Math.toDegrees(angles[2].toDouble()).toFloat()

    // Smooth azimuth around 0/360 boundary
    val diff = rawAzimuth - smoothedAzimuth
    val shortestAngle = (diff + 180f) % 360f - 180f
    smoothedAzimuth = (smoothedAzimuth + shortestAngle * 0.25f + 360f) % 360f

    _compassState.value = CompassState(
      azimuth = smoothedAzimuth,
      pitch = pitch,
      roll = roll,
      accuracy = accuracy
    )
  }

  override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    _compassState.value = _compassState.value.copy(accuracy = accuracy)
  }
}
