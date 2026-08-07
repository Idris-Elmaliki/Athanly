package com.example.athanly.qibla.data

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import com.example.athanly.qibla.domain.local.repository.CompassRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

// TYPE_ROTATION_VECTOR (fuses accelerometer + magnetometer + gyroscope) is used instead
// of raw magnetometer readings because it's far less noisy — no manifest permission
// is required for it, unlike location.
class SensorCompassRepository(
    private val sensorManager: SensorManager
) : CompassRepository {

    override fun observedHeadingDegrees(): Flow<Float> = callbackFlow {
        val rotationVectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
        if (rotationVectorSensor == null) {
            // Device has no compass-capable sensor — nothing to observe.
            close(IllegalStateException("No rotation vector sensor available on this device"))
            return@callbackFlow
        }

        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                val rotationMatrix = FloatArray(9)
                SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values)

                val orientationRadians = FloatArray(3)
                SensorManager.getOrientation(rotationMatrix, orientationRadians)

                // orientationRadians[0] is azimuth (rotation around Z) — the only
                // component a compass heading needs; [1]/[2] are pitch/roll (device tilt).
                trySend(headingDegreesFrom(orientationRadians[0]))
            }

            override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) = Unit
        }

        sensorManager.registerListener(listener, rotationVectorSensor, SensorManager.SENSOR_DELAY_UI)

        // callbackFlow's awaitClose runs when the collector's coroutine is cancelled
        // (e.g. QiblaViewModel is cleared) — this is what stops the sensor from
        // draining battery once the Qibla screen is no longer being observed.
        awaitClose { 
            sensorManager.unregisterListener(listener) 
        }
    }

    companion object {
        // Same negative-value wraparound as bearingTo's atan2 result — getOrientation()
        // returns azimuth in -pi..pi radians, but compass headings are 0..360 degrees.
        fun headingDegreesFrom(azimuthRadians: Float): Float {
            return (Math.toDegrees(azimuthRadians.toDouble()).toFloat() + 360) % 360
        }
    }
}