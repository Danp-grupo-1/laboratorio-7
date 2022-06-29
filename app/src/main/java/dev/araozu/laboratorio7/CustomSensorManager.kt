package dev.araozu.laboratorio7

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log

typealias SensorCallback = (Float, Float, Float) -> Unit

class CustomSensorManager(private val sensorManager: SensorManager) : SensorEventListener {
    private val rotationMatrix = FloatArray(9)
    private val mOrientationAngles = FloatArray(3)
    private val accelerometerReading = FloatArray(3)
    private val magnetometerReading = FloatArray(3)
    private var accelerometerCallback: SensorCallback? = null
    private var magnetometerCallback: SensorCallback? = null
    private var orientationCallback: ((String) -> Unit)? = null

    init {
        setUpSensor()
    }

    fun registerAccelerometerCallback(fn: SensorCallback) {
        accelerometerCallback = fn
    }

    fun registerMagnetometerCallback(fn: SensorCallback) {
        magnetometerCallback = fn
    }

    fun registerOrientationCallback(fn: (String) -> Unit) {
        orientationCallback = fn
    }

    private fun setUpSensor() {
        // Listen to Accelerometer and magnetic field
        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)?.also { accelerometer ->
            sensorManager.registerListener(
                this,
                accelerometer,
                SensorManager.SENSOR_DELAY_NORMAL,
                SensorManager.SENSOR_DELAY_UI
            )
        }
        sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)?.also { magneticField ->
            sensorManager.registerListener(
                this,
                magneticField,
                SensorManager.SENSOR_DELAY_NORMAL,
                SensorManager.SENSOR_DELAY_UI
            )
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        // Checks for the sensor we have registered
        if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
            System.arraycopy(event.values, 0, accelerometerReading, 0, accelerometerReading.size)

            // Call the accelerometer callback to inform the UI
            accelerometerCallback?.invoke(event.values[0], event.values[1], event.values[2])

            if (accelerometerReading[0].toInt() == 0 && accelerometerReading[1].toInt() == 0) {
                Log.e("pos", "plane")
                orientationCallback?.invoke("Plano")
            } else {
                if (accelerometerReading[0].toInt() < 0) {
                    Log.e("pos", "Horizontal 1")
                    orientationCallback?.invoke("Horizontal 1")
                } else if (accelerometerReading[0].toInt() > 0) {
                    Log.e("pos", "Horizontal 2")
                    orientationCallback?.invoke("Horizontal 2")
                } else if (accelerometerReading[1].toInt() > 0) {
                    Log.e("pos", "Vertical 1")
                    orientationCallback?.invoke("Vertical 1")
                } else if (accelerometerReading[1].toInt() < 0) {
                    Log.e("pos", "Vertical 2")
                    orientationCallback?.invoke("Vertical 2")
                }
            }
            updateOrientationAngles()
        } else if (event?.sensor?.type == Sensor.TYPE_MAGNETIC_FIELD) {
            System.arraycopy(event.values, 0, magnetometerReading, 0, magnetometerReading.size)

            // Call the magnetometer callback to inform the UI
            magnetometerCallback?.invoke(event.values[0], event.values[1], event.values[2])
        }
    }

    private fun updateOrientationAngles() {
        val pi = 22 / 7
        SensorManager.getRotationMatrix(
            rotationMatrix,
            null,
            accelerometerReading,
            magnetometerReading
        )

        SensorManager.getOrientation(rotationMatrix, mOrientationAngles)
        /*
        // angle of rotation about the -z axis
        Log.e("angle", "Azimuth R" + mOrientationAngles[0])
        // angle of rotation about the x axis
        Log.e("angle", "Pitch R" + mOrientationAngles[1])
        // angle of rotation about the y axis
        Log.e("angle", "Roll R" + mOrientationAngles[2])

        // all in rad

        Log.e("angle", "Azimuth G" + mOrientationAngles[0] * (180 / pi))
        // angle of rotation about the x axis
        Log.e("angle", "Pitch  G" + mOrientationAngles[1] * (180 / pi))
        // angle of rotation about the y axis
        Log.e("angle", "Roll  G" + mOrientationAngles[2] * (180 / pi))
         */
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    fun unregister() {
        sensorManager.unregisterListener(this)
    }
}