package com.example.athanly.qibla.local.data

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import com.example.athanly.qibla.local.domain.repository.CompassRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

// TYPE_ROTATION_VECTOR (fuses accelerometer + magnetometer + gyroscope) is used instead
// of raw magnetometer readings because it's far less noisy — no manifest permission
// is required for it, unlike location.
class SensorCompassRepository(
    private val sensorManager: SensorManager
) : CompassRepository {
    override fun currentPhoneCompassHeadingDegrees(): Flow<Float> {
        // Turns Android's repeating sensor listener into a Flow: the block runs once someone
        // collects, trySend() emits each reading, and awaitClose (below) cleans up.
        return callbackFlow {
            // Just a handle naming which sensor to subscribe to (registerListener below) — it
            // never delivers data itself. Null if the phone has no such sensor.
            val rotationVectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
            if (rotationVectorSensor == null) {
                // Device has no compass-capable sensor — nothing to observe.
                close(IllegalStateException("No rotation vector sensor available on this device"))
                return@callbackFlow
            }

            // Android calls this by itself on every sensor update (rate set by SENSOR_DELAY_UI
            // below) — we never poll. It only runs once registered via registerListener.
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

                // Required by the interface; intentionally ignored — calibration warnings aren't surfaced yet.
                override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) = Unit
            }

            sensorManager.registerListener(
                listener,
                rotationVectorSensor,
                SensorManager.SENSOR_DELAY_UI
            )

            // callbackFlow's awaitClose runs when the collector's coroutine is cancelled
            // (e.g. QiblaViewModel is cleared) — this is what stops the sensor from
            // draining battery once the Qibla screen is no longer being observed.
            awaitClose {
                sensorManager.unregisterListener(listener)
            }
        }
    }

    companion object {
        // Same negative-value wraparound as bearingTo's atan2 result — getOrientation()
        // returns azimuth in -pi..pi radians, but compass headings are 0..360 degrees.
        fun headingDegreesFrom(azimuthRadians: Float): Float {
            return (Math.toDegrees(azimuthRadians.toDouble()).toFloat() + 360) % 360
        }

        // The sensor measures from magnetic north, but the API's Qibla bearing is from true
        // north — adding the local declination makes both share the same zero.
        fun trueHeadingFrom(magneticHeadingDegrees: Float, declinationDegrees: Float): Float {
            // % alone keeps the sign of the left side (-5 % 360 = -5), so the second
            // + 360 % 360 lifts negatives back into 0..360.
            return (((magneticHeadingDegrees + declinationDegrees) % 360) + 360) % 360
        }
    }
}