package com.example.examen2.ui.slideshow

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.examen2.R
import com.example.examen2.databinding.FragmentSlideshowBinding

class SlideshowFragment : Fragment() {
    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    private var magnetometer: Sensor? = null

    private var lastAccelerometerReading = FloatArray(3)
    private var lastMagnetometerReading = FloatArray(3)
    private var lastOrientation = FloatArray(3)

    private val orientationAngles = FloatArray(3)
    private lateinit var rotationMatrix: FloatArray

    private lateinit var imageView: ImageView

    private val threshold = Math.PI / 12 // 15 degrees in radians
    private var _binding: FragmentSlideshowBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSlideshowBinding.inflate(inflater, container, false)
        val view = binding.root
        imageView = view.findViewById(R.id.imageView)
        return view
    }

    override fun onResume() {
        super.onResume()
        sensorManager = requireActivity().getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)

        sensorManager.registerListener(
            sensorEventListener,
            accelerometer,
            SensorManager.SENSOR_DELAY_NORMAL
        )
        sensorManager.registerListener(
            sensorEventListener,
            magnetometer,
            SensorManager.SENSOR_DELAY_NORMAL
        )
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(sensorEventListener)
    }

    private val sensorEventListener = object : SensorEventListener {
        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        }

        override fun onSensorChanged(event: SensorEvent) {
            if (event.sensor == accelerometer) {
                System.arraycopy(event.values, 0, lastAccelerometerReading, 0, event.values.size)
            } else if (event.sensor == magnetometer) {
                System.arraycopy(event.values, 0, lastMagnetometerReading, 0, event.values.size)
            }

            updateOrientationAngles()
            updateImageView()
        }
    }

    private fun updateOrientationAngles() {
        rotationMatrix = FloatArray(9)
        SensorManager.getRotationMatrix(
            rotationMatrix,
            null,
            lastAccelerometerReading,
            lastMagnetometerReading
        )
        SensorManager.getOrientation(rotationMatrix, orientationAngles)
    }

    private fun updateImageView() {
        // Check the orientation angles and decide which image to show
        val rollAngle = orientationAngles[2]
        if (rollAngle > threshold) {
            // Show image 1
            imageView.setImageResource(R.drawable.image1)
        } else {
            // Show image 2
            imageView.setImageResource(R.drawable.image2)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
