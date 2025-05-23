package com.github.sceneren.featurea.camera

import android.graphics.Bitmap
import androidx.camera.core.SurfaceRequest

data class CameraXState(
    val surfaceRequest: SurfaceRequest? = null,
    val isFront: Boolean = false,
    val isRecording: Boolean = false,
    val currentTime: String = System.currentTimeMillis().timestampToFormattedDate(),
    val waterMarkBitmap: Bitmap? = null,
    val deviceRotation: Float = 0f
)

sealed class CameraXEffect {
    data object Test : CameraXEffect()
}