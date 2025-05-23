package com.github.sceneren.featurea.camera.rotate

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import kotlin.math.abs

@Composable
fun RealPhysicalScreenRotationMonitor(onRotation: (Float) -> Unit) {
    val context = LocalContext.current
    val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

    var physicalOrientation by remember { mutableStateOf(DevicePhysicalOrientation(0f, 0f, 0f)) }
    var detectedRotation by remember { mutableStateOf(PhysicalScreenRotation.UNKNOWN) }
    var sensorAvailability by remember { mutableStateOf(false) }

    // 传感器监听器
    val rotationVectorEventListener = remember {
        object : SensorEventListener {
            private val rotationMatrix = FloatArray(9)
            private val orientationAngles = FloatArray(3) // Azimuth, Pitch, Roll

            override fun onSensorChanged(event: SensorEvent) {
                if (event.sensor.type == Sensor.TYPE_ROTATION_VECTOR) {
                    SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values)
                    SensorManager.getOrientation(rotationMatrix, orientationAngles)

                    val currentPitch = Math.toDegrees(orientationAngles[1].toDouble()).toFloat()
                    val currentRoll = Math.toDegrees(orientationAngles[2].toDouble()).toFloat()
                    val currentYaw = Math.toDegrees(orientationAngles[0].toDouble()).toFloat()

                    physicalOrientation =
                        DevicePhysicalOrientation(currentYaw, currentPitch, currentRoll)

                    // 根据俯仰和翻滚角度判断物理屏幕方向
                    detectedRotation = determinePhysicalScreenRotation(currentPitch, currentRoll)
                    onRotation.invoke(detectedRotation.degrees.toFloat())
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
                // 可选：处理传感器精度变化
            }
        }
    }

    DisposableEffect(sensorManager) {
        val rotationVectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)

        if (rotationVectorSensor == null) {
            sensorAvailability = false
            physicalOrientation = DevicePhysicalOrientation(Float.NaN, Float.NaN, Float.NaN)
            detectedRotation = PhysicalScreenRotation.UNKNOWN
            onRotation.invoke(detectedRotation.degrees.toFloat())
        } else {
            sensorAvailability = true
            sensorManager.registerListener(
                rotationVectorEventListener,
                rotationVectorSensor,
                SensorManager.SENSOR_DELAY_UI
            )
        }

        onDispose {
            sensorManager.unregisterListener(rotationVectorEventListener)
        }
    }
}

// 通常，我们认为设备水平放置（屏幕基本朝上），并且俯仰和翻滚角度在一定范围内时，才进行方向判断。
// 如果俯仰角过大（设备屏幕朝天花板或地面），则判断为未知。
const val FLAT_THRESHOLD_PITCH = 45f // 俯仰角超过此值认为设备非“平稳”放置
const val FLAT_THRESHOLD_ROLL = 45f  // 翻滚角超过此值认为设备非“平稳”放置

// 根据俯仰和翻滚角度判断设备的物理屏幕方向
fun determinePhysicalScreenRotation(pitch: Float, roll: Float): PhysicalScreenRotation {
    // 阈值：需要根据实际情况调整
    // abs(pitch) 小于某个角度，表示设备基本平稳（不倾斜太严重）
    // abs(roll) 接近某个角度，表示左右旋转
    // 我们可以通过检查设备在重力方向上的倾斜来判断它是否接近某个标准方向。


    // 判断是否接近竖屏
    if (abs(pitch) < FLAT_THRESHOLD_PITCH && abs(roll) < FLAT_THRESHOLD_ROLL) {
        return PhysicalScreenRotation.ROTATION_0 // 接近自然竖屏
    }
    // 判断是否接近倒置竖屏 (注意：倒置竖屏时 roll 接近 +/-180)
    if (abs(pitch) < FLAT_THRESHOLD_PITCH && abs(roll) > 135f) { // 135-180度之间
        return PhysicalScreenRotation.ROTATION_180
    }


    // 判断是否接近横屏（roll 接近 +/-90）
    if (abs(pitch) < FLAT_THRESHOLD_PITCH) {
        if (roll > 45f && roll < 135f) { // roll 接近 +90
            return PhysicalScreenRotation.ROTATION_90 // 左横屏 (Home键在左)
        }
        if (roll < -45f && roll > -135f) { // roll 接近 -90
            return PhysicalScreenRotation.ROTATION_270 // 右横屏 (Home键在右)
        }
        if (roll >= -45f && roll <= 45f) {
            return PhysicalScreenRotation.ROTATION_0
        }
        if (roll > 135f || roll < -135f) {
            return PhysicalScreenRotation.ROTATION_180
        }
    }

    return PhysicalScreenRotation.UNKNOWN // 未知方向（例如，屏幕朝天花板或地面，或者介于两者之间）
}