package com.example.internassessmentapp.viewmodel

import android.app.Application
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel

class SensorViewModel(application: Application) : AndroidViewModel(application), SensorEventListener {

    private val sensorManager = application.getSystemService(Context.SENSOR_SERVICE) as SensorManager

    // All available sensors
    private val accelerometer: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    private val gyroscope: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
    private val magneticField: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
    private val light: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)
    private val proximity: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY)
    private val pressureS: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE)
    private val temperatureS: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE)
    private val humidityS: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY)

    // Sensor availability states
    var isAccelerometerAvailable = mutableStateOf(accelerometer != null)
    var isGyroscopeAvailable = mutableStateOf(gyroscope != null)
    var isMagneticFieldAvailable = mutableStateOf(magneticField != null)
    var isLightAvailable = mutableStateOf(light != null)
    var isProximityAvailable = mutableStateOf(proximity != null)
    var isPressureAvailable = mutableStateOf(pressureS != null)
    var isTemperatureAvailable = mutableStateOf(temperatureS != null)
    var isHumidityAvailable = mutableStateOf(humidityS != null)

    // Accelerometer data
    var accelerometerX = mutableFloatStateOf(0f)
    var accelerometerY = mutableFloatStateOf(0f)
    var accelerometerZ = mutableFloatStateOf(0f)

    // Gyroscope data
    var gyroscopeX = mutableFloatStateOf(0f)
    var gyroscopeY = mutableFloatStateOf(0f)
    var gyroscopeZ = mutableFloatStateOf(0f)

    // Magnetic field data
    var magneticX = mutableFloatStateOf(0f)
    var magneticY = mutableFloatStateOf(0f)
    var magneticZ = mutableFloatStateOf(0f)

    // Light sensor data
    var lightLevel = mutableFloatStateOf(0f)

    // Proximity sensor data
    var proximityDistance = mutableFloatStateOf(0f)

    // Pressure sensor data
    var pressure = mutableFloatStateOf(1013.25f) // Standard atmospheric pressure

    // Temperature sensor data
    var temperature = mutableFloatStateOf(25f) // Default room temperature

    // Humidity sensor data
    var humidity = mutableFloatStateOf(50f) // Default humidity

    val activeSensorCount: Int
        get() = listOf(
            isAccelerometerAvailable.value,
            isGyroscopeAvailable.value,
            isMagneticFieldAvailable.value,
            isLightAvailable.value,
            isProximityAvailable.value,
            isPressureAvailable.value,
            isTemperatureAvailable.value,
            isHumidityAvailable.value
        ).count { it }

    fun startListening() {
        // Register all available sensors
        accelerometer?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
        gyroscope?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
        magneticField?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
        light?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
        proximity?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
        pressureS?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
        temperatureS?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
        humidityS?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
    }

    fun stopListening() {
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            when (it.sensor.type) {
                Sensor.TYPE_ACCELEROMETER -> {
                    accelerometerX.floatValue = it.values[0]
                    accelerometerY.floatValue = it.values[1]
                    accelerometerZ.floatValue = it.values[2]
                }
                Sensor.TYPE_GYROSCOPE -> {
                    gyroscopeX.floatValue = it.values[0]
                    gyroscopeY.floatValue = it.values[1]
                    gyroscopeZ.floatValue = it.values[2]
                }
                Sensor.TYPE_MAGNETIC_FIELD -> {
                    magneticX.floatValue = it.values[0]
                    magneticY.floatValue = it.values[1]
                    magneticZ.floatValue = it.values[2]
                }
                Sensor.TYPE_LIGHT -> {
                    lightLevel.floatValue = it.values[0]
                }
                Sensor.TYPE_PROXIMITY -> {
                    proximityDistance.floatValue = it.values[0]
                }
                Sensor.TYPE_PRESSURE -> {
                    pressure.floatValue = it.values[0]
                }
                Sensor.TYPE_AMBIENT_TEMPERATURE -> {
                    temperature.floatValue = it.values[0]
                }
                Sensor.TYPE_RELATIVE_HUMIDITY -> {
                    humidity.floatValue = it.values[0]
                }
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Handle accuracy changes if needed
    }

    override fun onCleared() {
        super.onCleared()
        stopListening()
    }
}