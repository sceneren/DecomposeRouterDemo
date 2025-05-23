package com.github.sceneren.featurea.camera.rotate

// 定义一个数据类来存储欧拉角
data class DevicePhysicalOrientation(
    val yaw: Float,   // 偏航角 (绕Z轴，方位角)
    val pitch: Float, // 俯仰角 (绕X轴，前后倾斜)
    val roll: Float   // 翻滚角 (绕Y轴，左右倾斜)
)

// 定义屏幕的真实物理旋转角度枚举
enum class PhysicalScreenRotation(val degrees: Int) {
    ROTATION_0(0),      // 自然竖屏
    ROTATION_90(90),    // 右横屏 (Home键在右)
    ROTATION_180(180),  // 倒置竖屏
    ROTATION_270(270),  // 左横屏 (Home键在左)
    UNKNOWN(0)
}