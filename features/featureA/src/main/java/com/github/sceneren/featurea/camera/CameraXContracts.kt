package com.github.sceneren.featurea.camera

import androidx.camera.core.SurfaceRequest

data class CameraXState(
    val surfaceRequest: SurfaceRequest? = null,
    val isFront: Boolean = false
)

sealed class CameraXEffect {
    data object Test : CameraXEffect()
}